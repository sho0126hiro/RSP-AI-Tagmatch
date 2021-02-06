package agents;

import common.RSPEnum;
import common.Result;

import java.util.Arrays;
import java.util.Random;

/**
 * 1AS07岩崎悠紀のエージェントプログラム（Q-learning）
 */
public class IwasakiAgent {
    public static final double EPSILON = 0.1;
    public static final double ALPHA = 0.2;
    public static final double GAMMA = 0.0;
    public static final String[] RSPMAP = {"ROCK", "SCISORS", "PAPER"};

    // Q値は【自分の現在の手＆味方の現在の手】【相手チームの手】【味方の次の手＆自分の次の手】
    private double q[][][];

    private RSPEnum teamA1;
    private RSPEnum teamA2;
    private RSPEnum teamB1;
    private RSPEnum teamB2;

    public IwasakiAgent(){
        this.teamA1 = RSPEnum.ROCK;
        this.teamA2 = RSPEnum.ROCK;
        this.teamB1 = RSPEnum.ROCK;
        this.teamB2 = RSPEnum.ROCK;
        this.q = new double[9][9][3];
        this.initQ();
    }

    // Q値の初期化（0~1の乱数）
    public void initQ() {
        Random rnd = new Random(); 
        for(int i=0; i<9; i++){
            for(int j=0; j<9; j++){
                for(int k=0; k<3; k++){
                    this.q[i][j][k] = rnd.nextDouble()*0.1 - 0.05;
                }
            }
        }
        return;
    }

    public void update(Result result) {
        double reward=0.0;

        RSPEnum teamA1 = result.AllyTeamAction.actionA;
        RSPEnum teamA2 = result.AllyTeamAction.actionB;
        RSPEnum teamB1 = result.EnemyTeamAction.actionA;
        RSPEnum teamB2 = result.EnemyTeamAction.actionB;

        int maxIndex = -1;
        int myTeam = teamA1.getIndex()*3 + teamA2.getIndex();
        int oppTeam = teamB1.getIndex()*3 + teamB2.getIndex();
        int myTeamOld = this.teamA1.getIndex()*3 + this.teamA2.getIndex();
        int oppTeamOld = this.teamB1.getIndex()*3 + this.teamB2.getIndex();

        // 勝ち負けを判定して報酬を決定する
        if (isWin(teamA1, teamA2, teamB1, teamB2)) {
            reward = 1.0;
        } else if (isLoose(teamA1, teamA2, teamB1, teamB2)) {
            reward = -1.0;
        } else if (isDraw(teamA1, teamA2, teamB1, teamB2)) {
            reward = 0.0;
        }

        // 出された手から，最大になるQ値のインデックスを取得
        for(int i=0; i<3; i++){
            if (maxIndex == -1) {
                maxIndex = i;
            } else if (this.q[myTeam][oppTeam][i] > this.q[myTeam][oppTeam][maxIndex]) {
                maxIndex = i;
            }
        }

        if (reward != 0.0) {
            // Q値の更新
            q[myTeamOld][oppTeamOld][teamA1.getIndex()] = 
                (1-ALPHA) * q[myTeamOld][oppTeamOld][teamA1.getIndex()]
                + ALPHA * (reward + GAMMA * q[myTeam][oppTeam][maxIndex]);
        }

        this.teamA1 = teamA1;
        this.teamA2 = teamA2;
        this.teamB1 = teamB1;
        this.teamB2 = teamB2;
    }

    private static boolean isWin(RSPEnum teamA1, RSPEnum teamA2, RSPEnum teamB1, RSPEnum teamB2) {
        if (teamA2.getIndex() == winTo(teamA1.getIndex()) ||
                teamB1.getIndex() == winTo(teamA1.getIndex()) ||
                teamB2.getIndex() == winTo(teamA1.getIndex())) {
            // 自分に対して勝てる手を出している人がいればfalse（あいこの可能性もある）
            return false;
        } else if (teamA2.getIndex() == teamA1.getIndex() &&
                teamB1.getIndex() == teamA1.getIndex() &&
                teamB2.getIndex() == teamA1.getIndex()) {
            // 全員自分と同じ手ならばあいこなのでfalse
            return false;
        } else {
            return true;
        }
    }

    private static boolean isLoose(RSPEnum teamA1, RSPEnum teamA2, RSPEnum teamB1, RSPEnum teamB2) {
        if (teamA2.getIndex() == looseTo(teamA1.getIndex()) ||
                teamB1.getIndex() == looseTo(teamA1.getIndex()) ||
                teamB2.getIndex() == looseTo(teamA1.getIndex())) {
            // 自分に対して負ける手を出している人がいればfalse（あいこの可能性もある）
            return false;
        } else if (teamA2.getIndex() == teamA1.getIndex() ||
                teamB1.getIndex() == teamA1.getIndex() ||
                teamB2.getIndex() == teamA1.getIndex()) {
            // 全員自分と同じ手ならばあいこなのでfalse
            return false;
        } else {
            return true;
        }
    }

    private static boolean isDraw(RSPEnum teamA1, RSPEnum teamA2, RSPEnum teamB1, RSPEnum teamB2) {
        if (isWin(teamA1, teamA2, teamB1, teamB2) || isLoose(teamA1, teamA2, teamB1, teamB2)) {
            return false;
        } else {
            return true;
        }
    }

    private static int winTo(int index) {
        if (index == 0) {
            return 2;
        } else {
            return index - 1;
        }
    }

    private static int looseTo(int index) {
        if (index == 2) {
            return 0;
        } else {
            return index + 1;
        }
    }

    // 貪欲法
    public RSPEnum eGreedy() {
        Random rnd = new Random();
        int maxIndex = -1;
        int myTeam = this.teamA1.getIndex()*3 + this.teamA2.getIndex();
        int opTeam = this.teamB1.getIndex()*3 + this.teamB2.getIndex();
        int currIndex;

        if (rnd.nextDouble() > EPSILON) {
            for(int i=0; i<3; i++) {
                currIndex = i;
                if (maxIndex == -1) {
                    maxIndex = currIndex;
                }else if (q[myTeam][opTeam][currIndex] > q[myTeam][opTeam][maxIndex]) {
                    maxIndex = currIndex;
                }
            }
        } else {
            maxIndex = rnd.nextInt(2);
        }

        return RSPEnum.valueOf(RSPMAP[maxIndex]);
    }

    public RSPEnum getAction(){
        return this.eGreedy();
    }
}
