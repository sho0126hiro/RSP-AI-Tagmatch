import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;
import common.Team;
import teams.SampleTeam;
import teams.THTeam;
import teams.IITeam;
import teams.KHTeam;
import teams.KimotoSumizome;

public class BattleManager {
  public Team teamA;
  public Team teamB;
  private boolean ignoresAiko;
  private boolean ignoresLogs;
  private boolean viewResult;

  public BattleManager(boolean ignoresAiko, boolean ignoresLogs, boolean viewResult) {
    this.ignoresAiko = ignoresAiko;
    this.ignoresLogs = ignoresLogs;
    this.viewResult = viewResult;
  }

  public enum TeamName {
    TeamA, TeamB
  }

  private class Desicion {
    // 獲得スコア: 1 or 0 （勝ちor負け）
    public int teamA_agentA_Score; // aチーム agentAの獲得スコア
    public int teamA_agentB_Score; // aチーム agentBの獲得スコア
    public int teamB_agentA_Score; // bチーム agentAの獲得スコア
    public int teamB_agentB_Score; // bチーム agentBの獲得スコア

    public TagTeamAction tagTeamActionA;
    public TagTeamAction tagTeamActionB;

    public Desicion(TagTeamAction a, TagTeamAction b, int a1, int a2, int b1, int b2) {
      this.teamA_agentA_Score = a1;
      this.teamA_agentB_Score = a2;
      this.teamB_agentA_Score = b1;
      this.teamB_agentB_Score = b2;
      this.tagTeamActionA = a;
      this.tagTeamActionB = b;
    }

    /**
     * 対戦結果からResultに変換する Decision: AとBのスコアを保持 Result: "味方"・"敵"のスコアを保持
     * 
     * @param teamName 変換先チームはTeamA or TeamBのどちらか
     * @return
     */
    public Result toResult(TeamName teamName) {
      if (teamName.equals(TeamName.TeamA)) {
        return new Result(this.teamA_agentA_Score, this.teamA_agentB_Score, this.teamB_agentA_Score,
            this.teamB_agentB_Score, this.tagTeamActionA, this.tagTeamActionB);
      }
      return new Result(this.teamB_agentA_Score, this.teamB_agentB_Score, this.teamA_agentA_Score,
          this.teamA_agentB_Score, this.tagTeamActionB, this.tagTeamActionA);
    }

    @Override
    public String toString() {
      return String.format("---SCORE--- [TeamA] a: %d, b: %d [TeamB] a: %d, b: %d", this.teamA_agentA_Score,
          this.teamA_agentB_Score, this.teamB_agentA_Score, this.teamB_agentB_Score);
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
    this.teamA_agentA_Score = 0;
    this.teamA_agentB_Score = 0;
    this.teamB_agentA_Score = 0;
    this.teamB_agentB_Score = 0;
  }

  public boolean isInteger(double d) {
    if ((d % 1) == 0) {
      return true;
    }
    return false;
  }

  public static RSPEnum winnerOf(RSPEnum r) {
    switch (r) {
      case ROCK:
        return RSPEnum.PAPER;

      case SCISORS:
        return RSPEnum.ROCK;

      case PAPER:
        return RSPEnum.SCISORS;
    }
    return r;
  }

  // aがbに勝っているときtrue
  // あいこは勝っていないのでfalse
  public boolean isWin2v2(RSPEnum a, RSPEnum b) {
    var winner = winnerOf(a);
    if (b.equals(winner)) {
      return true;
    }
    return false;
  }

  /**
   * a1, a2 vs b1, b2 の勝敗結果を返す
   */
  private Desicion getDesicion(TagTeamAction teamA, TagTeamAction teamB) {
    RSPEnum[] actions = { teamA.actionA, teamA.actionB, teamB.actionA, teamB.actionB };
    // RSPEnum[] actions = {RSPEnum.ROCK, RSPEnum.SCISORS, RSPEnum.SCISORS,
    // RSPEnum.SCISORS};
    RSPEnum[] rsp = RSPEnum.values();

    int[] count = { 0, 0, 0 }; // グー・チョキ・パーを出したAgentの数
    for (int j = 0; j < actions.length; j++) {
      count[actions[j].getIndex()]++;
    }
    // すべての手が出ていたらあいこ
    boolean isThreeAiko = true;
    for (int j = 0; j < count.length; j++) {
      if (count[j] == 0)
        isThreeAiko = false;
    }
    if (isThreeAiko)
      return new Desicion(teamA, teamB, 0, 0, 0, 0);

    // 4人すべて同じ手だったらあいこ
    for (int j = 0; j < count.length; j++) {
      if (count[j] == 4) {
        return new Desicion(teamA, teamB, 0, 0, 0, 0);
      }
    }

    // この時点で手は2つだけ
    RSPEnum[] twoRSPs = new RSPEnum[2];
    int twoRSPs_i = 0;
    for (int j = 0; j < count.length; j++) {
      if (count[j] > 0) {
        twoRSPs[twoRSPs_i] = rsp[j];
        twoRSPs_i++;
      }
    }

    RSPEnum winner = isWin2v2(twoRSPs[0], twoRSPs[1]) ? twoRSPs[1] : twoRSPs[0];

    int[] scores = { 0, 0, 0, 0 };

    for (int j = 0; j < 4; j++) {
      if (actions[j] == winner) {
        scores[j]++;
      }
    }

    return new Desicion(teamA, teamB, scores[0], scores[1], scores[2], scores[3]);
  }

  private long teamA_agentA_Score = 0;
  private long teamA_agentB_Score = 0;
  private long teamB_agentA_Score = 0;
  private long teamB_agentB_Score = 0;

  /**
   * 対戦を行う
   */
  private boolean buttle(int k) {
    this.teamA.before();
    this.teamB.before();
    TagTeamAction a = this.teamA.getAction();
    TagTeamAction b = this.teamB.getAction();
    Desicion d = getDesicion(a, b);
    this.teamA.after(d.toResult(TeamName.TeamA));
    this.teamB.after(d.toResult(TeamName.TeamB));

    this.teamA_agentA_Score += d.teamA_agentA_Score;
    this.teamA_agentB_Score += d.teamA_agentB_Score;
    this.teamB_agentA_Score += d.teamB_agentA_Score;
    this.teamB_agentB_Score += d.teamB_agentB_Score;

    // あいことなる条件をやや無理やり実装
    var isAiko = d.teamA_agentA_Score == 0 && d.teamA_agentB_Score == 0 && d.teamB_agentA_Score == 0
        && d.teamB_agentB_Score == 0;

    /*
     * System.out.println(a); System.out.println(b); System.out.println(d);
     */
    if (!ignoresLogs && (!isAiko || !ignoresAiko)) {
      this.writer.println(String.format("%d, %d, %d, %d, %d", k, d.teamA_agentA_Score, d.teamA_agentB_Score,
          d.teamB_agentA_Score, d.teamB_agentB_Score));
    }

    return !isAiko;
  }

  class SampleTeam2 extends SampleTeam {
    public TagTeamAction getAction() {
      return new TagTeamAction(RSPEnum.ROCK, RSPEnum.PAPER);
    };
  }

  PrintWriter writer = null;

  /**
   * チーム設定および対戦の実行
   */
  public void run() {
    File file = new File("./a.log");

    try {
      this.writer = new PrintWriter(new FileWriter(file, false));

      // ここにチームインスタンスを置く
      Team[] teams = { new KimotoSumizome(), new THTeam(), new IITeam(), new KHTeam() };
      // 総当りになるようにする必要あり
      // 50000回ループ * 5回戦，結果出力などの処理が必要
      for (int i = 0; i < teams.length; i++) {
        for (int j = i + 1; j < teams.length; j++) {
          this.config(teams[i], teams[j]);
          this.init();
          var teamAName = teams[i].getClass().getSimpleName();
          var teamBName = teams[j].getClass().getSimpleName();
          var aikos = 0l;
          if (!ignoresLogs) {
            writer.println("\n回数, " + teamAName + "のAの獲得スコア, " + teamAName + "のBの獲得スコア, " + teamBName + "のAの獲得スコア, "
                + teamBName + "のBの獲得スコア");
          }
          for (int k = 0; k < 10000;) {
            if (this.buttle(k)) {
              aikos = 0;
              k++;
            } else {
              // あいこが10000回続いたら切る
              aikos++;
              if (aikos > 10000) {
                break;
              }
            }
          }

          if (viewResult) {
            writer.println(teamAName + "のAの獲得スコア: " + this.teamA_agentA_Score + ", " + teamAName + "のBの獲得スコア: "
                + this.teamA_agentB_Score + ", " + teamBName + "のAの獲得スコア: " + this.teamB_agentA_Score + ", " + teamBName
                + "のBの獲得スコア: " + this.teamB_agentB_Score);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (this.writer != null) {
        this.writer.close();
      }
    }

    // CSVで出力します
    // バトルが切り替わったタイミングでは"\n\n"が入るので簡単に分割できるはず
  }

}
