package agents.KHTeam;

import common.RSPEnum;
import common.Result;

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
    public void after(Result r) {
        // TODO Auto-generated method stub
        
    }

}
