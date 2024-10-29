package com.github.aburaagetarou.statistics;

/**
 * 統計データ書き込みインターフェース
 * @author AburaAgeTarou
 */
public interface IStatisticsWriter {

	/**
	 * 統計データを書き込む
	 * @param content 報酬統計データ
	 */
	boolean write(String content);
}
