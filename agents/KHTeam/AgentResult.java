package agents.KHTeam;

import common.RSPEnum;
import common.Result;

/**
 * 各エージェント自体の対戦結果を示す
 */
public class AgentResult {
    private KHUtil khUtil = new KHUtil();
    public RSPEnum myAction; // 自分の行動
    public RSPEnum enemyActionA; // 敵Aの行動
    public RSPEnum enemyActionB; // 敵Bの行動
    public int isWin; // エージェント対敵 1: エージェントの勝ち、-0: 引き分け, -1: エージェントの負け

    public AgentResult(Result r, RSPEnum myAction){
        this.myAction = myAction;
        this.enemyActionA = r.EnemyTeamAction.actionA;
        this.enemyActionB = r.EnemyTeamAction.actionB;
        RSPEnum[] actions = {this.myAction, this.enemyActionA, this.enemyActionB};
        int[] count = {0,0,0}; 
        RSPEnum[] rsp = RSPEnum.values();
        for(int j=0; j<actions.length; j++){
            count[actions[j].getIndex()]++;
        }
        Boolean flag = true;
        for(int j=0; j<actions.length; j++){
            if(count[j] == 0) flag = false;
        }
        // あいこ
        if(flag) {
            this.isWin = 0;
            return;
        }
        int[] max = {0, 0}; // num, index
        for(int j=0;j<count.length;j++){
            if(max[0] < count[j]){
                max[0] = count[j];
                max[1] = j;
            }
        }
        int tmpIndex = 0;
        switch(max[0]){
            case 3:
                this.isWin = 0; // あいこ
                return;
            case 2:
                if(this.myAction.getIndex() == max[1]){
                    // 1回しか登場していないやつを探す
                    for (int j = 0; j < count.length; j++) {
                        if (j == max[1])
                            continue;
                        if (count[j] == 1)
                            tmpIndex = j;
                    }
                    if(khUtil.RSP1v1(rsp[max[1]], rsp[tmpIndex]) == 1) this.isWin = 1;
                    else this.isWin = -1;
                    return;
                } else {
                    for (int j = 0; j < count.length; j++) {
                        if (j == max[1])
                            continue;
                        if (count[j] == 2)
                            tmpIndex = j;
                    }
                    if(khUtil.RSP1v1(rsp[max[1]], rsp[tmpIndex]) == 1) this.isWin = 1;
                    else this.isWin = -1;
                    return;
                }
        }
                
    }

    @Override
    public String toString(){
            return "[AgentResult][isWin: "+ this.isWin +"] my: " + myAction.name() + ", enemyA: " + enemyActionA.name() + ", enemyB: " + enemyActionB.name();
    }

}
