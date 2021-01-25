package agents.KHTeam;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import common.RSPEnum;
import common.Result;

/**
 * 共通化クラス
 * Team Classから呼ばれるメソッドや、共通メソッドの定義
 */
public abstract class Agent {
    public static final int N = 10; // Queue Capacity
    public static final int M = 2; // 過去の情報を何回持つか
    public Queue<RSPEnum> actionHistory = new ArrayBlockingQueue<RSPEnum>(N); // Agentの行動履歴
    public Queue<Result> resultHistory = new ArrayBlockingQueue<Result>(N);;  // Agentが振る舞った後の結果履歴
    public Queue<Boolean> isWinHistory = new ArrayBlockingQueue<Boolean>(N);; // 過去の勝敗履歴 勝: true

    public String type; // "A" or "B" Teamで出たときにA側にいるかB側にいるか

    public void setType(String t){
        this.type = t;
    }

    public void addActionHistory(RSPEnum action) {
        if (this.actionHistory.size() >= N) this.actionHistory.poll();
        this.actionHistory.add(action);
    }

    public void addResultHistory(Result result){
        if(this.resultHistory.size() >= N) this.actionHistory.poll();
        this.resultHistory.add(result);
    }

    public int getNumOfWins(){
        int c =0;
        for(Boolean b: this.isWinHistory){
            if(b) c++;
        }
        return c;
    }

    /**
     * 行動選択の前に呼ばれる
     */
    public abstract void before();

    /**
     * 次の行動を取得
     * @return 
     */
    public abstract RSPEnum getNextAction();

    /**
     * 行動選択・じゃんけん後に呼ばれる
     * Afterで本クラス変数の値を変えるメソッド
     * （this.addResultHistoryなどは呼び出さないように注意）
     * @param 結果
     */
    public abstract void after(Result r);

    @Override
    public String toString(){
        String className = this.getClass().getName();
        return className + ": [NumofWins] " + String.valueOf(getNumOfWins()); 
    }

}
