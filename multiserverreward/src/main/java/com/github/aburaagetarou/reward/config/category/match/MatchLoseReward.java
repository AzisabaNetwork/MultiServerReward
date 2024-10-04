package com.github.aburaagetarou.reward.config.category.match;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.reward.config.category.RewardTrigger;
import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * 試合の敗北報酬設定 クラス
 * @author AburaAgeTarou
 */
public class MatchLoseReward extends MatchRewardBase {

    // 報酬設定リスト
    protected static List<MatchRewardBase> allRewards = new ArrayList<>();

    /**
     * 有効な報酬を取得する
     * @return 報酬リスト
     */
    public static List<IReward> getAvailableRewards() {
        List<IReward> list = new ArrayList<>();
        for(MatchRewardBase reward : allRewards) {
            list.addAll(reward.getRewards());
        }
        return list;
    }

    /**
     * 全報酬読み込み
     */
    public static void loadAll() {

        // 報酬リストを初期化
        allRewards = new ArrayList<>();

        // コンフィグファイルの取得
        File config = new File(MSRConfig.getSyncDataDir(), "match.yml");

        // コンフィグファイルが存在しない場合
        if(!config.exists()) {

            // plugins内のファイルを読み込み
            config = new File(MultiServerReward.getInstance().getDataFolder(), "match.yml");
            if(!config.exists()) {
                MultiServerReward.getInstance().getLogger().warning("試合報酬の設定が無いよ!!!");
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
        MatchLoseReward reward = new MatchLoseReward();
        allRewards.add(MatchRewardBase.loadMatchReward(reward, yml, null));

        // 期間限定報酬の読み込み
        ConfigurationSection section = yml.getConfigurationSection("limited-time");
        if(section != null) {
            for(String key : section.getKeys(false)) {
                reward = new MatchLoseReward();
                allRewards.add(MatchRewardBase.loadMatchReward(reward, yml, "limited-time." + key));
            }
        }
    }
    
    /**
     * 報酬設定のキーを取得する
     * @return 報酬設定のキー
     */
    @Override
    public String getKey() {
        return RewardTrigger.MATCH_LOSE.getAlias();
    }
}
