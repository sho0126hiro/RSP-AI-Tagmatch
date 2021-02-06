package agents.KHTeam;

import common.RSPEnum;

public class OnlyAgent extends Agent {
    RSPEnum action;

    public OnlyAgent(RSPEnum a){
        this.action = a;
    }

    @Override
    public RSPEnum getNextAction() {
        return this.action;
    }

    @Override
    public void before() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void after(AgentResult r) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String action(){
        return this.action.name();
    }

}
