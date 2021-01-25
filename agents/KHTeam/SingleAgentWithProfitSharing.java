package agents.KHTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

/**
 * ProfitSharingを用いたエージェント（個人最強型）
 */
public class SingleAgentWithProfitSharing extends Agent {
    private KHUtil khUtil = new KHUtil();
    // 状態履歴
    public Queue<State> stateHistory = new ArrayBlockingQueue<State>(M+1); // 2before, 1before, now

    // M回前までにだした，敵エージェント２人が出した手と自分のエージェントが出した手
    // [自分の出した手（-2）][enemyA（-2）][enemyB（-2）][自分の出した手（-1）][enemyA（-1）][enemyB（-1）][今の自分の手]
    public double weight[][][][][][][] = new double[3][3][3][3][3][3][3];

    private class Indices {
        public int i; // 2before my
        public int j; // 2before enemyA
        public int k; // 2before enemyB
        public int l; // 1before my
        public int m; // 1before enemyA
        public int n; // 1before enemyB
        public int o; // now my 
        public Indices(State before2, State before1, State now){
            this.i = before2.myAction.getIndex();
            this.j = before2.enemyActionA.getIndex();
            this.k = before2.enemyActionB.getIndex();
            this.l = before2.myAction.getIndex();
            this.m = before2.enemyActionA.getIndex();
            this.n = before2.enemyActionB.getIndex();
            this.o = now.myAction.getIndex();
        }
        @Override
        public String toString(){
            return "[Indices] " + this.i + " " + this.j + " " + this.k + " " + this.l + " " + this.m + " " + this.n + " " + this.o;
        }
    }

    // episode
    public static final int EPISODE = 10;

    // Episode文の重み更新
    public Queue<Indices> indicesQueue = new ArrayBlockingQueue<Indices>(EPISODE);

    private class State {
        public RSPEnum myAction;
        public RSPEnum enemyActionA;
        public RSPEnum enemyActionB;
        public State(Result r, String type){
            if(type.equals("A")) this.myAction = r.AllyTeamAction.actionA;
            else this.myAction = r.AllyTeamAction.actionB;
            this.enemyActionA = r.EnemyTeamAction.actionA;
            this.enemyActionB = r.EnemyTeamAction.actionB;
        }
        @Override
        public String toString(){
            return "[State] my: " + myAction.name() + ", enemyA: " + enemyActionA.name() + ", enemyB: " + enemyActionB.name();
        }
    }

    public SingleAgentWithProfitSharing(){
        for(int i=0;i<M;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    for(int l=0;l<3;l++){
                        for(int m=0;m<3;m++){
                            for(int n=0;n<3;n++){
                                for(int o=0;o<3;o++){
                                    this.weight[i][j][k][l][m][n][o] = 0.0;
                                }
                            }
                        }
                    }
                }
            }
        }

        // state 0初期化
        // indicesQueue: random
        for(int i=0;i<EPISODE+M;i++){
            TagTeamAction tta = new TagTeamAction(RSPEnum.ROCK, RSPEnum.ROCK);
            Result r = new Result(0,0,0,0, tta, tta);
            this.addState(r);
            if(this.stateHistory.size()>=M+1) {
                this.addIndicesQueue(
                    new ArrayList<State>(this.stateHistory));
            }
        }
    }

    public void addState(Result r){
        if(this.stateHistory.size() >= M+1) this.stateHistory.poll();
        if(this.type == null) this.type = "A"; // 初期状態
        this.stateHistory.add(new State(r, this.type));
    }
    
    // {-2, -1, 0}を与える (2人に負けた, 1人に負けた，あいこ or 勝ち)
    public double getReward(State now){
        int lose = 0;
        if(khUtil.RSP1v1(now.myAction, now.enemyActionA) == -1) lose--;
        if(khUtil.RSP1v1(now.myAction, now.enemyActionB) == -1) lose--;
        return lose;
    }
    /**
     * 強化関数
     * @param reward
     * @param idx
     * @return
     */
    public double reinforceFunction(double reward, int idx){
        return reward * Math.pow(1.0/3.0, idx);
    }

    public void addIndicesQueue(List<State> states){
        if(this.indicesQueue.size() >= EPISODE+1) this.indicesQueue.poll();
        State before2 = states.get(0);
        State before1 = states.get(1);
        State now = states.get(2);
        this.indicesQueue.add(new Indices(before2, before1, now));
    }

    public void updateWeight(double reward){
        int idx = 0;
        for(Indices i: this.indicesQueue){
            this.weight[i.i][i.j][i.k][i.l][i.m][i.n][i.o] += this.reinforceFunction(reward,idx);
            idx++;
        }
    }

    @Override
    public void before() {
    }

    @Override
    public RSPEnum getNextAction() {
        Indices i = new ArrayList<Indices>(this.indicesQueue).get(EPISODE-1);
        double[] w = this.weight[i.i][i.j][i.k][i.l][i.m][i.n];
        double max = (double) Double.MIN_VALUE;
        int idx = 0;
        int tmp=0;
        for(double x: w){
            if(x > max) idx = tmp;
            tmp++;  
        }
        return khUtil.idxToRSPEnum(idx);
    }

    @Override
    public void after(Result r) {        
        addState(r);
        List<State> states = new ArrayList<State>(this.stateHistory);
        this.addIndicesQueue(states);
        State now = states.get(states.size()-1); // 最新Queue
        double reward = getReward(now);
        updateWeight(reward);
    }

    public void displayIndicesQueue(){
        for(var i : this.indicesQueue){
            System.out.println(i);
        }
    }

    public void displayStateHistory(){
        for(var i : this.stateHistory){
            System.out.println(i);
        }
    }

}
