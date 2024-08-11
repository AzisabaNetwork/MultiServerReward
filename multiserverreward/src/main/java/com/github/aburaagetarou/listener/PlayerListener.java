package com.github.aburaagetarou.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.inventory.InventoryCopier;
import com.github.aburaagetarou.inventory.contents.FileInventoryContents;
import com.github.aburaagetarou.inventory.contents.InventoryContentsImpl;
import com.github.aburaagetarou.reward.manage.FileRewardManager;
import com.github.aburaagetarou.reward.manage.RewardManager;
import com.github.aburaagetarou.util.MessageUtils;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * プレイヤー参加時のリスナークラス
 * @author AburaAgeTarou
 */
public class PlayerListener implements Listener {
    
    /**
     * プレイヤー参加時の処理
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // 報酬管理クラスを設定
        RewardManager.setManager(event.getPlayer(), new FileRewardManager(event.getPlayer()));
        
        // 連携元サーバーの場合
        if(MSRConfig.isOriginal()) {
            
            // 一拍置いて報酬を受け取る
            Bukkit.getScheduler().runTaskLaterAsynchronously(MultiServerReward.getInstance(), () -> {
                if(!RewardManager.getManager(event.getPlayer()).give()) {
                    MessageUtils.sendColoredMessage(event.getPlayer(), "&c受け取れていない報酬があります。");
                    MessageUtils.sendColoredMessage(event.getPlayer(), "&cインベントリを整理し、&l/msr reward&cで受け取ってください。");
                }
            }, 60L);
        }
        // 連携先サーバーの場合
        else {

            // インベントリデータを読み込む
            InventoryCopier.copyInventoryFromFile(event.getPlayer(), null, () -> {

                // メッセージ送信
                MessageUtils.sendColoredMessage(event.getPlayer(), "&cインベントリ情報のコピーに失敗しました。");

                // 退避先サーバーに接続
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(MSRConfig.getEscapeServer());
                event.getPlayer().sendPluginMessage(MultiServerReward.getInstance(), "BungeeCord", out.toByteArray());
            });
        }
    }
    
    /**
     * プレイヤー退出時の処理
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        
        // 連携元サーバーの場合
        if(MSRConfig.isOriginal()) {

            // 非同期でインベントリデータを保存する
            Bukkit.getScheduler().runTaskAsynchronously(MultiServerReward.getInstance(), () -> {
                InventoryContentsImpl inventory = new FileInventoryContents(event.getPlayer());
                if(!inventory.save()) {
                    MultiServerReward.getInstance().getLogger().warning("Failed to save inventory data. (" + event.getPlayer().getName() + ")");
                }

                // 報酬管理クラスを削除
                RewardManager.removeManager(event.getPlayer());
            });
        }
        // 連携先サーバーの場合
        else {

            // 報酬データを保存する
            Bukkit.getScheduler().runTaskAsynchronously(MultiServerReward.getInstance(), () -> {
                if(!RewardManager.getManager(event.getPlayer()).save(true)) {
                    MultiServerReward.getInstance().getLogger().warning("Failed to save reward data. (" + event.getPlayer().getName() + ")");
                }

                // 報酬管理クラスを削除
                RewardManager.removeManager(event.getPlayer());
            });
        }
    }
}
