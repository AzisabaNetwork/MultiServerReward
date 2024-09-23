package com.github.aburaagetarou.reward.config.type;

/**
 * 合計可能であることを定義するインターフェース
 * @author AburaAgeTarou
 */
public interface ISummable {

    /**
     * 数量に加算する
     * @param amount 加算する数
     * @return 自身
     */
    ISummable add(double amount);

    /**
     * 数量を取得する
     * @return 報酬数
     */
    double getAmount();
}
