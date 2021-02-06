package teams;

import agents.THTeam.HiranoAgent;
import agents.THTeam.TanimuraAgent;
import common.RSPEnum;
import common.Result;
import common.TagTeamAction;
import common.Team;

/**
 * チームのサンプル
 * 内部通信はせず，結果も使わない
 */
public class THTeam implements Team {
    private TanimuraAgent agentA;
    private HiranoAgent agentB;
    private int winCount = 0;

    public void init(){
        agentA = new TanimuraAgent(RSPEnum.SCISORS);
        agentB = new HiranoAgent();
    }

    public void before(){
    	agentA.before();
    };
    public void after(Result r){
        System.out.println(r);
    	agentA.after(r);
    	agentB.addResult(r);
    	this.winCount = this.winCount + r.PointOfAllyAgentA + r.PointOfAllyAgentB;

    };

    public TagTeamAction getAction(){
        return new TagTeamAction(agentA.getAction(), agentB.getAction());
    };

    public int getWin(){
    	return winCount;
    }
}
