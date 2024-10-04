package com.github.aburaagetarou.reward.config.universal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;

/**
 * 汎用報酬設定 管理クラス
 */
public class UniversalRewardManager {
    
    // 報酬設定リスト
    private static Map<String, UniversalReward> allRewards = new HashMap<>();

    /**
     * 汎用報酬設定の読み込み
     */
    public static void loadAll() {

        allRewards = new HashMap<>();

        // コンフィグファイルの取得
        File config = new File(MSRConfig.getSyncDataDir(), "universal.yml");

        // コンフィグファイルが存在しない場合
        if(!config.exists()) {

            // plugins内のファイルを読み込み
            config = new File(MultiServerReward.getInstance().getDataFolder(), "universal.yml");
            if(!config.exists()) {
                MultiServerReward.getInstance().getLogger().warning("汎用報酬の設定[universal.yml]が無いよ!!!");
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
        
        // 最上位のキーをテーブル名として報酬設定を読み込み
        for(String key : yml.getKeys(false)) {
            UniversalReward reward = new UniversalReward(key);
            allRewards.put(key, UniversalReward.loadUniversalReward(reward, yml, key));
        }
    }

    /**
     * 指定テーブル名の報酬が登録されているかチェックする
     * @param key テーブル名
     * @return boolean:登録有無
     */
    public static boolean exists(String key) {
        return allRewards.containsKey(key);
    }

    /**
     * 指定テーブルの報酬設定を取得する
     * @param key テーブル名
     * @return 報酬設定
     */
    public static UniversalReward getReward(String key) {
        return allRewards.get(key);
    }
}
