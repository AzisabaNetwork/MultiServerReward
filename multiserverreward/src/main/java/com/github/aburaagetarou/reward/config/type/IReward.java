package com.github.aburaagetarou.reward.config.type;

import org.bukkit.entity.Player;

/**
 * 報酬を表すインターフェース
 * @author AburaAgeTarou
 */
public interface IReward {

    /**
     * 報酬リストの親キーを取得する
     * @return 親キー
     */
    String getParentKey();

    /**
     * 報酬を付与する
     * @param player 報酬を受け取るプレイヤー
     */
    void add(Player player);
    
    /**
     * 報酬の実物を与える
     * @param player 報酬を受け取るプレイヤー
     */
    void give(Player player);
}
