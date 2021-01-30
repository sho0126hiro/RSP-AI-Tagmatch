package agents;

import common.RSPEnum;

public class IkeAgent {
    Public Queue<RSPEnum> actionQueue = new ArrayDeque<>();
    Public Queue<Result> resultQueue = new ArrayDeque<>();
    Public String teamtag;
    public static final double EPSILON = 0.30;       // ε-greedy法のε
	public static final double ALPHA = 0.10;         // 学習率α
	public static final double GAMMA = 0.90;         // 割引率γ
    public static final int DIM = 5;                 // キューの長さ
    
    public double q[DIM][DIM][3];

    //出した手をキューに追加していく
    public void addActionQueue(RSPEnum action){
        this.actionQueue.add(action);
    }
    //出した手による結果をキューに追加していく
    public void addResultQueue(Result result){
    //    this.actionQueue.add(result.teamtag.ActionA);
        this.actionQueue.add(result);
    }
    
    //乱数をqに入れる
    public void initQ(){
        for(int i = 0;i<DIM;i++){
            for(int j = 0;j<DIM;j++){
                q[i][j] = 0;
            }
        }
    }

    //greedyする
    public RSPEnum greedy(){

    }

    //q値のアップデート
    public void updateQ(int r, int a){
        int maxA = 0;
        for(int i=0;i<3){
            if(){

            }

        }

    }

    
    public void main(String[] args){
        for(int i = 0; i < DIM; i++){

        }


    }

}






