package com.github.aburaagetarou.config;

import java.io.File;

import com.github.aburaagetarou.MultiServerReward;

/**
 * プラグインの設定を管理するクラス
 * @author AburaAgeTarou
 */
public class MSRConfig {
    
    // 保存先ディレクトリパス
    private static String syncDataDir = null;

    // 処理失敗時の退避先サーバー
    private static String escapeServer = null;

    // 通貨報酬の実行コマンド
    private static String balanceRewardCommand = null;

    // インベントリデータの原本フラグ
    private static boolean isOriginal = false;

    // 報酬データの自動保存間隔
    private static long rewardSaveInterval = 0L;

    // インベントリ読み込みタイムアウト時間
    private static long inventoryLoadTimeout = 0L;

    /**
     * コンフィグ読み込み
     */
    public static void loadConfig() {
        MultiServerReward.getInstance().saveDefaultConfig();
        syncDataDir = MultiServerReward.getInstance().getConfig().getString("sync-data-dir");
        balanceRewardCommand = MultiServerReward.getInstance().getConfig().getString("balance-reward-command");
        escapeServer = MultiServerReward.getInstance().getConfig().getString("escape-server");
        isOriginal = MultiServerReward.getInstance().getConfig().getBoolean("original");
        rewardSaveInterval = MultiServerReward.getInstance().getConfig().getLong("reward-save-interval");
        inventoryLoadTimeout = MultiServerReward.getInstance().getConfig().getLong("inventory-load-timeout");

        File file = new File(syncDataDir);
        if(!file.isAbsolute()) {
            File parent = MultiServerReward.getInstance().getDataFolder();
            parent = parent.getParentFile().getParentFile();
            syncDataDir = new File(parent, syncDataDir).getAbsolutePath();
        }
    }

    /**
     * 保存先ディレクトリパスの読み込み
     * @return ディレクトリパスの文字列
     */
    public static String getSyncDataDir() {
        return syncDataDir;
    }

    /**
     * 処理失敗時の退避先サーバーの読み込み
     * @return 退避先サーバー名
     */
    public static String getEscapeServer() {
        return escapeServer;
    }

    /**
     * 通貨報酬の実行コマンドの読み込み
     * @return 実行コマンド
     */
    public static String getBalanceRewardCommand() {
        return balanceRewardCommand;
    }

    /**
     * インベントリデータの原本フラグの読み込み
     * @return インベントリデータの原本フラグ
     */
    public static boolean isOriginal() {
        return isOriginal;
    }

    /**
     * 報酬データの自動保存間隔の読み込み
     * @return 自動保存間隔
     */
    public static long getRewardSaveInterval() {
        return rewardSaveInterval;
    }

    /**
     * インベントリ読み込みタイムアウト時間の読み込み
     * @return タイムアウト時間
     */
    public static long getInventoryLoadTimeout() {
        return inventoryLoadTimeout;
    }
}
