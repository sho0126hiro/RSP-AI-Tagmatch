package agents.KHTeam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

/**
 * ProfitSharingを用いたエージェント（個人最強型）
 */
public class AgentWithProfitSharing extends Agent {
    private KHUtil khUtil = new KHUtil();
    private final int X = 1;
    // 状態履歴
    public Queue<State> stateHistory = new ArrayBlockingQueue<State>(X + 1); // 2before, 1before, now

    // 1回前までにだした，敵エージェント２人が出した手と自分のエージェントが出した手
    // [enemyA（-1）][enemyB（-1）][自分の出した手（-1）]
    public double weight[][][] = new double[3][3][3];

    private class Indices {
        public int l; // 1before enemyA
        public int m; // 1before enemyB
        public int n; // 1before my
        

        public Indices(State before1, State now) {
            this.l = before1.enemyActionA.getIndex();
            this.m = before1.enemyActionB.getIndex();
            this.n = before1.myAction.getIndex();
        }

        @Override
        public String toString() {
            return "[Indices] " + this.l + " " + this.m + " " + this.n;
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

        public State(AgentResult r) {
            this.myAction = r.myAction;
            this.enemyActionA = r.enemyActionA;
            this.enemyActionB = r.enemyActionB;
        }

        @Override
        public String toString() {
            return "[State] my: " + myAction.name() + ", enemyA: " + enemyActionA.name() + ", enemyB: "
                    + enemyActionB.name();
        }
    }

    public AgentWithProfitSharing() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    this.weight[i][j][k] = -1.0;
                }
            }
        }

        // state, indicesQueue 0初期化
        for (int i = 0; i < EPISODE + X; i++) {
            TagTeamAction tta = new TagTeamAction(RSPEnum.ROCK, RSPEnum.ROCK);
            AgentResult r = new AgentResult(new Result(0, 0, 0, 0, tta, tta), RSPEnum.ROCK);
            this.addState(r);
            if (this.stateHistory.size() >= X+1) {
                this.addIndicesQueue(new ArrayList<State>(this.stateHistory));
            }
        }
    }

    public void addState(AgentResult r) {
        if (this.stateHistory.size() >= X + 1)
            this.stateHistory.poll();
        this.stateHistory.add(new State(r));
    }

    // {-2, -1, 0}を与える (2人に負けた, 1人に負けた，あいこ or 勝ち)
    public double getReward(State now) {
        double r = khUtil.RSP1v3(now.myAction, now.enemyActionA, now.enemyActionB);
        if(r == -0.5) r = 0.0;
        // System.out.printf("[getReward] %f\n", r);
        return r;
    }

    /**
     * 強化関数
     * 
     * @param reward
     * @param idx
     * @return
     */
    public double reinforceFunction(double reward, int idx) {
        return reward * Math.pow(1.0 / 3.0, idx);
    }

    public void addIndicesQueue(List<State> states) {
        if (this.indicesQueue.size() >= EPISODE)
            this.indicesQueue.poll();
        State before1 = states.get(0);
        State now = states.get(1);
        // System.out.printf("[addindicesQueue] states: ");
        // System.out.println(states);
        this.indicesQueue.add(new Indices(before1, now));
        // System.out.printf("[updateWeight(ad INdieces)] ");
        // displayIndicesQueue();
    }

    public void updateWeight(double reward) {
        int queueSize = this.indicesQueue.size();
        for(int i=0;i<queueSize; i++){
            double tmp = this.reinforceFunction(reward, i);
            Indices id = new ArrayList<Indices>(this.indicesQueue).get(queueSize  - i - 1);
            // System.out.printf("[updateWeight] ");
            // System.out.println(id);
            // System.out.printf("[updateWeight] reward: %f, tmp: %f\n", reward, tmp);

            // System.out.printf("[updateWeight] weight(before) ");
            // for(int j=0;j<this.weight.length;j++){
            //     System.out.printf("%f ", this.weight[id.l][id.m][j]);
            // }
            // System.out.println();

            this.weight[id.l][id.m][id.n] += tmp;

            // System.out.printf("[updateWeight] weight(updated) ");
            // for(int j=0;j<this.weight.length;j++){
            //     System.out.printf("%f ", this.weight[id.l][id.m][j]);
            // }
            // System.out.println();
        }
        // displayIndicesQueue();
    }

    @Override
    public void before() {
    }

    /**
     * ProfitSharingにおける重みを確率に変換
     * 
     * @param w
     * @return
     */
    public double[] weightToProb(double[] w) {
        double sum = 0.0;
        for (int i = 0; i < w.length; i++) {
            sum += w[i];
        }
        double[] problist = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            problist[i] = w[i] / sum;
        }
        double max = -1 * Double.MAX_VALUE;
        int maxIndex = 0;
        double min = Double.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < problist.length; i++) {
            if (max < problist[i]) {
                max = problist[i];
                maxIndex = i;
            }
            if (min > problist[i]) {
                min = problist[i];
                minIndex = i;
            }
        }
        // exchange
        double tmp = problist[minIndex];
        double tmp2 = problist[maxIndex];
        problist[maxIndex] = tmp; // max <= min
        problist[minIndex] = tmp2; // min <= max
        // System.out.printf("[weightToProb] ");
        // for(double p: problist){
            // System.out.printf("%f ", p);
        // }
        // System.out.println();
        return problist;
    }

    @Override
    public RSPEnum getNextAction() {
        Indices i = new ArrayList<Indices>(this.indicesQueue).get(EPISODE - 1);
        double[] w = this.weight[i.l][i.m];
        // System.out.printf("[getNextAction(Index)] ");
        // System.out.println(i);
        // System.out.printf("[getNextAction(weight)] ");
        // for (int j = 0; j < w.length; j++) {
        //     System.out.printf("%f ", w[j]);
        // }
        // System.out.println();

        // 最大のindexを出す
        double max = (double) -1.0 * Double.MAX_VALUE;
        int idx = 0;
        int tmp = 0;
        for (double x : w) {
            if (x > max) {
                idx = tmp;
                max = x;
            }
            tmp++;
        }
        // return RSPEnum.SCISORS;

        // epsilon greedy
        // final double EPS = 0.3;
        // Random r = new Random();
        // if (r.nextDouble() > EPS) {
        return khUtil.choiceRSPEnumByIndex(idx);
        // }
        // return khUtil.choiceRSPEnumByProb(this.weightToProb(w));
    }

    @Override
    public void after(AgentResult r) {
        addState(r);
        List<State> states = new ArrayList<State>(this.stateHistory);
        this.addIndicesQueue(states);
        State now = states.get(states.size() - 1); // 最新Queue
        double reward = getReward(now);
        updateWeight(reward);
    }

    public void displayIndicesQueue() {
        System.out.printf("[displayIndicesQueue] ");
        for (var i : this.indicesQueue) {
            System.out.println(i);
        }
    }

    public void displayStateHistory() {
        System.out.printf("[displayStateHistory] ");
        for (var i : this.stateHistory) {
            System.out.println(i);
        }
    }

    public void displayWeight() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    System.out.printf("%f ", this.weight[i][j][k]);
                }
                System.out.println();
            }
        }
    }
}
