package agents.KHTeam;

import common.RSPEnum;
import common.Result;
import common.Result.StatusEnum;

/**
 * 各エージェント自体の対戦結果を示す
 */
public class AgentResult {
    public RSPEnum myAction; // 自分の行動
    public RSPEnum allyAction; // 自分以外の味方の行動
    public RSPEnum enemyActionA; // 敵Aの行動
    public RSPEnum enemyActionB; // 敵Bの行動

    public StatusEnum teamStatus; // チームの勝敗結果
    public StatusEnum agentStatus; // エージェント個人の勝敗
    
    public int allyPoint; // 味方チームの獲得ポイント
    public int enemyPoint; // 相手チームの獲得ポイント

    public int myPoint; // 自分の獲得ポイント

    // 獲得ポイント: 勝ち => 1, 負け => 0
    public int PointOfAllyAgentA; // 味方のエージェントAの獲得ポイント
    public int PointOfAllyAgentB; // 味方のエージェントAの獲得ポイント
    public int PointOfEnemyAgentA; // 敵のエージェントBの獲得ポイント
    public int PointOfEnemyAgentB; // 敵のエージェントBの獲得ポイント

    public AgentResult(Result r, String type){
        if(type.equals("A")){
            this.myAction = r.AllyTeamAction.actionA;
            this.allyAction = r.AllyTeamAction.actionB;
        } else {
            this.myAction = r.AllyTeamAction.actionB;
            this.allyAction = r.AllyTeamAction.actionA;
        }
        this.enemyActionA = r.EnemyTeamAction.actionA;
        this.enemyActionB = r.EnemyTeamAction.actionB;
    }

    /**
     * もし戦っていたら、どの様な結果になっていたかを返す。
     * @param r
     * @param myAction: 自分の行動
     */
    public AgentResult(Result r, RSPEnum myAction){
           
    }

}
