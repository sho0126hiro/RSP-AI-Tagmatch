package agents;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

import java.util.Random;

public class IkeAgent {
    //Public Queue<RSPEnum> actionQueue = new ArrayDeque<>();
    //Public Queue<Result> resultQueue = new ArrayDeque<>();
    public static final String[] RSPMAP = {"ROCK", "SCISORS", "PAPER"};
    
    public String myTeamTag;
    public static final double EPSILON = 0.30;       // ε-greedy法のε
	public static final double ALPHA = 0.10;         // 学習率α
	public static final double GAMMA = 0.90;         // 割引率γ
    public RSPEnum action;
    public double q[][][];   //自分たちの手3x3=9通り，相手の手9通り，勝ち負けあいこで3通り
    public Result result;
    /*
      G C P G C P G C P
    G
    C
    P
    G
    C
    P
    G
    C
    P
    */

    /*
    //出した手をキューに追加していく
    public void addActionQueue(RSPEnum action){
        this.actionQueue.add(action);
    }
    //出した手による結果をキューに追加していく
    public void addResultQueue(Result result){
    //    this.actionQueue.add(result.teamtag.ActionA);
        this.actionQueue.add(result);
    }*/

    public IkeAgent(){
        this.q = new double[9][9][3];
        this.result = new Result(0, 0, 0, 0, 
                new TagTeamAction(RSPEnum.ROCK, RSPEnum.ROCK), 
                new TagTeamAction(RSPEnum.ROCK, RSPEnum.ROCK));
        this.initQ();
    }
    
    //乱数をqに入れる
    public void initQ(){
        Random rnd = new Random();
        for(int i = 0;i<9;i++){
            for(int j = 0;j<9;j++){
                for(int k=0;k<3;k++){
                    this.q[i][j][k] = rnd.nextDouble();
                }
            }
        }
    }

    //greedyする
    public RSPEnum greedy(){
        Random rnd = new Random();
        int maxIndex = -1;
        TagTeamAction myTeam;
        TagTeamAction emTeam;
    
        int myTeamIndex;
        int emTeamIndex;
        int index;

        if(myTeamTag == "a"){
            myTeam = result.AllyTeamAction;
            emTeam = result.EnemyTeamAction;
        }else{
            emTeam = result.AllyTeamAction;
            myTeam = result.EnemyTeamAction;
        }
        myTeamIndex = myTeam.actionA.getIndex()*3 + myTeam.actionB.getIndex();
        emTeamIndex = emTeam.actionA.getIndex()*3 + emTeam.actionB.getIndex();

        if(rnd.nextDouble() > EPSILON){
            for(int i=0; i<3; i++){
                index = i;
                if(maxIndex  == -1){
                    maxIndex = index;
                }else if(q[myTeamIndex][emTeamIndex][index] > q[myTeamIndex][emTeamIndex][maxIndex]){
                    maxIndex = index;
                }
            }
        }else{
            maxIndex = rnd.nextInt(2);
        }
        return RSPEnum.valueOf(RSPMAP[maxIndex]);
    }

    //q値のアップデート
    public void update(Result result){
        int maxIndex = -1;
        TagTeamAction myTeam;
        TagTeamAction emTeam;
    
        int myTeamIndex;
        int emTeamIndex;
        int pastMyIndex;
        int pastEmIndex;

        //報酬の設定
        double reward = result.PointOfAllyAgentB;


        if(myTeamTag == "a"){
            myTeam = result.AllyTeamAction;
            emTeam = result.EnemyTeamAction;
        }else{
            emTeam = result.AllyTeamAction;
            myTeam = result.EnemyTeamAction;
        }
        
        myTeamIndex = myTeam.actionA.getIndex()*3 + myTeam.actionB.getIndex();
        emTeamIndex = emTeam.actionA.getIndex()*3 + emTeam.actionB.getIndex();

        //q値が最大になる場所を見る
        for(int i=0;i<3;i++){
            if(maxIndex == -1){
                maxIndex = i;
            }else if(this.q[myTeamIndex][emTeamIndex][i] > this.q[myTeamIndex][emTeamIndex][maxIndex]){
                maxIndex = i;
            }
        }


        if(myTeamTag == "a"){
            pastMyIndex = this.result.AllyTeamAction.actionA.getIndex()*3 + this.result.AllyTeamAction.actionB.getIndex();
            pastEmIndex = this.result.EnemyTeamAction.actionA.getIndex()*3 + this.result.EnemyTeamAction.actionB.getIndex();
        }else{
            pastEmIndex = this.result.AllyTeamAction.actionA.getIndex()*3 + this.result.AllyTeamAction.actionB.getIndex();
            pastMyIndex = this.result.EnemyTeamAction.actionA.getIndex()*3 + this.result.EnemyTeamAction.actionB.getIndex();
        }

        //q値を更新する
        q[pastMyIndex][pastEmIndex][this.result.AllyTeamAction.actionB.getIndex()] = (1-ALPHA) * q[pastMyIndex][pastEmIndex][this.result.AllyTeamAction.actionB.getIndex()] + ALPHA * (reward * GAMMA * q[myTeamIndex][emTeamIndex][maxIndex]);
        this.result = result;
    }

    public RSPEnum getAction(){
        return this.greedy();
    }
}






