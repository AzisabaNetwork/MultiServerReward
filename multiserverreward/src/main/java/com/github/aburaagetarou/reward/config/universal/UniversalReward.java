package com.github.aburaagetarou.reward.config.universal;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.reward.config.RewardType;
import com.github.aburaagetarou.reward.config.type.IReward;

/**
 * 汎用報酬設定 クラス
 * @author AburaAgeTarou
 * @version 1.1.0
 */
public class UniversalReward {
    
    /**
     * 試合報酬設定の読み込み
     * @param rewardCfg 報酬設定
     * @param yml 設定ファイル
     * @param node 親ノード
     * @return 報酬設定(rewardCfg)
     */
    public static UniversalReward loadUniversalReward(UniversalReward rewardCfg, YamlConfiguration yml, @Nonnull String node) {

        // 汎用報酬設定はテーブル名を必須とする
        if(node == null) throw new IllegalArgumentException("node is null");

        // 報酬リストの初期化
        List<IReward> rewards = new ArrayList<>();
        rewardCfg.setRewards(rewards);

        // 配布期間の取得
        String startPath = node + "." + rewardCfg.getKey() + ".start";
        String endPath = node + "." + rewardCfg.getKey() + ".end";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if(yml.contains(startPath)) rewardCfg.setStart(sdf.parse(yml.getString(startPath)));
            if(yml.contains(endPath)) rewardCfg.setEnd(sdf.parse(yml.getString(endPath)));
        } catch (Exception e) {
            e.printStackTrace();
            // 続行
        }

        // 報酬を取得
        ConfigurationSection section = yml.getConfigurationSection(node);
        if(section == null) return rewardCfg;
        for(String key : section.getKeys(false)) {

            // 報酬リストの初期化
            List<IReward> rewardList = new ArrayList<>();

            // 報酬タイプの取得
            RewardType type = RewardType.fromAlias(key);  
            if(type == null) continue;

            // パスの作成
            String rewardPath = node + "." + key;

            // 報酬リストの取得
            try {
                Constructor<? extends IReward> constructor = type.getRewardClass().getConstructor(type.getRewardType());
                for(Object data : yml.getList(rewardPath)) {
                    IReward reward = constructor.newInstance(data);
                    rewardList.add(reward);
                }    
            } catch(Exception e) {
                e.printStackTrace();
                continue;
            }

            // 報酬リストに追加
            rewards.addAll(rewardList);
        }

        // 報酬設定に報酬リストを設定
        rewardCfg.setRewards(rewards);
        
        return rewardCfg;
    }    

    // 報酬のテーブル名
    private final String key;

    // 配布期間
    private Date start = new Date(0L);
    private Date end = new Date(Long.MAX_VALUE);

    // 報酬リスト
    private List<IReward> rewards;

    /**
     * コンストラクタ
     * @param key 報酬のテーブル名
     */
    public UniversalReward(String key) {
        this.key = key;
    }

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

    /**
     * 報酬のテーブル名を取得する
     * @return 報酬のテーブル名文字列
     */
    public String getKey() {
        return key;
    }
}
