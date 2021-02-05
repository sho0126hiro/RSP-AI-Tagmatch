package teams;

import agents.SampleAgent;
import common.Team;
import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

/**
 * チームのサンプル 内部通信はせず，結果も使わない
 */
public class SampleTeam implements Team {
  private SampleAgent agentA;
  private SampleAgent agentB;

  public void init() {
    agentA = new SampleAgent(RSPEnum.SCISORS);
    agentB = new SampleAgent(RSPEnum.ROCK);
  }

  public void before() {

  };

  public void after(Result r) {
    System.out.println(r);
  };

  public TagTeamAction getAction() {
    return new TagTeamAction(agentA.getAction(), agentB.getAction());
  };
}
