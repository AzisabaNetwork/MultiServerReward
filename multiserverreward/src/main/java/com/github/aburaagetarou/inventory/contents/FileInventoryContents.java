package com.github.aburaagetarou.inventory.contents;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.aburaagetarou.config.MSRConfig;

/**
 * インベントリデータの内容をファイルで保持するクラス
 * @author AburaAgeTarou
 */
public class FileInventoryContents extends InventoryContentsImpl {

    /**
     * コンストラクタ
     * @param player プレイヤー
     */
    public FileInventoryContents(Player player) {
        super(player);
    }

    /**
     * インベントリデータをファイルに保存する
     * @return 保存に成功した場合はtrue
     */
    @Override
    public boolean save() {

        // 保存先ディレクトリパスの取得
        String syncDataDir = MSRConfig.getSyncDataDir();

        // ファイル名の生成
        File file = new File(syncDataDir, "inventory/" + player.getUniqueId() + ".yml");

        // ファイルが存在する場合は削除
        if (file.exists()) {
            file.delete();
        }

        // YAML形式でデータを保存
        YamlConfiguration yml = new YamlConfiguration();
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (player.getInventory().getItem(i) != null) {
                yml.set(String.valueOf(i), player.getInventory().getItem(i));
            }
        }
        
        // ファイルに保存
        try {
            yml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * インベントリデータをファイルから読み込む
     * @return 読み込みに成功した場合はtrue
     */
    @Override
    public boolean load() {
        
        // 保存先ディレクトリパスの取得
        String syncDataDir = MSRConfig.getSyncDataDir();
        
        // ファイルオブジェクトの生成
        File file = new File(syncDataDir, "inventory/" + player.getUniqueId() + ".yml");

        // ファイルが存在しない場合は読み込まない
        if (!file.exists()) {
            return false;
        }
        
        // ファイルからデータを読み込む
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        for (String key : yml.getKeys(false)) {
            ItemStack item = yml.getItemStack(key);
            contents.put(Integer.valueOf(key), item);
        }

        // ファイル削除
        file.delete();

        return true;
    }
}
