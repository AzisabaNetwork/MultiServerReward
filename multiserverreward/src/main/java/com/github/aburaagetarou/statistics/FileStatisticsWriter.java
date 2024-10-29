package com.github.aburaagetarou.statistics;

import com.github.aburaagetarou.MultiServerReward;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * ファイルへの統計書き込み
 * @author AburaAgeTarou
 */
public class FileStatisticsWriter implements IStatisticsWriter {

	// ファイルオブジェクト
	File file;

	/**
	 * コンストラクタ
	 * @param file ファイルオブジェクト もしくはファイルパス
	 */
	public FileStatisticsWriter(File file) {
		this.file = file;
	}
	public FileStatisticsWriter(String file) {
		this.file = new File(file);
	}

	/**
	 * 報酬統計データを書き込む
	 * @param content 報酬統計データ
	 */
	@Override
	public boolean write(String content) {
		if(file == null) return false;
		try {
			if(!file.exists()) {
				if(!file.createNewFile()) return false;
			}
			if(file.isDirectory()) {
				throw new IOException("File is directory: " + file.getPath());
			}
			Files.write(file.toPath(), Collections.singletonList(content), StandardOpenOption.APPEND);
		} catch (IOException e) {
			MultiServerReward.getInstance().getLogger().warning("Failed to write reward data: " + file.getPath());
			return false;
		}
		return true;
	}
}
