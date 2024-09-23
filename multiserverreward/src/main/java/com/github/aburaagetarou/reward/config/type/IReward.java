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
     * Yamlに書き込みを行うオブジェクトを返す
     * @return オブジェクト
     */
    Object serialize();
    
    /**
     * 報酬を付与する
     * @param player 報酬を受け取るプレイヤー
     */
    void get(Player player);
    
    /**
     * 報酬の実物を与える
     * @param player 報酬を受け取るプレイヤー
     */
    void give(Player player);
}
