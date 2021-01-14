import java.util.Arrays;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;
import common.Team;
import teams.SampleTeam;

public class ButtleManager {
    public Team teamA;
    public Team teamB;

    public enum TeamName {
        TeamA, TeamB
    }

    private class Desicion {
        // 獲得スコア: 1 or 0 （勝ちor負け）
        public int teamA_agentA_Score; // aチーム agentAの獲得スコア
        public int teamA_agentB_Score; // aチーム agentBの獲得スコア
        public int teamB_agentA_Score; // bチーム agentAの獲得スコア
        public int teamB_agentB_Score; // bチーム agentBの獲得スコア

        public Desicion(int a1, int a2, int b1, int b2) {
            this.teamA_agentA_Score = a1;
            this.teamA_agentB_Score = a2;
            this.teamB_agentA_Score = b1;
            this.teamB_agentB_Score = b2;
        }

        /**
         * 対戦結果からResultに変換する
         * Decision: AとBのスコアを保持
         * Result:  "味方"・"敵"のスコアを保持
         * @param teamName 変換先チームはTeamA or TeamBのどちらか
         * @return
         */
        public Result toResult(TeamName teamName) {
            if (teamName.equals(TeamName.TeamA)) {
                return new Result(this.teamA_agentA_Score, this.teamA_agentB_Score, this.teamB_agentA_Score,
                        this.teamB_agentB_Score);
            }
            return new Result(this.teamB_agentA_Score, this.teamB_agentB_Score, this.teamA_agentA_Score,
                    this.teamA_agentB_Score);
        }

        @Override
        public String toString(){
            return String.format("---SCORE--- [TeamA] a: %d, b: %d [TeamB] a: %d, b: %d", 
            this.teamA_agentA_Score, this.teamA_agentB_Score, this.teamB_agentA_Score, this.teamB_agentB_Score);
        }
    }

    /**
     * team <a> vs team <b> を定義
     */
    private void config(Team teamA, Team teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
    }

    /**
     * 初期化
     */
    private void init() {
        this.teamA.init();
        this.teamB.init();
    }

    public boolean isInteger(double d) {
        if ((d % 1) == 0) {
            return true;
        }
        return false;
    }

    // aがbに勝っているときtrue
    // あいこは勝っていないのでfalse
    public boolean isWin2v2(RSPEnum a, RSPEnum b) {
        switch (a) {
            case ROCK:
                if (b.equals(RSPEnum.PAPER))
                    return false;
                if (b.equals(RSPEnum.SCISORS))
                    return true;
            case SCISORS:
                if (b.equals(RSPEnum.ROCK))
                    return false;
                if (b.equals(RSPEnum.PAPER))
                    return true;
            case PAPER:
                if (b.equals(RSPEnum.SCISORS))
                    return false;
                if (b.equals(RSPEnum.ROCK))
                    return true;
        }
        return false;
    }

    /**
     * a1, a2 vs b1, b2 の勝敗結果を返す
     */
    private Desicion getDesicion(TagTeamAction teamA, TagTeamAction teamB) {
        RSPEnum[] actions = { teamA.actionA, teamA.actionB, teamB.actionA, teamB.actionB };
        // RSPEnum[] actions = {RSPEnum.ROCK, RSPEnum.PAPER, RSPEnum.PAPER, RSPEnum.PAPER};
        RSPEnum[] rsp = RSPEnum.values();

        int[] count = { 0, 0, 0 }; // グー・チョキ・パーを出したAgentの数
        for (int j = 0; j < actions.length; j++) {
            count[actions[j].getIndex()]++;
        }
        // すべての手が出ていたらあいこ
        Boolean flag = true;
        for (int j = 0; j < count.length; j++) {
            if (count[j] == 0) flag = false;
        }
        if(flag) return new Desicion(0,0,0,0);
        int[] max = { 0, 0 }; // {num, index}
        for (int j = 0; j < count.length; j++) {
            if (max[0] < count[j]) {
                max[0] = count[j];
                max[1] = j;
            }
        }
        int tmpIndex = 0;
        int winIndex = 0;
        int[] scores = { 0, 0, 0, 0 }; // agentごとの獲得スコア
        switch (max[0]) {
            case 4: // 全エージェントが同じ手を出しているとき
                return new Desicion(0, 0, 0, 0);
            case 2:
                for (int j = 0; j < count.length; j++) {
                    if (j == max[1])
                        continue;
                    if (count[j] == 2)
                        tmpIndex = j;
                }
            case 3:
                for (int j = 0; j < count.length; j++) {
                    if (j == max[1])
                        continue;
                    if (count[j] == 1)
                        tmpIndex = j;
                }
        }
        if (isWin2v2(rsp[max[1]], rsp[tmpIndex]))
            winIndex = max[1];
        else
            winIndex = tmpIndex;
        for (int i = 0; i < actions.length; i++) {
            if (actions[i] == rsp[winIndex])
                scores[i]++;
        }
        return new Desicion(scores[0], scores[1], scores[2], scores[3]);
    }

    /**
     * 対戦を行う
     */
    private void buttle() {
        this.teamA.before();
        this.teamB.before();
        TagTeamAction a = this.teamA.getAction();
        TagTeamAction b = this.teamB.getAction();
        Desicion d = getDesicion(a, b);
        this.teamA.after(d.toResult(TeamName.TeamA));
        this.teamA.after(d.toResult(TeamName.TeamB));
        System.out.println(a);
        System.out.println(b);
        System.out.println(d);
    }

    /**
     * チーム設定および対戦の実行
     */
    public void run() {
        Team SampleTeam1 = new SampleTeam();
        Team SampleTeam2 = new SampleTeam();
        // 総当りになるようにする必要あり
        this.config(SampleTeam1, SampleTeam2);
        this.init();
        // 50000回ループ * 5回戦，結果出力などの処理が必要
        this.buttle();
    }

}