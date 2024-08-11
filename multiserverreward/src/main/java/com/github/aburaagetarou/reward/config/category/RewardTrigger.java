package com.github.aburaagetarou.reward.config.category;

/**
 * 報酬の種類を表す列挙型
 * @author AburaAgeTarou
 */
public enum RewardTrigger {
    
    KILL_STREAK("kill-streak"),                 // 固定キルストリーク
    KILL_STREAK_LEVEL("kill-streak-level"),     // 倍数キルストリーク
    ASSIST_STREAK("assist-streak"),             // 固定アシストストリーク
    ASSIST_STREAK_LEVEL("assist-streak-level"), // 倍数アシストストリーク
    MATCH_END("match-end"),                     // 試合参加
    MATCH_WIN("match-win"),                     // 試合勝利
    MATCH_LOSE("match-lose");                   // 試合敗北

    // コンフィグでのエイリアス
    private String alias;

    /**
     * コンストラクタ
     * @param alias コンフィグでのエイリアス
     */
    private RewardTrigger(String alias) {
        this.alias = alias;
    }

    /**
     * コンフィグでのエイリアスを取得する
     * @return コンフィグでのエイリアス
     */
    public String getAlias() {
        return alias;
    }

    /**
     * コンフィグでのエイリアスから報酬の種類を取得する
     * @param alias コンフィグでのエイリアス
     * @return 報酬の種類
     */
    public static RewardTrigger fromAlias(String alias) {
        for (RewardTrigger type : values()) {
            if (type.getAlias().equals(alias)) {
                return type;
            }
        }
        return null;
    }
}
