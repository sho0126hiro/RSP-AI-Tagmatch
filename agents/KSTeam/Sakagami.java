package agents.KSTeam;

import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

/**
 * Agentのサンプル 一つの手しか出さない
 */
public class Sakagami implements IAgent {

  TagTeamAction action;
  Kimoto k;

  public Sakagami(Kimoto k) {
    this.k = k;
  }

  public RSPEnum getAction() {
    return k.getSecondAction();
  }

  @Override
  public void after(Result r) {

  }

  @Override
  public void init() {

  }

}