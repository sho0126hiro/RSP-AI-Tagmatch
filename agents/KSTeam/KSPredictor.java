package agents.KSTeam;

import java.util.LinkedList;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

public class KSPredictor {
  private class JankenLog {
    /**
     * こちら方Aのじゃんけんログ
     */
    public final RSPEnum allyA;

    /**
     * こちら方Bのじゃんけんログ
     */
    public final RSPEnum allyB;
    /**
     * 相手方Aのじゃんけんログ
     */
    public final RSPEnum enemyA;
    /**
     * 相手方Bのじゃんけんログ
     */
    public final RSPEnum enemyB;

    public JankenLog(RSPEnum allyA, RSPEnum allyB, RSPEnum enemyA, RSPEnum enemyB) {
      this.allyA = allyA;
      this.allyB = allyB;
      this.enemyA = enemyA;
      this.enemyB = enemyB;
    }
  }

  private class RSPFilter {
    public double hR[];
    public double hS[];
    public double hP[];

    public RSPFilter(int n) {
      this.hR = new double[n];
      this.hS = new double[n];
      this.hP = new double[n];
    }

    public void zeros() {
      for (int i = 0; i < n; i++) {
        this.hR[i] = 0.0;
        this.hS[i] = 0.0;
        this.hP[i] = 0.0;
      }
    }

    private String doubleAToString(double[] a) {
      StringBuilder sb = new StringBuilder("[");
      for (double d : a) {
        sb.append(d);
        sb.append(",");
      }
      sb.append("]");
      return sb.toString();
    }

    @Override
    public String toString() {

      return "R:" + doubleAToString(this.hR) + "\nS" + doubleAToString(this.hS) + "\nP" + doubleAToString(this.hP);
    }
  }

  private LinkedList<JankenLog> jankenLog;

  /**
   * N どこまで深く取るか
   */
  private final int n;

  /**
   * 相手Aの可変フィルタ
   */
  private RSPFilter hA;

  /**
   * 相手Bの可変フィルタ
   */
  private RSPFilter hB;

  /**
   * グー・チョキ・パーが均衡かどうか
   */
  private boolean unbias = true;

  public KSPredictor(int n) {
    this.jankenLog = new LinkedList<>();
    this.n = n;
    this.hA = new RSPFilter(this.n);
    this.hA.zeros();
    this.hB = new RSPFilter(this.n);
    this.hB.zeros();
  }

  public KSPredictor() {
    this(100);
  }

  public double[] getFlattenJankenLog() {
    double[] flattenJankenLog = new double[this.n * 4 * 3];
    for (int i = 0; i < this.n * 4 * 3; i++) {
      flattenJankenLog[i] = 0.0;
    }

    if (this.jankenLog.size() == 0) {
      return flattenJankenLog;
    }

    var iter = this.jankenLog.iterator();
    JankenLog next = this.jankenLog.getFirst();
    for (int i = 0; iter.hasNext() && i < this.n; next = iter.next(), i++) {
      for (int j = 0; j < 4; j++) {
        RSPEnum n = next.allyA;
        switch (j) {
          case 0:
            n = next.allyA;
            break;
          case 1:
            n = next.allyB;
            break;
          case 2:
            n = next.enemyA;
            break;
          case 3:
            n = next.enemyB;
            break;
        }
        for (int k = 0; k < 3; k++) {
          switch (k) {
            case 0:
              flattenJankenLog[4 * 3 * i + 3 * j + k] = n == RSPEnum.ROCK ? 1.0 : 0.0;
              break;
            case 1:
              flattenJankenLog[4 * 3 * i + 3 * j + k] = n == RSPEnum.SCISORS ? 1.0 : 0.0;
              break;
            case 2:
              flattenJankenLog[4 * 3 * i + 3 * j + k] = n == RSPEnum.PAPER ? 1.0 : 0.0;
              break;

          }
        }
      }
    }
    return flattenJankenLog;
  }

  public double[] shiftRSP(double[] a) {
    var r = new double[this.n * 4 * 3];
    for (int i = 0; i < this.n; i++) {
      for (int j = 0; j < 4; j++) {
        r[4 * 3 * i + 3 * j + 0] = a[4 * 3 * i + 3 * j + 1];
        r[4 * 3 * i + 3 * j + 1] = a[4 * 3 * i + 3 * j + 2];
        r[4 * 3 * i + 3 * j + 2] = a[4 * 3 * i + 3 * j + 0];
      }
    }
    return r;
  }

  public static double[] shift(double n, double[] a) {
    double[] r = new double[a.length];
    for (int i = a.length - 1; i >= 1; i--) {
      r[i] = a[i - 1];
    }
    r[0] = n;
    return r;
  }

  public static double dot(double[] a, double[] b) {
    double r = 0.0;
    for (int i = 0; i < Math.min(a.length, b.length); i++) {
      r += a[i] * b[i];
    }
    return r;
  }

  public static double[] plus(double[] a, double[] b) {
    var length = Math.min(a.length, b.length);
    var r = new double[length];
    for (int i = 0; i < length; i++) {
      r[i] = a[i] + b[i];
    }
    return r;
  }

  public static double[] ctimes(double a, double[] b) {
    var r = new double[b.length];
    for (int i = 0; i < b.length; i++) {
      r[i] = a * b[i];
    }
    return r;
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

  public static TagTeamAction winnerOf(TagTeamAction r) {
    return new TagTeamAction(winnerOf(r.actionA), winnerOf(r.actionB));
  }

  public static RSPEnum loserOf(RSPEnum r) {
    return winnerOf(winnerOf(r));
  }

  public void init() {
    this.jankenLog = new LinkedList<>();
    this.hA = new RSPFilter(this.n);
    this.hB = new RSPFilter(this.n);
  }

  private void update(TagTeamAction allyTeamAction, TagTeamAction enemyTeamAction, double[] x) {
    var x_norm_2 = dot(x, x);
    var mu = 1.0 / (x_norm_2 + 0.00001);

    double dA[] = { enemyTeamAction.actionA == RSPEnum.ROCK ? 1.0 : 0.0,
        enemyTeamAction.actionA == RSPEnum.SCISORS ? 1.0 : 0.0, enemyTeamAction.actionA == RSPEnum.PAPER ? 1.0 : 0.0 };
    double yA[] = { dot(x, this.hA.hR), dot(x, this.hA.hS), dot(x, this.hA.hP) };
    var eA = plus(dA, ctimes(-1.0, yA));
    this.hA.hR = plus(this.hA.hR, ctimes(mu * eA[0], x));
    this.hA.hS = plus(this.hA.hS, ctimes(mu * eA[1], x));
    this.hA.hP = plus(this.hA.hP, ctimes(mu * eA[2], x));

    double dB[] = { enemyTeamAction.actionB == RSPEnum.ROCK ? 1.0 : 0.0,
        enemyTeamAction.actionB == RSPEnum.SCISORS ? 1.0 : 0.0, enemyTeamAction.actionB == RSPEnum.PAPER ? 1.0 : 0.0 };
    double yB[] = { dot(x, this.hB.hR), dot(x, this.hB.hS), dot(x, this.hB.hP) };
    var eB = plus(dB, ctimes(-1.0, yB));
    this.hB.hR = plus(this.hB.hR, ctimes(mu * eB[0], x));
    this.hB.hS = plus(this.hB.hS, ctimes(mu * eB[1], x));
    this.hB.hP = plus(this.hB.hP, ctimes(mu * eB[2], x));
  }

  public void after(Result r) {
    var allyTeamAction = r.AllyTeamAction;
    var enemyTeamAction = r.EnemyTeamAction;
    var x = this.getFlattenJankenLog();
    update(allyTeamAction, enemyTeamAction, x);
    if (unbias) {
      update(winnerOf(allyTeamAction), winnerOf(enemyTeamAction), shiftRSP(x));
      update(winnerOf(winnerOf(allyTeamAction)), winnerOf(winnerOf(enemyTeamAction)), shiftRSP(shiftRSP(x)));
    } else {
      update(allyTeamAction, enemyTeamAction, x);
      update(allyTeamAction, enemyTeamAction, x);
    }

    this.jankenLog.addFirst(new JankenLog(allyTeamAction.actionA, allyTeamAction.actionB, enemyTeamAction.actionA,
        enemyTeamAction.actionB));
  }

  private RSPEnum[] predict() {
    var x = this.getFlattenJankenLog();
    RSPEnum[] r = new RSPEnum[2];
    double yA[] = { dot(x, this.hA.hR), dot(x, this.hA.hS), dot(x, this.hA.hP) };
    double yB[] = { dot(x, this.hB.hR), dot(x, this.hB.hS), dot(x, this.hB.hP) };
    if (yA[0] >= yA[1] && yA[0] >= yA[2]) {
      r[0] = RSPEnum.ROCK;
    } else if (yA[1] >= yA[0] && yA[1] >= yA[2]) {
      r[0] = RSPEnum.SCISORS;
    } else {
      r[0] = RSPEnum.PAPER;
    }

    if (yB[0] >= yB[1] && yB[0] >= yB[2]) {
      r[1] = RSPEnum.ROCK;
    } else if (yB[1] >= yB[0] && yB[1] >= yB[2]) {
      r[1] = RSPEnum.SCISORS;
    } else {
      r[1] = RSPEnum.PAPER;
    }
    return r;
  }

  public TagTeamAction getAction() {
    var p = this.predict();
    /*
     * System.out.println("hA:\n" + this.hA); System.out.println("hB:\n" + this.hB);
     */
    if (p[0].equals(p[1])) {
      return new TagTeamAction(winnerOf(p[0]), winnerOf(p[1]));
    }
    if (winnerOf(p[0]).equals(p[1])) {
      return new TagTeamAction(p[1], p[1]);
    }
    return new TagTeamAction(p[0], p[0]);
  }
}