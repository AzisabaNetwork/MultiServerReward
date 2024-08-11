package com.github.aburaagetarou.reward.config;

import com.github.aburaagetarou.reward.config.type.IReward;
import com.github.aburaagetarou.reward.config.type.RewardBalance;
import com.github.aburaagetarou.reward.config.type.RewardCommand;
import com.github.aburaagetarou.reward.config.type.RewardMessage;

/**
 * 報酬の種類を表す列挙型
 * @author AburaAgeTarou
 */
public enum RewardType {
    
    MESSAGE("messages", RewardMessage.class, String.class),
    //ITEM("item", RewardItem.class),
    COMMAND("commands", RewardCommand.class, String.class),
    BALANCE("balance", RewardBalance.class, Double.class);

    // コンフィグでのエイリアス
    private final String alias;

    // 報酬の実装クラス
    private Class<? extends IReward> rewardClass;

    // 報酬内容の型
    private Class<?> rewardType;

    /**
     * コンストラクタ
     * @param alias コンフィグでのエイリアス
     * @param rewardClass 報酬の実装クラス
     * @param rewardType 報酬内容の型
     */
    private RewardType(String alias, Class<? extends IReward> rewardClass, Class<?> rewardType) {
        this.alias = alias;
        this.rewardClass = rewardClass;
        this.rewardType = rewardType;
    }

    /**
     * コンフィグでのエイリアスを取得する
     * @return コンフィグでのエイリアス
     */
    public String getAlias() {
        return alias;
    }

    /**
     * 報酬の実装クラスを取得する
     * @return 報酬の実装クラス
     */
    public Class<? extends IReward> getRewardClass() {
        return rewardClass;
    }

    /**
     * 報酬内容の型を取得する
     * @return 報酬内容の型
     */
    public Class<?> getRewardType() {
        return rewardType;
    }

    /**
     * コンフィグでのエイリアスから報酬の種類を取得する
     * @param alias コンフィグでのエイリアス
     * @return 報酬の種類
     */
    public static RewardType fromAlias(String alias) {
        for (RewardType type : values()) {
            if (type.getAlias().equals(alias)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 報酬の実装クラスから報酬の種類を取得する
     * @param rewardClass 報酬の実装クラス
     * @return 報酬の種類
     */
    public static RewardType fromRewardClass(Class<? extends IReward> rewardClass) {
        for (RewardType type : values()) {
            if (type.getRewardClass().equals(rewardClass)) {
                return type;
            }
        }
        return null;
    }
}
