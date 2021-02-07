package agents.KSTeam;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

/**
 * Agentのサンプル 一つの手しか出さない
 */
public class Kimoto implements IAgent {

  KSPredictor pred;
  TagTeamAction action;

  public Kimoto() {
    this.pred = new KSPredictor(100);
  }

  public RSPEnum getAction() {
    this.action = pred.getAction();
    return this.action.actionA;
  }

  public RSPEnum getSecondAction() {
    return this.action.actionB;
  }

  public void after(Result r) {
    pred.after(r);
  }

  public void init() {
    pred.init();
  }

}