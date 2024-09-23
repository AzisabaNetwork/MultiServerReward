package com.github.aburaagetarou.reward.config.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.reward.config.RewardType;

/**
 * コマンド実行による報酬を表すクラス
 * @author AburaAgeTarou
 */
public class RewardCommand implements IReward {
    
    // 報酬コマンド
    private final String command;

    /**
     * コンストラクタ
     * @param command 報酬コマンド
     */
    public RewardCommand(String command) {
        this.command = command;
    }

    /**
     * 報酬リストの親キーを取得する
     * @return 親キー
     */
    @Override
    public String getParentKey() {
        return RewardType.COMMAND.getAlias();
    }

    /**
     * Yamlに書き込みを行うオブジェクトを返す
     * @return オブジェクト
     */
    @Override
    public Object serialize() {
        return command;
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
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName()));
        });

        return;
    }

    /**
     * 報酬コマンドを取得する
     * @return 報酬コマンド
     */
    public String getCommand() {
        return command;
    }

    /**
     * 報酬データの文字列を取得する
     * @return 報酬データの文字列
     */
    @Override
    public String toString() {
        return command;
    }
}
