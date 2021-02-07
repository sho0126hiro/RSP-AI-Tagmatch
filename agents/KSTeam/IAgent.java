package agents.KSTeam;

import common.RSPEnum;
import common.Result;

public interface IAgent {
  public RSPEnum getAction();

  public void after(Result r);

  public void init();
}
