package KSTeam.agents;

import common.RSPEnum;

/**
 * Agentのサンプル 一つの手しか出さない
 */
public class SampleAgent {
  public RSPEnum ret;

  public SampleAgent(RSPEnum ret) {
    this.ret = ret;
  }

  public RSPEnum getAction() {
    return ret;
  }
}