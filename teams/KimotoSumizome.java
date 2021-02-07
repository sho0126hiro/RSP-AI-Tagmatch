package teams;

import common.Team;
import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

import java.util.LinkedList;
import java.util.List;

import agents.KSTeam.KSPredictor;
import agents.KSTeam.Kimoto;
import agents.KSTeam.Sakagami;

public class KimotoSumizome implements Team {

  Kimoto kimoto;
  Sakagami sakagami;

  public KimotoSumizome() {
    kimoto = new Kimoto();
    sakagami = new Sakagami(kimoto);
  }

  @Override
  public void init() {
    kimoto.init();
    sakagami.init();
  }

  @Override
  public void before() {
    // TODO Auto-generated method stub

  }

  @Override
  public void after(Result r) {

    kimoto.after(r);
    sakagami.after(r);
  }

  @Override
  public TagTeamAction getAction() {
    return new TagTeamAction(kimoto.getAction(), sakagami.getAction());
  }

}
