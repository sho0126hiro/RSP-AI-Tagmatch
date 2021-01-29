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
    public static final int N = 10000; // Queue Capacity
    public static final int M = 2; // 過去の情報を何回持つか
    public Queue<RSPEnum> actionHistory = new ArrayBlockingQueue<RSPEnum>(N); // Agentの行動履歴
    public Queue<AgentResult> resultHistory = new ArrayBlockingQueue<AgentResult>(N);  // Agentが振る舞った後の結果履歴
    public Queue<Integer> isWinHistory = new ArrayBlockingQueue<Integer>(N);; // 過去の勝敗履歴 勝: true

    public void addActionHistory(RSPEnum action) {
        if (this.actionHistory.size() >= N) this.actionHistory.poll();
        this.actionHistory.add(action);
    }

    public void addResultHistory(AgentResult result){
        if(this.resultHistory.size() >= N) this.actionHistory.poll();
        this.resultHistory.add(result);
    }

    public void addIsWinHistory(Integer b){
        if(this.isWinHistory.size() >= N) this.isWinHistory.poll();
        this.isWinHistory.add(b);
    }

    public int getNumOfWins(){
        int c =0;
        for(Integer b: this.isWinHistory){
            if(b==1) c++;
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
    public abstract void after(AgentResult r);

    @Override
    public String toString(){
        String className = this.getClass().getName();
        return className + ": [NumOfWins] " + String.valueOf(getNumOfWins()); 
    }

}
