package com.github.aburaagetarou.reward.manage;

import java.io.File;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.reward.config.RewardType;
import com.github.aburaagetarou.reward.config.type.IReward;
import com.github.aburaagetarou.reward.config.type.SingleRewardBase;
import com.github.aburaagetarou.reward.config.type.SumRewardBase;
import com.github.aburaagetarou.statistics.IStatisticsTarget;
import com.github.aburaagetarou.statistics.StatisticsUtil;

/**
 * ファイルによる報酬管理クラス
 * @author AburaAgeTarou
 */
public class FileRewardManager extends RewardManager {

    // 報酬情報の自動保存タスク
    private static BukkitTask autoSaveTask;

    /**
     * 報酬情報の自動保存を開始
     */
    public static void startAutoSave() {

        // 実行中のタスクがある場合はキャンセル
        if(autoSaveTask != null) {
            Bukkit.getScheduler().cancelTask(autoSaveTask.getTaskId());
        }

        // タスクの開始
        autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(MultiServerReward.getInstance(), () -> {
            for(RewardManager manager : managers.values()) {
                manager.save(true);
            }
        }, 0L, MSRConfig.getRewardSaveInterval() * 20L);
    }

    /**
     * コンストラクタ
     * @param player 報酬の受取者
     */
    public FileRewardManager(Player player) {
        super(player);
    }

    /**
     * 対象のファイルオブジェクトを取得する
     * @param player プレイヤー
     * @return ファイルオブジェクト
     */
    private File getFile(Player player) {
        String syncDataDir = MSRConfig.getSyncDataDir();
        return new File(syncDataDir, "rewards/" + player.getUniqueId() + ".yml");
    }

    /**
     * 報酬情報を取得する
     */
    @Override
    public List<IReward> load() {

        // 報酬情報の初期化
        List<IReward> rewards = new ArrayList<>();        

        // ファイルオブジェクトの取得
        File file = getFile(player);

        // ファイルが存在しない場合は処理を終了
        if(!file.exists()) {
            return rewards;
        }

        // コンフィグオブジェクトの生成
        YamlConfiguration yml = new YamlConfiguration();

        // ファイルから報酬情報を取得
        try {
            yml.load(file);
            for(String key : yml.getKeys(false)) {
                RewardType type = RewardType.fromAlias(key);
                if(type == null) continue;
                Constructor<? extends IReward> constructor = type.getRewardClass().getConstructor(type.getRewardType());
                for(Object data : yml.getList(key)) {
                    rewards.add(constructor.newInstance(data));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rewards;
    }

    /**
     * 未保存の報酬情報を追記する
     * @param append 追記する場合はtrue
     */
    @Override
    public boolean save(boolean append) {

        // ファイルオブジェクトの取得
        File file = getFile(player);

        // コンフィグオブジェクトの生成
        YamlConfiguration yml = new YamlConfiguration();

        // 追記する場合はファイルを読み込む
        if(append && file.exists()) {
            try {
                yml.load(file);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // 報酬情報をタイプ別に取得
        Map<String, List<String>> rewards = new HashMap<>();
        Map<String, BigDecimal> sumRewards = new HashMap<>();
        for(IReward reward : unsavedRewards) {
            String parentKey = reward.getParentKey();
            if(parentKey == null) continue;

            // 単一報酬の場合
            if(reward instanceof SingleRewardBase) {
                if(!rewards.containsKey(parentKey)) {
                    rewards.put(parentKey, yml.getStringList(parentKey));
                }
                rewards.get(parentKey).add(((SingleRewardBase)reward).serialize());            
            }

            // 合計報酬の場合
            if(reward instanceof SumRewardBase) {
                if(!sumRewards.containsKey(parentKey)) {
                    BigDecimal sum = new BigDecimal("0.0");
                    for(double amount : yml.getDoubleList(parentKey)) {
                        sum = sum.add(new BigDecimal(amount));
                    }
                    sumRewards.put(parentKey, sum);
                }
                sumRewards.put(parentKey, sumRewards.get(parentKey).add(new BigDecimal(((SumRewardBase)reward).getAmount())));
            }

            // 統計対象の場合
            if(reward instanceof IStatisticsTarget) {
                StatisticsUtil.writeData(MultiServerReward.getStatisticsWriter(), (IStatisticsTarget)reward, player);
            }
        }
        for(String key : sumRewards.keySet()) {
            List<Double> list = new ArrayList<>();
            list.add(sumRewards.get(key).doubleValue());
            yml.set(key, list);
        }

        // 報酬情報をファイルに書き込み
        for(String key : rewards.keySet()) {
            yml.set(key, rewards.get(key));
        }
        
        // ファイルに保存
        try {
            yml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 未保存の報酬情報をクリア
        unsavedRewards.clear();

        return true;
    }

    /**
     * 報酬データを削除する
     * @return 削除に成功した場合はtrue
     */
    @Override
    public boolean delete() {
        File file = getFile(player);
        return file.delete();
    }
}
