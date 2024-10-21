package com.github.aburaagetarou.command;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.github.aburaagetarou.MultiServerReward;
import com.github.aburaagetarou.config.ConfigManager;
import com.github.aburaagetarou.config.MSRConfig;
import com.github.aburaagetarou.reward.config.category.killstreak.KillStreakLevelReward;
import com.github.aburaagetarou.reward.config.category.killstreak.KillStreakReward;
import com.github.aburaagetarou.reward.config.category.match.MatchEndReward;
import com.github.aburaagetarou.reward.config.category.match.MatchLoseReward;
import com.github.aburaagetarou.reward.config.category.match.MatchWinReward;
import com.github.aburaagetarou.reward.config.type.IReward;
import com.github.aburaagetarou.reward.config.type.RewardCommand;
import com.github.aburaagetarou.reward.config.universal.UniversalRewardManager;
import com.github.aburaagetarou.reward.manage.RewardManager;
import com.github.aburaagetarou.util.MessageUtils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;

@CommandAlias("msr")
@Description("MultiServerRewardのコマンド")
public class MSRCommand extends BaseCommand {

    /**
     * コンストラクタ
     */
    public MSRCommand() {
        MultiServerReward.addCommand(this);
    }

    @Dependency
    private Plugin plugin;

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @CommandPermission("msr.reload")
    @Description("設定情報を再読み込みします")
    public void onReload(CommandSender sender) {

        // 再読み込み
        ConfigManager.reload(true);

        MessageUtils.sendColoredMessage(sender, "&a設定情報を再読み込みしました。");
        MessageUtils.sendColoredMessage(sender, "&a60秒以内に連携サーバーの設定が再読み込みされます。");
    }

    @Subcommand("demo")
    @CommandPermission("msr.demo")
    @Description("報酬配布のデモを行います")
    public class Demo extends BaseCommand {

        @Subcommand("command")
        @CommandPermission("msr.demo.command")
        @Description("任意のコマンド報酬を配布します")
        public void onCommand(Player player, String command) {
            List<RewardCommand> rewards = new ArrayList<>();
            rewards.add(new RewardCommand(command));
            RewardManager.getManager(player).addRewards(rewards);
        }
    }

    @Subcommand("check")
    @CommandPermission("msr.check")
    @Description("報酬の内容を確認します")
    public class Check extends BaseCommand {

        @Subcommand("killstreak")
        @Description("固定キルストリーク報酬の内容を表示します")
        public void onKillStreak(CommandSender sender, @Default("-1") int streak) {
            if(streak >= 0) {
                MessageUtils.sendColoredMessage(sender, "&e----- &l" + streak + "キルストリーク報酬 &e-----");
                List<IReward> rewards = KillStreakReward.getAvailableRewards("" + streak);
                if(rewards.isEmpty()) {
                    MessageUtils.sendColoredMessage(sender, "&c報酬はありません。");
                }
                for(IReward reward : rewards) {
                    MessageUtils.sendColoredMessage(sender, "&a報酬内容: &r" + reward.toString());
                }
            }
            else {
                Map<String, List<IReward>> all = KillStreakReward.getAllRewards();
                if(all.isEmpty()) {
                    MessageUtils.sendColoredMessage(sender, "&c報酬はありません。");
                }
                for(String key : all.keySet()) {
                    MessageUtils.sendColoredMessage(sender, "&e----- &l" + key + "キルストリーク報酬 &e-----");
                    for(IReward reward : all.get(key)) {
                        MessageUtils.sendColoredMessage(sender, "&a報酬内容: &r" + reward.toString());
                    }
                }
            }
        }

        @Subcommand("killstreaklevel")
        @Description("倍数キルストリーク報酬の内容を表示します")
        public void onKillStreakLevel(CommandSender sender, @Default("-1") int streak) {
            if(streak >= 0) {
                MessageUtils.sendColoredMessage(sender, "&e----- &l" + streak + "キルストリーク毎報酬 &e-----");
                List<IReward> rewards = KillStreakLevelReward.getAvailableRewards(streak);
                if(rewards.isEmpty()) {
                    MessageUtils.sendColoredMessage(sender, "&c報酬はありません。");
                }
                for(IReward reward : rewards) {
                    MessageUtils.sendColoredMessage(sender, "&a報酬内容: &r" + reward.toString());
                }
            }
            else {
                Map<String, List<IReward>> all = KillStreakLevelReward.getAllRewards();
                if(all.isEmpty()) {
                    MessageUtils.sendColoredMessage(sender, "&c報酬はありません。");
                }
                for(String key : all.keySet()) {
                    MessageUtils.sendColoredMessage(sender, "&e----- &l" + key + "キルストリーク毎報酬 &e-----");
                    for(IReward reward : all.get(key)) {
                        MessageUtils.sendColoredMessage(sender, "&a報酬内容: &r" + reward.toString());
                    }
                }
            }
        }

        @Subcommand("matchend")
        @Description("試合参加報酬の内容を表示します")
        public void onMatchEnd(CommandSender sender) {
            MessageUtils.sendColoredMessage(sender, "&e----- &l試合参加報酬 &e-----");
            List<IReward> rewards = MatchEndReward.getAvailableRewards();
            if(rewards.isEmpty()) {
                MessageUtils.sendColoredMessage(sender, "&c報酬はありません。");
            }
            for(IReward reward : rewards) {
                MessageUtils.sendColoredMessage(sender, "&a報酬内容: &r" + reward.toString());
            }
        }

        @Subcommand("matchwin")
        @Description("試合勝利報酬の内容を表示します")
        public void onMatchWin(CommandSender sender) {
            MessageUtils.sendColoredMessage(sender, "&e----- &l試合勝利報酬 &e-----");
            List<IReward> rewards = MatchWinReward.getAvailableRewards();
            if(rewards.isEmpty()) {
                MessageUtils.sendColoredMessage(sender, "&c報酬はありません。");
            }
            for(IReward reward : rewards) {
                MessageUtils.sendColoredMessage(sender, "&a報酬内容: &r" + reward.toString());
            }
        }

        @Subcommand("matchlose")
        @Description("試合敗北報酬の内容を表示します")
        public void onMatchLose(CommandSender sender) {
            MessageUtils.sendColoredMessage(sender, "&e----- &l試合敗北報酬 &e-----");
            List<IReward> rewards = MatchLoseReward.getAvailableRewards();
            if(rewards.isEmpty()) {
                MessageUtils.sendColoredMessage(sender, "&c報酬はありません。");
            }
            for(IReward reward : rewards) {
                MessageUtils.sendColoredMessage(sender, "&a報酬内容: &r" + reward.toString());
            }
        }
    }

    @Subcommand("test")
    @CommandPermission("msr.test")
    @Description("報酬配布のテストを行います")
    public class Test extends BaseCommand {

        @Subcommand("killstreak")
        @Description("固定キルストリーク報酬のテスト配布を行います")
        public void onKillStreak(Player player, int streak) {
            List<IReward> rewards = KillStreakReward.getAvailableRewards("" + streak);
            RewardManager.getManager(player).addRewards(rewards);
            if(rewards.size() > 0) {
                MessageUtils.sendColoredMessage(player, "&a報酬を受け取りました。");
            }
        }

        @Subcommand("killstreaklevel")
        @Description("倍数キルストリーク報酬のテスト配布を行います")
        public void onKillStreakLevel(Player player, int streak) {
            List<IReward> rewards = KillStreakLevelReward.getAvailableRewards(streak);
            RewardManager.getManager(player).addRewards(rewards);
            if(rewards.size() > 0) {
                MessageUtils.sendColoredMessage(player, "&a報酬を受け取りました。");
            }
        }

        @Subcommand("matchend")
        @Description("試合参加報酬のテスト配布を行います")
        public void onMatchEnd(Player player) {
            List<IReward> rewards = MatchEndReward.getAvailableRewards();
            RewardManager.getManager(player).addRewards(rewards);
            if(rewards.size() > 0) {
                MessageUtils.sendColoredMessage(player, "&a報酬を受け取りました。");
            }
        }

        @Subcommand("matchwin")
        @Description("試合勝利報酬のテスト配布を行います")
        public void onMatchWin(Player player) {
            List<IReward> rewards = MatchWinReward.getAvailableRewards();
            RewardManager.getManager(player).addRewards(rewards);
            if(rewards.size() > 0) {
                MessageUtils.sendColoredMessage(player, "&a報酬を受け取りました。");
            }
        }

        @Subcommand("matchlose")
        @Description("試合敗北報酬のテスト配布を行います")
        public void onMatchLose(Player player) {
            List<IReward> rewards = MatchLoseReward.getAvailableRewards();
            RewardManager.getManager(player).addRewards(rewards);
            if(rewards.size() > 0) {
                MessageUtils.sendColoredMessage(player, "&a報酬を受け取りました。");
            }
        }
    }

    @Subcommand("give")
    @CommandPermission("msr.give")
    @Description("指定テーブルの汎用報酬を配布します")
    public void onGive(Player player, String key, @Flags("target") OnlinePlayer target, @Default("100") double argChance, @Default("true") boolean isOverLottery) {

        // 登録されていないテーブルの場合は警告
        if(!UniversalRewardManager.exists(key)) {
            MultiServerReward.getInstance().getLogger().warning("コマンドで未定義の報酬テーブル[" + key + "]が指定されました。");
            return;
        }

        // 報酬を取得
        List<IReward> rewards = UniversalRewardManager.getReward(key).getRewards();

        // 抽選
        Random lottery = new SecureRandom();
        double chance = (!isOverLottery ? Math.min(argChance, 100.0d) : argChance);
        while(chance > 0.0d) {
            double random = lottery.nextDouble() * 100.0d;
            if(random <= chance) {
                RewardManager.getManager(target.player).addRewards(rewards);
            }
            chance -= 100.0d;
        }
    }

    @Subcommand("debug")
    @CommandPermission("msr.debug")
    @Description("開発者向け機能")
    public class Debug extends BaseCommand {
        
        @Subcommand("give")
        @CommandPermission("msr.debug.give")
        @Description("指定テーブルの汎用報酬を配布します")
        public void onGive(Player player, String key, @Flags("target") OnlinePlayer target, @Default("100") double argChance, @Default("true") boolean isOverLottery) {

            // 登録されていないテーブルの場合は警告
            if(!UniversalRewardManager.exists(key)) {
                MessageUtils.sendColoredMessage(player, "&c未定義の報酬テーブルです。");
                return;
            }

            // 報酬を取得
            List<IReward> rewards = UniversalRewardManager.getReward(key).getRewards();

            // 抽選
            Random lottery = new SecureRandom();
            double chance = (!isOverLottery ? Math.min(argChance, 100.0d) : argChance);
            while(chance > 0.0d) {
                double random = lottery.nextDouble() * 100.0d;
                MessageUtils.sendColoredMessage(player, "random: " + random + " <= chance: " + chance);
                if(random <= chance) {
                    RewardManager.getManager(target.player).addRewards(rewards);
                }
                chance -= 100.0d;
            }
        }
    }

    @Subcommand("reward")
    //@CommandPermission("msr.reward")
    @Description("報酬を受け取ります")
    public void onReward(Player player) {

        // 連携元サーバーではない場合は処理しない
        if(!MSRConfig.isOriginal()) {
            MessageUtils.sendColoredMessage(player, "&c報酬はロビーでのみ受け取れます。");
            return;
        }

        RewardManager.getManager(player).give();
    }
}
