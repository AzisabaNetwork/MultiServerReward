package com.github.aburaagetarou.inventory;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.inventory.contents.FileInventoryContents;

/**
 * 保存されたインベントリデータを読み込み、反映する
 * @author AburaAgeTarou
 */
public class InventoryCopier {

    /**
     * インベントリデータを読み込み、反映する
     * @param player プレイヤー
     * @param onSuccess 読み込み成功時の処理
     * @param onFailed 読み込み失敗時の処理
     */
    public static void copyInventoryFromFile(Player player, @Nullable Runnable onSuccess, @Nullable Runnable onFailed) {

        // タスク開始タイミングを取得
        long startTime = MultiServerReward.getInstance().getServer().getCurrentTick();

        // 非同期でインベントリデータを読み込む
        Bukkit.getScheduler().runTaskTimerAsynchronously(MultiServerReward.getInstance(), (task) -> {
            FileInventoryContents inventory = new FileInventoryContents(player);

            // 胴体装備を取得
            ItemStack chestPlate = player.getInventory().getChestplate(); 

            if(inventory.load()) {
                
                // インベントリをクリア
                player.getInventory().clear();

                // インベントリにアイテムセット
                inventory.getInventoryContents().forEach((slot, item) -> {
                    player.getInventory().setItem(slot, item);
                });

                // 胴体装備はチーム判別に使用するため上書きしない
                player.getInventory().setChestplate(chestPlate);

                // 読み込み成功時の処理
                if(onSuccess != null) {
                    onSuccess.run();
                }
                
                // タスクをキャンセル
                Bukkit.getScheduler().cancelTask(task.getTaskId());
            }

            // タイムアウト処理
            long currentTime = MultiServerReward.getInstance().getServer().getCurrentTick();
            if(((currentTime - startTime)/20) > MSRConfig.getInventoryLoadTimeout()) {
                Bukkit.getScheduler().cancelTask(task.getTaskId());

                // 読み込み失敗時の処理
                if(onFailed != null) {
                    onFailed.run();
                }
            }
        }, 0L, 20L);
    }
}
