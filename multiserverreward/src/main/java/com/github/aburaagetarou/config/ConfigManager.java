package com.github.aburaagetarou.config;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.reward.config.category.killstreak.AssistStreakLevelReward;
import com.github.aburaagetarou.reward.config.category.killstreak.AssistStreakReward;
import com.github.aburaagetarou.reward.config.category.killstreak.KillStreakLevelReward;
import com.github.aburaagetarou.reward.config.category.killstreak.KillStreakReward;
import com.github.aburaagetarou.reward.config.category.match.MatchEndReward;
import com.github.aburaagetarou.reward.config.category.match.MatchLoseReward;
import com.github.aburaagetarou.reward.config.category.match.MatchWinReward;
import com.github.aburaagetarou.util.MessageUtils;

/**
 * 連携元/先サーバーで行われた設定のリロードを監視するクラス
 * @author AburaAgeTarou
 */
public class ConfigManager {

    // 再読み込み通知ファイルの削除間隔
    private static final long DELETE_INTERVAL = 60L*20L;

    // 前回使用した再読み込み通知ファイルのタイムスタンプ
    private static long lastFileTimeStamp = 0L;

    // リロード監視タスク
    private static BukkitTask task;

    /**
     * リロード監視の開始
     */
    public static void start() {

        // タスクが実行中の場合は停止
        if (task != null) {
            Bukkit.getScheduler().cancelTask(task.getTaskId());
        }

        // 1分ごとに他サーバーからの再読み込み通知を受け取る
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(MultiServerReward.getInstance(), ConfigManager::observe, 0L, DELETE_INTERVAL);
    }
    
    /**
     * 設定の再読み込みを行う
     * @param observe 他サーバーに設定の再読み込みを通知するか
     */
    public static void reload(boolean observe) {

        MessageUtils.broadcastColoredMessage("&c&l設定の再読み込みを行います。");
        MessageUtils.broadcastColoredMessage("&c&lラグが発生する場合があります。");

        // 再読み込みの通知を行う
        if (observe) {
            
            // 連携用ファイルの保存ディレクトリに空のファイルを作成
            // 連携先サーバーはこのファイルの存在を確認して再読み込みを行う
            File file = new File(MSRConfig.getSyncDataDir(), "reload");
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            // 自動更新ファイルのタイムスタンプを更新
            lastFileTimeStamp = file.lastModified();

            // 60秒後にファイルを削除
            Bukkit.getScheduler().runTaskLaterAsynchronously(MultiServerReward.getInstance(), file::delete, DELETE_INTERVAL);
        }

        // コンフィグの読み込み
        MSRConfig.loadConfig();

        // 報酬情報の読み込み
        KillStreakReward.loadAll();
        KillStreakLevelReward.loadAll();
        AssistStreakReward.loadAll();
        AssistStreakLevelReward.loadAll();
        MatchEndReward.loadAll();
        MatchWinReward.loadAll();
        MatchLoseReward.loadAll();

        MessageUtils.broadcastColoredMessage("&a&l設定の再読み込みが完了しました。");
        MessageUtils.broadcastColoredMessage("&a&lご協力ありがとうございました。");
    }

    /**
     * 他サーバーからの再読み込み通知を受け取る
     */
    public static void observe() {

        // 連携用ファイルの保存ディレクトリにファイルが存在する場合
        File file = new File(MSRConfig.getSyncDataDir(), "reload");
        if (file.exists()) {

            // 前回使用した再読み込み通知ファイルのタイムスタンプと異なる場合
            if (lastFileTimeStamp != file.lastModified()) {

                // 再読み込みを行う
                reload(false);
                lastFileTimeStamp = file.lastModified();
            }
        }
    }
}
