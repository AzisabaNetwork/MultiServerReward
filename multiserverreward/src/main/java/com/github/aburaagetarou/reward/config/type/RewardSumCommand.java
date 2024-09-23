package com.github.aburaagetarou.reward.config.type;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.StoredCommandConfig;
import com.github.aburaagetarou.reward.config.RewardType;
import com.github.aburaagetarou.util.PlaceHolderUtils;

/**
 * コマンド実行による累計報酬を表すクラス
 * @author AburaAgeTarou
 */
public class RewardSumCommand implements IReward, ISummable {

    // 実行するコマンドのキー名
    private final String commandKey;
    
    // 報酬付与数
    private double amount;

    // 四捨五入する小数桁
    private static final int DECIMAL_PLACES = 2;

    /**
     * コンストラクタ
     * @param commandKey 実行するコマンドのキー名：数量
     */
    public RewardSumCommand(String key) {
        String split[] = key.split(":", 2);
        if(split.length != 2) {
            this.commandKey = key;
            this.amount = 1.0d;
        } else {
            this.commandKey = split[0];
            this.amount = Double.parseDouble(split[1]);
        }
    }

    /**
     * 報酬リストの親キーを取得する
     * @return 親キー
     */
    @Override
    public String getParentKey() {
        return RewardType.SUM_COMMAND.getAlias();
    }

    /**
     * 数量に加算する
     * @param amount 加算する数
     * @return 自身
     */
    @Override
    public ISummable add(double amount) {
        this.amount += amount;
        return this;
    }

    /**
     * 報酬付与数を取得する
     * @return 報酬付与数
     */
    @Override
    public double getAmount() {
        return amount;
    }

    /**
     * Yamlに書き込みを行うオブジェクトを返す
     * @return オブジェクト
     */
    @Override
    public Object serialize() {
        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);
        return commandKey + ":" + bd.stripTrailingZeros().toString();
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

        // コマンドを取得
        String command = StoredCommandConfig.getCommand(commandKey);
        if(command == null) return;

        // メインスレッドでコマンド実行
        Bukkit.getScheduler().runTask(MultiServerReward.getInstance(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceHolderUtils.replace(command, player, amount));
        });

        return;
    }

    /**
     * 報酬データの文字列を取得する
     * @return 報酬データの文字列
     */
    @Override
    public String toString() {
        return commandKey;
    }    
}
