package agents.KHTeam;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;
public class AgentWithQ_learning extends Agent {
    private double alpha = 0.3;
    private double epsilon = 0.5;
    private double gamma = 0.5;
    private KHUtil khUtil = new KHUtil();
    // 状態履歴
    public Queue<StateElement> state = new ArrayBlockingQueue<StateElement>(M+1); // 2before, 1before, now

    // M回前までにだした，敵エージェント２人が出した手と自分のエージェントが出した手
    // [自分の出した手（-2）][enemyA（-2）][enemyB（-2）][自分の出した手（-1）][enemyA（-1）][enemyB（-1）][今の自分の手]
    public double qvalue[][][][][][][] = new double[3][3][3][3][3][3][3];

    private class Indices {
        public int i; // 2before my
        public int j; // 2before enemyA
        public int k; // 2before enemyB
        public int l; // 1before my
        public int m; // 1before enemyA
        public int n; // 1before enemyB
        public int o; // now my
        public Indices(StateElement before2, StateElement before1, StateElement now){
            this.i = before2.myAction.getIndex();
            this.j = before2.enemyActionA.getIndex();
            this.k = before2.enemyActionB.getIndex();
            this.l = before1.myAction.getIndex();
            this.m = before1.enemyActionA.getIndex();
            this.n = before1.enemyActionB.getIndex();
            this.o = now.myAction.getIndex();
        }
        public Indices(StateElement before2, StateElement before1, int now){
            this.i = before2.myAction.getIndex();
            this.j = before2.enemyActionA.getIndex();
            this.k = before2.enemyActionB.getIndex();
            this.l = before1.myAction.getIndex();
            this.m = before1.enemyActionA.getIndex();
            this.n = before1.enemyActionB.getIndex();
            this.o = now;
        }
        @Override
        public String toString(){
            return "[Indices] " + this.i + " " + this.j + " " + this.k + " " + this.l + " " + this.m + " " + this.n + " " + this.o;
        }
    }

    private class StateElement {
        public RSPEnum myAction;
        public RSPEnum enemyActionA;
        public RSPEnum enemyActionB;
        public StateElement(AgentResult r){
            this.myAction = r.myAction;
            this.enemyActionA = r.enemyActionA;
            this.enemyActionB = r.enemyActionB;
        }
        @Override
        public String toString(){
            return "[State] my: " + myAction.name() + ", enemyA: " + enemyActionA.name() + ", enemyB: " + enemyActionB.name();
        }
    }

    public AgentWithQ_learning(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                for(int k=0;k<3;k++){
                    for(int l=0;l<3;l++){
                        for(int m=0;m<3;m++){
                            for(int n=0;n<3;n++){
                                for(int o=0;o<3;o++){
                                    this.qvalue[i][j][k][l][m][n][o] = Math.random();
                                }
                            }
                        }
                    }
                }
            }
        }

        // state, indicesQueue 0初期化
        for(int i=0;i<M+1;i++){
            TagTeamAction tta = new TagTeamAction(RSPEnum.ROCK, RSPEnum.ROCK);
            AgentResult r = new AgentResult(new Result(0,0,0,0, tta, tta), RSPEnum.ROCK);
            this.addState(r);
        }
    }

    public void addState(AgentResult r){
        if(this.state.size() >= M+1) this.state.poll();
        this.state.add(new StateElement(r));
    }

    /**
     * 報酬を与える
     * @param now 状態
     * @return {0.0, -1.0, -2.0, -3.0}:
     * (一人勝ち、どっちかに勝つ),あいこ, どっちかに負ける、二人に負ける
     */
    public double getReward(StateElement now){
        double r = khUtil.RSP1v3(now.myAction, now.enemyActionA, now.enemyActionB);
        if(r == -0.5) r = 0.0;
        return r;
    }

    public Indices nowIndices(List<StateElement> states){
        StateElement before2 = states.get(0);
        StateElement before1 = states.get(1);
        StateElement now = states.get(2);
        return new Indices(before2,before1,now);
    }

    //t+1のときのq値　行動は0ととりあえず置いている(Maxをとる場合とイプシロンで確率的にとる場合があるため)
    public Indices futureIndices(List<StateElement> states){
        StateElement before2 = states.get(1);
        StateElement before1 = states.get(2);
        int now = 0;
        return new Indices(before2,before1,now);
    }
    public double updateNum(double nowQvalue, double reward, double maxQvalue){
        double value = nowQvalue + alpha*(reward - gamma*maxQvalue + nowQvalue);
        return value;
    }

    public void updateQvalue(double reward){
        double maxQvalue = -Double.MAX_VALUE,tmpValue;
        int maxIndex = 0;
        Indices nowId = nowIndices(new ArrayList<StateElement>(this.state));
        double nowQvalue = this.qvalue[nowId.i][nowId.j][nowId.k][nowId.l][nowId.m][nowId.n][nowId.o];//現在のq値
        Indices futureMaxId = futureIndices(new ArrayList<StateElement>(this.state));//s+1のq値
        //次に行動したときの最大のq値を探す
        for(int tmpNum = 0; tmpNum < RSPEnum.values().length; tmpNum++){
            tmpValue = this.qvalue[futureMaxId.i][futureMaxId.j][futureMaxId.k][futureMaxId.l][futureMaxId.m][futureMaxId.n][tmpNum];
            if(tmpValue > maxQvalue){
                maxQvalue = tmpValue;
                maxIndex = tmpNum;
            }
        }
        this.qvalue[nowId.i][nowId.j][nowId.k][nowId.l][nowId.m][nowId.n][nowId.o] = updateNum(nowQvalue,reward,maxQvalue);
    }

    @Override
    public void before() {
    }

    @Override
    public RSPEnum getNextAction() {
        Indices futureMaxId = futureIndices(new ArrayList<StateElement>(this.state));
        double maxQvalue = -Double.MAX_VALUE,tmpValue;
        int maxIndex = 0;
        int idx = 0;
        //確率epsilonでランダムに行動
        if(Math.random() < epsilon){
            double rand = 3.0 * Math.random();
            if(rand < 1.0){
                idx = 0;
            }
            if(rand < 2.0){
                idx = 1;
            }
            if(rand < 3.0){
                idx = 2;
            }
        }else{
            for(int tmpNum = 0; tmpNum < RSPEnum.values().length; tmpNum++){
                tmpValue = this.qvalue[futureMaxId.i][futureMaxId.j][futureMaxId.k][futureMaxId.l][futureMaxId.m][futureMaxId.n][tmpNum];
                if(tmpValue > maxQvalue){
                    maxQvalue = tmpValue;
                    maxIndex = tmpNum;
                }
            }
            idx = maxIndex;
        }
        if(epsilon > 0){//epsilonは1万回の更新で0に収束するようにしている
            epsilon = epsilon - 0.00005;
        }

        return khUtil.choiceRSPEnumByIndex(idx); // 決定的方策
        // return khUtil.choiceRSPEnumByProb(this.weightToProb(w)); // 確率的方策
    }

    @Override
    public void after(AgentResult r) {
        addState(r);
        List<StateElement> states = new ArrayList<StateElement>(this.state);
        StateElement now = states.get(states.size()-1); // 最新Queue
        double reward = getReward(now);
        updateQvalue(reward);
    }

    public void displayStateHistory(){
        System.out.printf("displayStateHistory ");
        for(StateElement i : this.state){
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
                                    System.out.printf("%f ", this.qvalue[i][j][k][l][m][n][o]);
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