package com.github.aburaagetarou.reward.config.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.aburaagetarou.MultiServerReward;

/**
 * コマンド実行による報酬を表すクラス
 * @author AburaAgeTarou
 */
public class RewardCommand extends SingleRewardBase {
    
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
        return "commands";
    }

    /**
     * 報酬を文字列化する
     * @return 報酬の文字列
     */
    @Override
    public String serialize() {
        return command;
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
