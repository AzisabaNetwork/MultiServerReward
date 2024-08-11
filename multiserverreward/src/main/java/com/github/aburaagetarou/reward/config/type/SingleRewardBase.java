package com.github.aburaagetarou.reward.config.type;

/**
 * 単一の報酬を表すインターフェース
 */
public abstract class SingleRewardBase implements IReward {

    /**
     * 報酬を文字列化する
     * @return 報酬の文字列
     */
    public abstract String serialize();
}
