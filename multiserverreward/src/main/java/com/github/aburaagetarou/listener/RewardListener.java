package com.github.aburaagetarou.listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.aburaagetarou.reward.config.category.killstreak.AssistStreakLevelReward;
import com.github.aburaagetarou.reward.config.category.killstreak.AssistStreakReward;
import com.github.aburaagetarou.reward.config.category.killstreak.KillStreakLevelReward;
import com.github.aburaagetarou.reward.config.category.killstreak.KillStreakReward;
import com.github.aburaagetarou.reward.config.category.match.MatchEndReward;
import com.github.aburaagetarou.reward.config.category.match.MatchLoseReward;
import com.github.aburaagetarou.reward.config.category.match.MatchWinReward;
import com.github.aburaagetarou.reward.config.type.IReward;
import com.github.aburaagetarou.reward.manage.RewardManager;

import net.azisaba.lgw.core.LeonGunWar;
import net.azisaba.lgw.core.events.MatchFinishedEvent;
import net.azisaba.lgw.core.events.PlayerAssistEvent;
import net.azisaba.lgw.core.events.PlayerKillEvent;
import net.azisaba.lgw.core.util.BattleTeam;

/**
 * 報酬受け取り用のリスナークラス
 * @author AburaAgeTarou
 */
public class RewardListener implements Listener {
    
    // デバッグ用キルストリーク数設定
    public static int debugKillStreak = -1;

    /**
     * 報酬の付与
     * リーダーの場合は2倍与える
     * @param player プレイヤー
     * @param rewards 報酬リスト
     */
    public static void addRewards(Player player, List<IReward> rewards) {
        RewardManager manager = RewardManager.getManager(player);
        manager.addRewards(rewards);

        //報酬弐倍システム
        if(LeonGunWar.doubleRewardEnable){
            manager.addRewards(rewards);
        }

        // リーダー報酬
        if(LeonGunWar.getPlugin().getManager().isLeaderMatch() && !LeonGunWar.doubleRewardEnable) {
            Map<BattleTeam, Player> leaders = LeonGunWar.getPlugin().getManager().getLDMLeaderMap();
            if(leaders.values().contains(player)) {
                manager.addRewards(rewards);
            }
        }
    }

    /**
     * キルストリーク報酬
     */
    @EventHandler
    public void onKillStreak(PlayerKillEvent event) {

        // ストリーク数取得
        int streak = event.getKillStreaks();
        if(debugKillStreak != -1) {
            streak = debugKillStreak;
            debugKillStreak = -1;
        }

        // 固定キルストリーク報酬
        List<IReward> rewards = KillStreakReward.getAvailableRewards("" + streak);
        if(rewards.size() > 0) {
            addRewards(event.getPlayer(), rewards);
        }

        // 倍数キルストリーク報酬
        rewards = KillStreakLevelReward.getAvailableRewards(streak);
        if(rewards.size() > 0) {
            addRewards(event.getPlayer(), rewards);
        }
    }

    /**
     * アシストストリーク報酬
     */
    @EventHandler
    public void onAssistStreak(PlayerAssistEvent event) {

        // ストリーク数取得
        AtomicInteger streak = LeonGunWar.getPlugin().getAssistStreaks().get(event.getPlayer());
        if(debugKillStreak != -1) {
            streak = new AtomicInteger(debugKillStreak);
            debugKillStreak = -1;
        }

        // 固定アシストストリーク報酬
        List<IReward> rewards = AssistStreakReward.getAvailableRewards("" + streak.get());
        if(rewards.size() > 0) {
            addRewards(event.getPlayer(), rewards);
        }

        // 倍数アシストストリーク報酬
        rewards = AssistStreakLevelReward.getAvailableRewards(streak.get());
        if(rewards.size() > 0) {
            addRewards(event.getPlayer(), rewards);
        }
    }

    /**
     * 試合報酬
     */
    @EventHandler
    public void onMatchEnd(MatchFinishedEvent event) {

        // 各試合報酬を得る
        List<IReward> matchEndRews  = MatchEndReward .getAvailableRewards();  // 試合終了報酬
        List<IReward> matchWinRews  = MatchWinReward .getAvailableRewards();  // 試合勝利報酬
        List<IReward> matchLoseRews = MatchLoseReward.getAvailableRewards();  // 試合敗北報酬
        
        // 試合終了報酬(全員)
        for(Player player : event.getAllTeamPlayers()) {
            RewardManager manager = RewardManager.getManager(player);
            manager.addRewards(matchEndRews);
        }

        // 勝利/敗北報酬
        // 勝者が存在する場合のみ
        if(event.getWinners().size() > 0) {
            Map<BattleTeam, List<Player>> teams = event.getTeamPlayers();
            for(BattleTeam team : teams.keySet()) {
                
                // 勝敗に応じて報酬を付与
                boolean win = event.getWinners().contains(team);
                for(Player player : teams.get(team)) {
                    RewardManager manager = RewardManager.getManager(player);
                    if(win) manager.addRewards(matchWinRews);
                    else    manager.addRewards(matchLoseRews);
                }
            }
        }
    }
}
