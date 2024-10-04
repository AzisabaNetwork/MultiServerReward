package com.github.aburaagetarou.reward.config.category.killstreak;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.reward.config.RewardType;
import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * キルストリーク報酬設定 クラス
 * @author AburaAgeTarou
 */
public abstract class KillStreakRewardBase {

    /**
     * キルストリーク報酬設定の読み込み
     * @param rewardCfg 報酬設定
     * @param yml 設定ファイル
     * @param node 親ノード(nullの場合はルートノード)
     * @return 報酬設定(rewardCfg)
     */
    public static KillStreakRewardBase loadKillStreakReward(KillStreakRewardBase rewardCfg, YamlConfiguration yml, @Nullable String node) {

        // 報酬リストの初期化
        Map<String, List<IReward>> rewards = new HashMap<>();
        rewardCfg.setRewards(rewards);

        // 配布期間の取得
        String startPath = (node == null ? "start" : node + ".start");
        String endPath = (node == null ? "end" : node + ".end");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if(yml.contains(startPath)) rewardCfg.setStart(sdf.parse(yml.getString(startPath)));
            if(yml.contains(endPath)) rewardCfg.setEnd(sdf.parse(yml.getString(endPath)));
        } catch (Exception e) {
            e.printStackTrace();
            // 続行
        }

        // 報酬を取得
        String path = (node == null ? rewardCfg.getKey() : node + "." + rewardCfg.getKey());
        ConfigurationSection section = yml.getConfigurationSection(path);
        if(section == null) return rewardCfg;
        for(String streak : section.getKeys(false)) {

            // 報酬リストの初期化
            List<IReward> rewardList = new ArrayList<>();
            
            // ストリーク数ごとの報酬を取得
            for(String key : yml.getConfigurationSection(path + "." + streak).getKeys(false)) {

                // 報酬タイプの取得
                RewardType type = RewardType.fromAlias(key);  
                if(type == null) continue;

                // パスの作成
                String rewardPath = path + "." + streak + "." + key;

                // 報酬リストの取得
                try {
                    Constructor<? extends IReward> constructor = type.getRewardClass().getConstructor(type.getRewardType());
                    for(Object data : yml.getList(rewardPath)) {
                        IReward reward = constructor.newInstance(data);
                        rewardList.add(reward);
                    }    
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

            // 報酬リストに設定
            rewards.put(streak, rewardList);
        }

        // 報酬設定に報酬リストを設定
        rewardCfg.setRewards(rewards);
        
        return rewardCfg;
    }

    /**
     * 報酬設定のキーを取得する
     * @return キー文字列
     */
    public abstract String getKey();
    
    // 配布期間
    private Date start = new Date(0L);
    private Date end = new Date(Long.MAX_VALUE);

    // 報酬リスト
    protected Map<String, List<IReward>> rewards;

    /**
     * 配布期間(開始)を設定する
     * @param start 開始日時
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * 配布期間(終了)を設定する
     * @param end 終了日時
     */
    public void setEnd(Date end) {
        this.end = end;
    }
    
    /**
     * キーから報酬を取得する
     * @return 報酬リスト
     */
    public List<IReward> getReward(String key) {
        
        // 配布期間外の場合はnullを返す
        if(!isAvailable()) return null;

        return rewards.get(key);
    }

    /**
     * 報酬を設定する
     * @param rewards 報酬リスト
     */
    public void setRewards(Map<String, List<IReward>> rewards) {
        this.rewards = rewards;
    }

    /**
     * 配布期間内かどうかを判定する
     * @return 配布期間内の場合はtrue
     */
    public boolean isAvailable() {
        Date now = new Date();
        return start.before(now) && end.after(now);
    }
}
