package com.github.aburaagetarou.statistics;

import javax.annotation.Nullable;

/**
 * 統計対象インターフェース
 * @author AburaAgeTarou
 */
public interface IStatisticsTarget {

	/**
	 * 統計記録時のカテゴリ名を得る
	 * @return カテゴリ名
	 */
	String getStatCategory();

	/**
	 * 統計記録時の内容を得る
	 * @return 名称
	 */
	String getStatData();

	/**
	 * 統計記録時の数量を得る
	 * @return 数量
	 */
	double getStatAmount();
}
