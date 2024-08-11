package com.github.aburaagetarou.reward.config.category.killstreak;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.reward.config.category.RewardTrigger;
import com.github.aburaagetarou.reward.config.category.RewardUtil;
import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * 倍数アシストストリーク報酬設定 クラス
 * @author AburaAgeTarou
 */
public class AssistStreakLevelReward extends KillStreakRewardBase {

    // 報酬設定リスト
    protected static List<KillStreakRewardBase> allRewards = new ArrayList<>();

    /**
     * キーから報酬を取得する
     * @param key ストリーク数
     * @return 報酬リスト
     */
    public static List<IReward> getAvailableRewards(int streak) {
        List<IReward> list = new ArrayList<>();
        for(KillStreakRewardBase reward : allRewards) {

            // キーを倍数として取得
            for(String key : reward.rewards.keySet()) {
                int multiple = 0;
                try {
                    multiple = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    continue;
                }
                if(multiple > 0 && streak % multiple == 0) {
                    List<IReward> rewardList = reward.getReward(key);
                    if(rewardList != null) list.addAll(rewardList);
                }
            }
        }
        return list;
    }

    /**
     * 全報酬の取得
     * @return 報酬リスト
     */
    public static Map<String, List<IReward>> getAllRewards() {
        Map<String, List<IReward>> all = new HashMap<>();
        for(KillStreakRewardBase reward : allRewards) {
            if(reward.rewards == null) continue;
            for(String key : reward.rewards.keySet()) {
                List<IReward> rewardList = reward.getReward(key);
                if(rewardList != null) all.put(key, rewardList);
            }
        }
        return all;
    }

    /**
     * 全報酬読み込み
     */
    public static void loadAll() {

        // 報酬リストを初期化
        allRewards = new ArrayList<>();

        // コンフィグファイルの取得
        File config = new File(MSRConfig.getSyncDataDir(), "kill-streak.yml");

        // コンフィグファイルが存在しない場合
        if(!config.exists()) {

            // plugins内のファイルを読み込み
            config = new File(MultiServerReward.getInstance().getDataFolder(), "kill-streak.yml");
            if(!config.exists()) {
                MultiServerReward.getInstance().getLogger().warning("キルスト報酬の設定が無いよ!!!");
                return;
            }
        }

        // ファイルから設定読み込み
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.load(config);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        // 通常報酬の読み込み
        AssistStreakLevelReward reward = new AssistStreakLevelReward();
        allRewards.add(RewardUtil.loadKillStreakReward(reward, yml, null));

        // 期間限定報酬の読み込み
        ConfigurationSection section = yml.getConfigurationSection("limited-time-streak");
        if(section != null) {
            for(String key : section.getKeys(false)) {
                reward = new AssistStreakLevelReward();
                allRewards.add(RewardUtil.loadKillStreakReward(reward, yml, "limited-time-streak." + key));
            }
        }
    }

    /**
     * 報酬設定のキーを取得する
     * @return キー文字列
     */
    @Override
    public String getKey() {
        return RewardTrigger.ASSIST_STREAK_LEVEL.getAlias();
    }
}
