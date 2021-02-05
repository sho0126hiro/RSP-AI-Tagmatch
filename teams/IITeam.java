package teams;

import agents.IwasakiAgent;
import agents.IkeAgent;
import common.Team;
import common.RSPEnum;
import common.Result;
import common.TagTeamAction;

/**
 * チームのサンプル
 * 内部通信はせず，結果も使わない
 */
public class IITeam implements Team {
    private IwasakiAgent agentA;
    private IkeAgent agentB;

    public void init(){
        agentA = new IwasakiAgent();
        agentB = new IkeAgent();
    }

    public void before(){
        ;
    };

    public void after(Result r){
        agentA.update(r);
        agentB.update(r);
    };

    public TagTeamAction getAction(){
        return new TagTeamAction(agentA.getAction(), agentB.getAction());
    }; 
}
