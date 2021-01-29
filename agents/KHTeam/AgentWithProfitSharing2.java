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
 * 状態数: （多）
 * これ、学習できないので消します
 */
public class AgentWithProfitSharing2 extends Agent {
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
            this.l = before1.myAction.getIndex();
            this.m = before1.enemyActionA.getIndex();
            this.n = before1.enemyActionB.getIndex();
            this.o = now.myAction.getIndex();
        }
        @Override
        public String toString(){
            return "[Indices] " + this.i + " " + this.j + " " + this.k + " " + this.l + " " + this.m + " " + this.n + " " + this.o;
        }
    }

    // episode
    public static final int EPISODE = 3;

    // Episode文の重み更新
    public Queue<Indices> indicesQueue = new ArrayBlockingQueue<Indices>(EPISODE);

    private class State {
        public RSPEnum myAction;
        public RSPEnum enemyActionA;
        public RSPEnum enemyActionB;
        public State(AgentResult r){
            this.myAction = r.myAction;
            this.enemyActionA = r.enemyActionA;
            this.enemyActionB = r.enemyActionB;
        }
        @Override
        public String toString(){
            return "[State] my: " + myAction.name() + ", enemyA: " + enemyActionA.name() + ", enemyB: " + enemyActionB.name();
        }
    }

    public AgentWithProfitSharing2(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    for(int l=0;l<3;l++){
                        for(int m=0;m<3;m++){
                            for(int n=0;n<3;n++){
                                for(int o=0;o<3;o++){
                                    this.weight[i][j][k][l][m][n][o] = -1* 1.0;
                                }
                            }
                        }
                    }
                }
            }
        }

        // state, indicesQueue 0初期化
        for(int i=0;i<EPISODE+M;i++){
            TagTeamAction tta = new TagTeamAction(RSPEnum.ROCK, RSPEnum.ROCK);
            AgentResult r = new AgentResult(new Result(0,0,0,0, tta, tta), RSPEnum.ROCK);
            this.addState(r);
            if(this.stateHistory.size()>=M+1) {
                this.addIndicesQueue(
                    new ArrayList<State>(this.stateHistory));
            }
        }
    }

    public void addState(AgentResult r){
        if(this.stateHistory.size() >= M+1) this.stateHistory.poll();
        this.stateHistory.add(new State(r));
    }
    
    /**
     * 報酬を与える
     * @param now 状態
     * @return {0.0, -1.0, -2.0, -3.0}: 
     * (一人勝ち、どっちかに勝つ),あいこ, どっちかに負ける、二人に負ける
     */
    public double getReward(State now){
        double r = khUtil.RSP1v3(now.myAction, now.enemyActionA, now.enemyActionB);
        if(r == -0.5) r = 0.0;
        return r;
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
        if(this.indicesQueue.size() >= EPISODE) this.indicesQueue.poll();
        State before2 = states.get(0);
        State before1 = states.get(1);
        State now = states.get(2);
        this.indicesQueue.add(new Indices(before2, before1, now));
    }

    public void updateWeight(double reward){
        int queueSize = this.indicesQueue.size();
        for(int i=0;i<queueSize; i++){
            double tmp = this.reinforceFunction(reward, i);
            Indices id = new ArrayList<Indices>(this.indicesQueue).get(queueSize  - i - 1);
            this.weight[id.i][id.j][id.k][id.l][id.m][id.n][id.o] += tmp;
        }
    }

    @Override
    public void before() {
    }

    /**
     * ProfitSharingにおける重みを確率に変換
     * @param w
     * @return
     */
    public double[] weightToProb(double[] w){
        double sum = 0.0;
        for(int i=0;i<w.length;i++){
            sum += w[i];
        }
        double[] problist = new double[w.length];
        for(int i=0;i<w.length;i++){
            problist[i] = w[i]/sum; 
        }
        double max = -1 * Double.MAX_VALUE;
        int maxIndex = 0;
        double min = Double.MAX_VALUE;
        int minIndex = 0;
        for(int i=0;i<problist.length;i++){
            if(max < problist[i]){
                max = problist[i];
                maxIndex = i;
            }
            if(min > problist[i]){
                min = problist[i];
                minIndex = i;
            }
        }
        // exchange
        double tmp = problist[minIndex];
        double tmp2 = problist[maxIndex];
        problist[maxIndex] = tmp; // max <= min
        problist[minIndex] = tmp2; // min <= max
        return problist;
    }

    @Override
    public RSPEnum getNextAction() {
        Indices i = new ArrayList<Indices>(this.indicesQueue).get(EPISODE-1);
        double[] w = this.weight[i.i][i.j][i.k][i.l][i.m][i.n];
        System.out.printf("[getNextAction] ");
        System.out.println(i);
        System.out.printf("[getNextAction] ");
        for(int j=0;j<w.length;j++){
            System.out.printf("%f ", w[j]);
        }
        System.out.println();
        
        // 最大のindexを出す
        double max = (double) -1.0 * Double.MAX_VALUE;
        int idx = 0;
        int tmp=0;
        for(double x: w){
            if(x > max){
                idx = tmp;
                max = x;
            }
            tmp++;
        }

        return khUtil.choiceRSPEnumByIndex(idx); // 決定的方策
        // return khUtil.choiceRSPEnumByProb(this.weightToProb(w)); // 確率的方策
    }

    @Override
    public void after(AgentResult r) {        
        addState(r);
        List<State> states = new ArrayList<State>(this.stateHistory);
        this.addIndicesQueue(states);
        State now = states.get(states.size()-1); // 最新Queue
        double reward = getReward(now);
        updateWeight(reward);
    }

    public void displayIndicesQueue(){
        System.out.printf("[displayIndicesQueue] ");
        for(var i : this.indicesQueue){
            System.out.println(i);
        }
    }

    public void displayStateHistory(){
        System.out.printf("displayStateHistory ");
        for(var i : this.stateHistory){
            System.out.println(i);
        }
    }

    public void displayWeight(){
        for(int i=0;i<M;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    for(int l=0;l<3;l++){
                        for(int m=0;m<3;m++){
                            for(int n=0;n<3;n++){
                                for(int o=0;o<3;o++){
                                    System.out.printf("%f ", this.weight[i][j][k][l][m][n][o]);
                                }
                            }
                        }
                    }
                }
                System.out.println();
            }
        }
    }

}
