package com.github.aburaagetarou.inventory.contents;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.aburaagetarou.inventory.IInventoryContents;

/**
 * インベントリデータの内容を保持するクラス
 * @author AburaAgeTarou
 */
public abstract class InventoryContentsImpl implements IInventoryContents {
    
    // インベントリのアイテム
    protected Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();

    // 所有者
    protected Player player;
    
    /**
     * コンストラクタ
     * @param player 所有者
     */
    public InventoryContentsImpl(Player player) {
        this.player = player;
    }

    /**
     * インベントリデータを取得する
     */
    @Override
    public Map<Integer, ItemStack> getInventoryContents() {
        return contents;
    }

    /**
     * アイテムデータを設定する
     * @param slot インベントリスロット
     * @param item アイテム
     */
    public void setItem(int slot, ItemStack item) {
        contents.put(slot, item);
    }

    /**
     * アイテムデータを保存する
     */
    abstract public boolean save();

    /**
     * アイテムデータを読み込む
     */
    abstract public boolean load();
}
