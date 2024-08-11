package com.github.aburaagetarou.reward.config.category.match;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * 試合報酬設定 クラス
 * @author AburaAgeTarou
 */
public abstract class MatchRewardBase {

    /**
     * 報酬設定のキーを取得する
     * @return キー文字列
     */
    public abstract String getKey();
    
    // 配布期間
    private Date start = new Date(0L);
    private Date end = new Date(Long.MAX_VALUE);

    // 報酬リスト
    private List<IReward> rewards;

    /**
     * 配布期間(開始)を設定する
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * 配布期間(終了)を設定する
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * 報酬リストを設定する
     * @param rewards 報酬リスト
     */
    public void setRewards(List<IReward> rewards) {
        this.rewards = rewards;
    }
    
    /**
     * 期間が有効な場合、報酬を取得する
     * @return 報酬リスト
     */
    public List<IReward> getRewards() {
        return isAvailable() ? rewards : new ArrayList<>();
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
