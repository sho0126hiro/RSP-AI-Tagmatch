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

    // 獲得ポイント: 勝ち => 1, 負け => 0
    public int PointOfAllyAgentA; // 味方のエージェントAの獲得ポイント
    public int PointOfAllyAgentB; // 味方のエージェントAの獲得ポイント
    public int PointOfEnemyAgentA; // 敵のエージェントBの獲得ポイント
    public int PointOfEnemyAgentB; // 敵のエージェントBの獲得ポイント

    public Result(int pointOfAllyAgentA, int pointOfAllyAgentB, 
        int pointOfEnemyAgentA, int pointOfEnemyAgentB){
            this.PointOfAllyAgentA = pointOfAllyAgentA;
            this.PointOfAllyAgentB = pointOfAllyAgentB;
            this.PointOfEnemyAgentA = pointOfEnemyAgentA;
            this.PointOfEnemyAgentB = pointOfEnemyAgentB;
            this.allyPoint = this.PointOfAllyAgentA + this.PointOfAllyAgentB;
            this.enemyPoint = this.PointOfEnemyAgentA + this.PointOfEnemyAgentB;
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
