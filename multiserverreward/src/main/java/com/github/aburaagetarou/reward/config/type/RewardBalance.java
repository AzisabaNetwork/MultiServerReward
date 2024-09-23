package com.github.aburaagetarou.reward.config.type;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.reward.config.RewardType;

/**
 * ＄報酬を表すクラス
 * @author AburaAgeTarou
 */
public class RewardBalance implements IReward, ISummable {
    
    // 報酬金額
    private int balance;

    // 四捨五入する小数桁
    private static final int DECIMAL_PLACES = 0;

    /**
     * コンストラクタ
     * @param balance 報酬金額
     */
    public RewardBalance(Double balance) {
        BigDecimal bd = new BigDecimal(balance);
        bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        this.balance = bd.intValue();
    }
    public RewardBalance(String balance) {
        this(new BigDecimal(balance).doubleValue());
    }


    /**
     * 報酬リストの親キーを取得する
     * @return 親キー
     */
    @Override
    public String getParentKey() {
        return RewardType.BALANCE.getAlias();
    }

    /**
     * 数量に加算する
     * @param amount 加算する数
     * @return 自身
     */
    @Override
    public ISummable add(double amount) {
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        this.balance += bd.intValue();
        return this;
    }

    /**
     * 数量を取得する
     * @return 数量
     */
    @Override
    public double getAmount() {
        return (double)balance;
    }

    /**
     * Yamlに書き込みを行うオブジェクトを返す
     * @return オブジェクト
     */
    @Override
    public Object serialize() {
        BigDecimal bd = new BigDecimal(balance);
        bd = bd.setScale(DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * 報酬を付与する
     * @param player 報酬を受け取るプレイヤー
     */
    @Override
    public void get(Player player) {
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
