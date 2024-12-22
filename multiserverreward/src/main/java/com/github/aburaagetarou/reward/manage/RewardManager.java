package com.github.aburaagetarou.reward.manage;

import java.util.*;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * 報酬管理クラスの基底クラス
 * @author AburaAgeTarou
 */
public abstract class RewardManager {

    // 管理クラスマップ
    protected static final Map<Player, RewardManager> managers = new HashMap<>();

    /**
     * 報酬管理クラスを設定する
     * @param player
     */
    public static void setManager(Player player, RewardManager manager) {
        managers.put(player, manager);
    }

    /**
     * 報酬管理クラスを削除する
     * @param player
     */
    public static void removeManager(Player player) {
        managers.remove(player);
    }

    /**
     * 報酬管理クラスを取得する
     * @param player 報酬の受取者
     */
    public static RewardManager getManager(Player player) {
        return managers.get(player);
    }

    // 報酬の受取者
    final Player player;
    
    // 未保存の報酬情報
    protected List<IReward> unsavedRewards;

    /**
     * コンストラクタ
     * @param player 報酬の受取者
     */
    public RewardManager(Player player) {
        this.player = player;
        unsavedRewards = new ArrayList<>();
    }

    /**
     * 報酬情報を取得する
     */
    public abstract List<IReward> load();

    /**
     * 未保存の報酬情報を追記する
     * @param append 追記フラグ
     */
    public abstract boolean save(boolean append);

    /**
     * 報酬データを削除する
     */
    public abstract boolean delete();

    /**
     * 報酬を渡す
     */
    public void give() {

        // 報酬情報を取得
        List<IReward> rewards = load();

        // 報酬情報がない場合は処理しない
        if(rewards.isEmpty()) {
            return;
        }

        // 同期実行
        Bukkit.getScheduler().runTask(MultiServerReward.getInstance(), () -> {

            // 未受取の報酬情報
            IReward[] unsaved = rewards.toArray(new IReward[0]);

            // 報酬を付与
            for(int i = 0; i < unsaved.length; i++) {
                IReward reward = unsaved[i];

                // アイテムを受け取るスロットがない場合は中断
                if(player.getInventory().firstEmpty() == -1) {
                    MessageUtils.sendColoredMessage(player, "&c受け取れていない報酬があります。");
                    MessageUtils.sendColoredMessage(player, "&cインベントリを整理し、&l/msr reward&cで受け取ってください。");
                    break;
                }

                reward.give(player);
                unsaved[i] = null;
            }

            Bukkit.getScheduler().runTaskAsynchronously(MultiServerReward.getInstance(), () -> {
                // 未受取の報酬情報を再設定
                for(IReward reward : unsaved) {
                    if(reward != null) {
                        unsavedRewards.add(reward);
                    }
                }

                // 未保存の報酬情報を保存
                if(!unsavedRewards.isEmpty()) {
                    save(false);
                }
                else {
                    delete();
                }
            });
        });
    }

    /**
     * 報酬を追加する
     * @param rewards 報酬
     */
    public void addRewards(Collection<? extends IReward> rewards) {
        rewards.forEach(reward -> reward.add(player));
        unsavedRewards.addAll(rewards);
    }

    /**
     * 未保存の報酬情報を取得する
     * @return 未保存の報酬情報
     */
    public List<IReward> getUnsavedRewards() {
        return unsavedRewards;
    }
}
