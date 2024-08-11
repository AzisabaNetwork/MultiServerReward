package com.github.aburaagetarou.reward.config.category.killstreak;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * キルストリーク報酬設定 クラス
 * @author AburaAgeTarou
 */
public abstract class KillStreakRewardBase {

    /**
     * 報酬設定のキーを取得する
     * @return キー文字列
     */
    public abstract String getKey();
    
    // 配布期間
    private Date start = new Date(0L);
    private Date end = new Date(Long.MAX_VALUE);

    // 報酬リスト
    protected Map<String, List<IReward>> rewards;

    /**
     * 配布期間(開始)を設定する
     * @param start 開始日時
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * 配布期間(終了)を設定する
     * @param end 終了日時
     */
    public void setEnd(Date end) {
        this.end = end;
    }
    
    /**
     * キーから報酬を取得する
     * @return 報酬リスト
     */
    public List<IReward> getReward(String key) {
        
        // 配布期間外の場合はnullを返す
        if(!isAvailable()) return null;

        return rewards.get(key);
    }

    /**
     * 報酬を設定する
     * @param rewards 報酬リスト
     */
    public void setRewards(Map<String, List<IReward>> rewards) {
        this.rewards = rewards;
    }

    /**
     * 配布期間内かどうかを判定する
     * @return 配布期間内の場合はtrue
     */
    public boolean isAvailable() {
        Date now = new Date();
        return start.before(now) && end.after(now);
    }
}
