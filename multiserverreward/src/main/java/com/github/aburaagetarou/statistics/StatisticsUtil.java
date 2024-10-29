package com.github.aburaagetarou.statistics;

import com.github.aburaagetarou.MultiServerReward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 報酬統計関連のユーティリティクラス
 * @author AburaAgeTarou
 */
public class StatisticsUtil {

	// 統計
	private static final Map<OfflinePlayer, List<IStatisticsTarget>> statistics = new HashMap<>();

	/**
	 * 要素をCSV形式に変換する
	 * @param contents 要素
	 * @return CSV形式の文字列
	 */
	public static String toCSV(String... contents) {

		// エスケープ
		List<String> list = Arrays.asList(contents);
		for (int i = 0; i < list.size(); i++) {
			String content = list.get(i);
			content = content.replace("\"", "\"\"");
			content = "\"" + content + "\"";
			list.set(i, content);
		}
		return String.join(",", list);
	}

	/**
	 * データを書き込む
	 * @param writer 統計データ書き込みインターフェース
	 * @param reward 報酬
	 * @param player プレイヤー
	 */
	public static void writeData(IStatisticsWriter writer, IStatisticsTarget reward, OfflinePlayer player) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(MultiServerReward.getInstance(), () -> {
			writer.write(toCSV(player.getName(), reward.getStatCategory(), reward.getStatData(), BigDecimal.valueOf(reward.getStatAmount()).setScale(2, RoundingMode.CEILING).toPlainString()));
		}, 1L);

		// 統計データの更新
		List<IStatisticsTarget> rewardList = statistics.getOrDefault(player, new ArrayList<>());
		rewardList.add(reward);
		statistics.put(player, rewardList);
	}

	/**
	 * 統計データの書き込み
	 * @param writer 統計データ書き込みインターフェース
	 */
	public static void writeStatistics(IStatisticsWriter writer) {
		for(OfflinePlayer player : statistics.keySet()) {
			List<IStatisticsTarget> statData = statistics.get(player);
			statData.sort(Comparator.comparing(IStatisticsTarget::getStatCategory).thenComparing(IStatisticsTarget::getStatData));
			double amount = 0.0d;
			String compBefore = "";
			String categoryBefore = "";
			String dataBefore = "";
			for(IStatisticsTarget data : statData) {
				String comp = data.getStatCategory() + data.getStatData();
				if(compBefore.isEmpty()) compBefore = comp;
				if(categoryBefore.isEmpty()) categoryBefore = data.getStatCategory();
				if(dataBefore.isEmpty()) dataBefore = data.getStatData();
				if(!compBefore.isEmpty() && !compBefore.equals(comp)) {
					writer.write(toCSV(player.getName(), categoryBefore, dataBefore, BigDecimal.valueOf(amount).setScale(2, RoundingMode.CEILING).toPlainString()));
					amount = 0.0d;
					categoryBefore = data.getStatCategory();
					dataBefore = data.getStatData();
				}
				amount += data.getStatAmount();
				compBefore = comp;
			}
			if(compBefore.isEmpty()) {
				writer.write(toCSV(player.getName(), categoryBefore, dataBefore, BigDecimal.valueOf(amount).setScale(2, RoundingMode.CEILING).toPlainString()));
			}
		}

		// 合計
		List<IStatisticsTarget> allStatData = new ArrayList<>();
		for(List<IStatisticsTarget> rewards : statistics.values()) {
			allStatData.addAll(rewards);
		}
		allStatData.sort(Comparator.comparing(IStatisticsTarget::getStatCategory).thenComparing(IStatisticsTarget::getStatData));

		double amount = 0.0d;
		String compBefore = "";
		String categoryBefore = "";
		String dataBefore = "";
		for(IStatisticsTarget data : allStatData) {
			String comp = data.getStatCategory() + data.getStatData();
			if(compBefore.isEmpty()) compBefore = comp;
			if(categoryBefore.isEmpty()) categoryBefore = data.getStatCategory();
			if(dataBefore.isEmpty()) dataBefore = data.getStatData();
			if(!compBefore.isEmpty() && !compBefore.equals(comp)) {
				writer.write(toCSV("TOTAL", categoryBefore, dataBefore, BigDecimal.valueOf(amount).setScale(2, RoundingMode.CEILING).toPlainString()));
				amount = 0.0d;
				categoryBefore = data.getStatCategory();
				dataBefore = data.getStatData();
				compBefore = comp;
			}
			amount += data.getStatAmount();
		}
		if(compBefore.isEmpty()) {
			writer.write(toCSV("TOTAL", categoryBefore, dataBefore, BigDecimal.valueOf(amount).setScale(2, RoundingMode.CEILING).toPlainString()));
		}
	}
}
