package com.github.aburaagetarou.reward.config.category;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.reward.config.RewardType;
import com.github.aburaagetarou.reward.config.category.killstreak.KillStreakRewardBase;
import com.github.aburaagetarou.reward.config.category.match.MatchRewardBase;
import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * 報酬のユーティリティクラス
 * @author AburaAgeTarou
 */
public class RewardUtil {

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
     * 試合報酬設定の読み込み
     * @param rewardCfg 報酬設定
     * @param yml 設定ファイル
     * @param node 親ノード(nullの場合はルートノード)
     * @return 報酬設定(rewardCfg)
     */
    public static MatchRewardBase loadMatchReward(MatchRewardBase rewardCfg, YamlConfiguration yml, @Nullable String node) {

        // 報酬リストの初期化
        List<IReward> rewards = new ArrayList<>();
        rewardCfg.setRewards(rewards);

        // 配布期間の取得
        String startPath = (node == null ? rewardCfg.getKey() + ".start" : node + "." + rewardCfg.getKey() + ".start");
        String endPath = (node == null ? rewardCfg.getKey() + ".end" : node + "." + rewardCfg.getKey() + ".end");
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
        for(String key : section.getKeys(false)) {

            // 報酬リストの初期化
            List<IReward> rewardList = new ArrayList<>();

            // 報酬タイプの取得
            RewardType type = RewardType.fromAlias(key);  
            if(type == null) continue;

            // パスの作成
            String rewardPath = path + "." + key;

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

            // 報酬リストに追加
            rewards.addAll(rewardList);
        }

        // 報酬設定に報酬リストを設定
        rewardCfg.setRewards(rewards);
        
        return rewardCfg;
    }
}
