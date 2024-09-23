package com.github.aburaagetarou.util;

import java.math.BigDecimal;

import org.bukkit.entity.Player;

/**
 * 文字列置き換えユーティリティクラス
 * @author AburaAgeTarou
 */
public class PlaceHolderUtils {
    
    /**
     * 置き換えの実行
     * @param message 置き換え対象の文字列
     * @param args 置き換えに使用するデータ
     * @return 置き換え後の文字列
     */
    public static String replace(String message, Object ...args) {
        String newMsg = new String(message);
        
        // 置き換え
        for(Object obj : args) {

            // プレイヤー情報
            if(obj instanceof Player) {
                newMsg = newMsg.replace("<player>", ((Player)obj).getName());
            }

            // 数値
            if(obj instanceof Double) {
                BigDecimal bd = new BigDecimal((Double)obj);
                newMsg = newMsg.replace("<amount>", bd.stripTrailingZeros().toString());
            }
        }
        return newMsg;
    }
}
