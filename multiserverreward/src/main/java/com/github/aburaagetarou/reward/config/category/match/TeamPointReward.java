package com.github.aburaagetarou.reward.config.category.match;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.reward.config.category.RewardTrigger;
import com.github.aburaagetarou.reward.config.type.IReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 試合中ポイント加算時 クラス
 * @author AburaAgeTarou
 */
public class TeamPointReward extends MatchRewardBase {

    // 報酬設定リスト
    protected static List<MatchRewardBase> allRewards = new ArrayList<>();

    /**
     * 有効な報酬を取得する（ゲームルール指定あり）
     * @param ruleName ゲームルール名（例: "tdm"）。null/空なら通常設定のみ。
     * @return 報酬リスト
     */
    public static List<IReward> getAvailableRewards(String ruleName) {
        List<IReward> list = new ArrayList<>();
        for(MatchRewardBase reward : allRewards) {
            list.addAll(reward.getRewards());
        }
        list.addAll(ruleRewards(ruleName));
        return list;
    }

    /**
     * 有効な報酬を取得する
     * @return 報酬リスト
     */
    public static List<IReward> getAvailableRewards() {
        return getAvailableRewards(null);
    }

    private static YamlConfiguration loadedYml;

    private static List<IReward> ruleRewards(String ruleName) {
        List<IReward> list = new ArrayList<>();
        if(loadedYml == null) return list;

        list.addAll(MatchRewardBase.loadRuleRewards(loadedYml, null, ruleName, RewardTrigger.TEAM_POINT.getAlias()));

        ConfigurationSection section = loadedYml.getConfigurationSection("limited-time");
        if(section != null) {
            for(String key : section.getKeys(false)) {
                TeamPointReward dummy = new TeamPointReward();
                MatchRewardBase.loadMatchReward(dummy, loadedYml, "limited-time." + key);
                if(!dummy.isAvailable()) continue;

                list.addAll(MatchRewardBase.loadRuleRewards(loadedYml, "limited-time." + key, ruleName, RewardTrigger.TEAM_POINT.getAlias()));
            }
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

        loadedYml = yml;

        // 通常報酬の読み込み
        TeamPointReward reward = new TeamPointReward();
        allRewards.add(MatchRewardBase.loadMatchReward(reward, yml, null));

        // 期間限定報酬の読み込み
        ConfigurationSection section = yml.getConfigurationSection("limited-time");
        if(section != null) {
            for(String key : section.getKeys(false)) {
                reward = new TeamPointReward();
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
        return RewardTrigger.TEAM_POINT.getAlias();
    }
}
