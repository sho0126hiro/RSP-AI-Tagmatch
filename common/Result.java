package common;

/**
 *　対戦結果
 */
public class Result {
    public StatusEnum status; // 勝敗を示すフラグ

    /**
     * StatusEnumは分岐用
     * 実際は勝利数（総獲得ポイント）で争うので，このEnumの勝利数（チーム全体としての勝利数）ではないので注意
     */
    public enum StatusEnum {
        WIN, LOSE, DRAW
    }

    public int allyPoint; // 味方の総獲得ポイント
    public int enemyPoint; // 相手の総獲得ポイント

    // 獲得ポイント: 勝ち => 1, 負け => 2
    public int PointOfAllyAgentA; // 味方のエージェントAの獲得ポイント
    public int PointOfAllyAgentB; // 味方のエージェントAの獲得ポイント
    public int PointOfEnemyAgentA; // 敵のエージェントBの獲得ポイント
    public int PointOfEnemyAgentB; // 敵のエージェントBの獲得ポイント

    public TagTeamAction AllyTeamAction; // 今回出したAが出した手
    public TagTeamAction EnemyTeamAction; // 今回出したBが出した手

    public Result(int pointOfAllyAgentA, int pointOfAllyAgentB, 
        int pointOfEnemyAgentA, int pointOfEnemyAgentB, 
        TagTeamAction tagTeamActionA, TagTeamAction tagTeamActionB){
            this.PointOfAllyAgentA = pointOfAllyAgentA;
            this.PointOfAllyAgentB = pointOfAllyAgentB;
            this.PointOfEnemyAgentA = pointOfEnemyAgentA;
            this.PointOfEnemyAgentB = pointOfEnemyAgentB;
            this.allyPoint = this.PointOfAllyAgentA + this.PointOfAllyAgentB;
            this.enemyPoint = this.PointOfEnemyAgentA + this.PointOfEnemyAgentB;
            this.AllyTeamAction = tagTeamActionA;
            this.EnemyTeamAction = tagTeamActionB;
            if(this.allyPoint > this.enemyPoint){
                this.status = StatusEnum.WIN;
            }else if(this.allyPoint == this.enemyPoint){
                this.status = StatusEnum.DRAW;
            }else {
                this.status = StatusEnum.LOSE;
            }
    }

    @Override
    public String toString(){
        return String.format("[status: %s] [Ally] A: %d, B: %d [Enemy] A: %d, B: %d", 
        this.status.name(), this.PointOfAllyAgentA, this.PointOfAllyAgentB, this.PointOfEnemyAgentA, this.PointOfEnemyAgentB);
    }
}
