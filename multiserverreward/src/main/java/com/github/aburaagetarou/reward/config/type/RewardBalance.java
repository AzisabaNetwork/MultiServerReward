package com.github.aburaagetarou.reward.config.type;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;

/**
 * ＄報酬を表すクラス
 * @author AburaAgeTarou
 */
public class RewardBalance extends SumRewardBase {
    
    // 報酬コマンド
    private final int balance;

    /**
     * コンストラクタ
     * @param balance 報酬金額
     */
    public RewardBalance(Double balance) {
        BigDecimal bd = new BigDecimal(balance);
        bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        this.balance = bd.intValue();
    }

    /**
     * 報酬リストの親キーを取得する
     * @return 親キー
     */
    @Override
    public String getParentKey() {
        return "balance";
    }

    /**
     * 報酬を文字列化する
     * @return 報酬の文字列
     */
    @Override
    public double getAmount() {
        return (double)balance;
    }

    /**
     * 報酬を付与する
     * @param player 報酬を受け取るプレイヤー
     */
    @Override
    public void add(Player player) {
        return;
    }

    /**
     * 報酬の実物を与える
     * @param player 報酬を受け取るプレイヤー
     */
    @Override
    public void give(Player player) {
        
        // メインスレッドでコマンド実行
        Bukkit.getScheduler().runTask(MultiServerReward.getInstance(), () -> {
            String command = MSRConfig.getBalanceRewardCommand();
            command = command.replace("<player>", player.getName());
            command = command.replace("<amount>", String.valueOf(balance));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
        return;
    }

    /**
     * 報酬コマンドを取得する
     * @return 報酬コマンド
     */
    public long getBalance() {
        return balance;
    }

    /**
     * 報酬データの文字列を取得する
     * @return 報酬データの文字列
     */
    @Override
    public String toString() {
        return "$" + balance;
    }
}