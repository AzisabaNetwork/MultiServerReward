package com.github.aburaagetarou.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.MultiServerReward;

/**
 * 報酬コマンド定義の設定クラス
 * @author AburaAgeTarou
 */
public class StoredCommandConfig {
    
    // コマンド定義
    // K=設定キー, V=コマンド
    private static Map<String, String> commands;

    // 設定ファイル
    private static final File file;
    static {
        File config = new File(MSRConfig.getSyncDataDir(), "stored-command.yml");
        if(!config.exists()) {
            config = new File(MultiServerReward.getInstance().getDataFolder(), "stored-command.yml");
        }
        file = config;
    }

    /**
     * コマンド設定読み込み
     * @param file ファイル
     */
    public static void loadConfig(File file) {

        // クリア
        commands = new HashMap<>();
        
        // ファイルが存在しない場合、警告を出力
        if(!file.exists()) {
            MultiServerReward.getInstance().getLogger().warning("コマンド定義ファイル「stored-command.yml」が存在しません。");
            return;
        }

        // ファイルから設定を読み込む
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.load(file);
        }
        catch(Exception e) {
            MultiServerReward.getInstance().getLogger().warning("コマンド定義ファイル「stored-command.yml」の読み込みに失敗しました。");
            e.printStackTrace();
            return;
        }

        // コマンド定義を取得
        for(String key : yml.getKeys(false)) {
            commands.put(key, yml.getString(key));
        }
    }
    public static void loadConfig() {
        loadConfig(file);
    }

    /**
     * キー文字列からコマンドを取得
     * @param key キー
     * @return コマンド
     */
    public static String getCommand(String key) {
        return commands.get(key);
    }
}
