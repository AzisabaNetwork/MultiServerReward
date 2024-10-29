package com.github.aburaagetarou;

import co.aikar.commands.BaseCommand;
import com.github.aburaagetarou.statistics.FileStatisticsWriter;
import com.github.aburaagetarou.statistics.IStatisticsWriter;
import com.github.aburaagetarou.statistics.StatisticsUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.aburaagetarou.command.MSRCommand;
import com.github.aburaagetarou.config.ConfigManager;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.listener.PlayerListener;
import com.github.aburaagetarou.listener.RewardListener;
import com.github.aburaagetarou.reward.manage.FileRewardManager;
import com.github.aburaagetarou.reward.manage.RewardManager;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MultiServerReward メインクラス
 * @author AburaAgeTarou
 */
public class MultiServerReward extends JavaPlugin
{
    // プラグインインスタンス
    private static MultiServerReward instance;

    // コマンドAPI
    private static PaperCommandManager manager;

    // コマンドリスト
    private static final List<BaseCommand> commands = new ArrayList<>();

    // 統計情報の保存クラス
    private static IStatisticsWriter statisticsWriter;

    /**
     * プラグインが有効化されたときの処理
     */
    public void onEnable() {
        instance = this;

        getLogger().info("MultiServerRewardを有効化します。");

        // 設定の読み込み
        ConfigManager.reload(false);

        // 連携サーバーでの設定再読み込みの監視を開始
        ConfigManager.start();

        // 参加中プレイヤーの報酬管理クラスを初期化
        // ※リロード対策
        Bukkit.getOnlinePlayers().forEach(player -> {
            RewardManager.setManager(player, new FileRewardManager(player));
        });

        // イベント登録
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new RewardListener(), this);

        // コマンドAPIの初期化
        manager = new PaperCommandManager(this);

        // brigadierを有効化しろと言われたので有効化
        manager.enableUnstableAPI("brigadier");

        // helpを有効化
        manager.enableUnstableAPI("help");

        // コマンド登録
        manager.registerCommand(new MSRCommand().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return true;
        }), true);

        // 連携先サーバーの場合、報酬データの自動保存を開始
        if(!MSRConfig.isOriginal()) {
            FileRewardManager.startAutoSave();
        }

        // 統計情報の保存クラスを初期化
        String syncDataDir = MSRConfig.getSyncDataDir();
        String statFileName = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        statisticsWriter = new FileStatisticsWriter(new File(syncDataDir, "logs/" + statFileName + ".csv"));

        // Bungeecord連携
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getLogger().info("MultiServerRewardが正常に有効化されました。");
    }

    /**
     * プラグインが無効化されたときの処理
     */
    @Override
    public void onDisable() {
        getLogger().info("MultiServerRewardの終了処理を開始します。");

        // 連携先サーバーの場合は報酬情報を保存
        if(!MSRConfig.isOriginal()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                RewardManager manager = RewardManager.getManager(player);
                if(!manager.save(true)) {
                    getLogger().warning("報酬情報の保存に失敗しました。 (" + player.getName() + ")");
                    getLogger().warning("未保存の報酬情報：" + manager.getUnsavedRewards());
                }
            });
        }

        // コマンド登録の解除
        commands.forEach(manager::unregisterCommand);
        manager.unregisterCommands();

        // 統計データの書き込み
        String syncDataDir = MSRConfig.getSyncDataDir();
        String statFileName = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        IStatisticsWriter writer = new FileStatisticsWriter(new File(syncDataDir, "logs/" + statFileName + "_stat.csv"));
        StatisticsUtil.writeStatistics(writer);

        getLogger().info("MultiServerRewardの終了処理が完了しました。");
    }

    /**
     * プラグインインスタンスを取得する
     */
    public static MultiServerReward getInstance() {
        return instance;
    }

    /**
     * コマンドAPIを取得する
     */
    public static PaperCommandManager getCommandManager() {
        return manager;
    }

    /**
     * コマンドを登録する
     */
    public static void addCommand(BaseCommand command) {
        commands.add(command);
    }

    /**
     * 統計情報の保存クラスを取得する
     * @return 統計情報の保存クラス
     */
    public static IStatisticsWriter getStatisticsWriter() {
        return statisticsWriter;
    }
}
