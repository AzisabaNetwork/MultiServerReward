package com.github.aburaagetarou.reward.config.type;

import org.bukkit.entity.Player;
import com.github.aburaagetarou.util.MessageUtils;

/**
 * 報酬としてメッセージ送信を行うクラス
 * ※この報酬はファイルに保存しない
 * @author AburaAgeTarou
 */
public class RewardMessage implements IReward {
    
    // 報酬メッセージ
    private final String message;

    /**
     * コンストラクタ
     * @param message 報酬メッセージ
     */
    public RewardMessage(String message) {
        this.message = message;
    }

    /**
     * 報酬リストの親キーを取得する
     * @return null
     */
    @Override
    public String getParentKey() {
        return null;
    }

    /**
     * Yamlに書き込みを行うオブジェクトを返す
     * @return オブジェクト
     */
    @Override
    public Object serialize() {
        return null;
    }

    /**
     * 報酬を付与する
     * @param player 報酬を受け取るプレイヤー
     */
    @Override
    public void get(Player player) {

        // メッセージがない場合は何もしない
        if(message == null || message.length() == 0) {
            return;
        }

        // 送信するメッセージを作成
        String msg = MessageUtils.replacePlayerName(new String(message), player);
        
        // 全体メッセージを送信
        MessageUtils.broadcastColoredMessage(msg);
    }
    
    /**
     * 報酬の実物を与える
     * @param player 報酬を受け取るプレイヤー
     */
    @Override
    public void give(Player player) {
        return;
    }

    /**
     * 報酬を文字列化する
     * @return 報酬メッセージ
     */
    @Override
    public String toString() {
        String result = "&7[全体メッセージ] &r" + message;
        return result;
    }
}
