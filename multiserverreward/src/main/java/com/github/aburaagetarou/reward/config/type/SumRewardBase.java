package com.github.aburaagetarou.reward.config.type;

/**
 * 合計の報酬を表すインターフェース
 */
public abstract class SumRewardBase implements IReward {

    /**
     * 報酬を数値化する
     * @return 報酬数
     */
    public abstract double getAmount();
}
