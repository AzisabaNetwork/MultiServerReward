package com.github.aburaagetarou.inventory;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

/**
 * インベントリデータの内容を保持するインターフェース
 * @author AburaAgeTarou
 */
public interface IInventoryContents {
    
    /**
     * インベントリデータを取得する
     * @return スロット/アイテムのマップ
     */
    public Map<Integer, ItemStack> getInventoryContents();
}
