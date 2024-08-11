package com.github.aburaagetarou.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.listener.RewardListener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.azisaba.lgw.core.LeonGunWar;
import net.azisaba.lgw.core.events.MatchFinishedEvent;
import net.azisaba.lgw.core.events.PlayerKillEvent;
import net.azisaba.lgw.core.util.BattleTeam;
import net.azisaba.lgw.core.util.GameMap;

/**
 * 爆弾(後で消す)
 */
@CommandAlias("takuya")
public class Takuya extends BaseCommand {

    /**
     * 試合終了時のイベントを手動発生させる激ヤバコマンド
     * 実行するとどうなっちゃうの！？知らな～い
     */
    @Subcommand("matchfinish")
    @CommandPermission("go.is.god")
    public void onMatchEnd(Player player) {
        
        // タクヤさんお好みのマップが勝手に選ばれる
        GameMap map = LeonGunWar.getPlugin().getMapsConfig().getRandomMap();

        // じゃあお前のチーム勝たせてやるか！しょうがねぇな…
        Map<BattleTeam, List<Player>> teams = new HashMap<>();
        List<BattleTeam> winner = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        BattleTeam team = LeonGunWar.getPlugin().getManager().getBattleTeam(player);
        players.add(player);
        teams.put(team, players);
        winner.add(team);

        // それでは、ご覧ください――
        MatchFinishedEvent event = new MatchFinishedEvent(map, winner, teams);
        MultiServerReward.getInstance().getServer().getPluginManager().callEvent(event);
    }

    /**
     * キルストリーク報酬を手動で発生させる激ヤバコマンド
     * ストリーク数の指定は報酬受け取りプラグインでのみ機能する、やはりヤバい
     */
    @Subcommand("killstreak")
    @CommandPermission("go.is.god")
    public void onKillStreak(Player player, int streak) {

        // ダイナマイ！
        PlayerKillEvent event = new PlayerKillEvent(player, "邪剣「夜」");
        RewardListener.debugKillStreak = streak;
        MultiServerReward.getInstance().getServer().getPluginManager().callEvent(event);
    }
}
