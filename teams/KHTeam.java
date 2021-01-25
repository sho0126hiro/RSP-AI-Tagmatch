package teams;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import agents.KHTeam.Agent;
import agents.KHTeam.OnlyAgent;
import agents.KHTeam.SingleAgentWithProfitSharing;
import agents.KHTeam.KHUtil.Pair;
import common.RSPEnum;
import common.Result;
import common.TagTeamAction;
import common.Team;

public class KHTeam implements Team {
    private Agent rockAgent;
    private Agent scisorsAgent;
    private Agent paperAgent;
    private Agent singleAgentWithProfitSharing;
    
    private Agent agentA;
    private Agent agentB;

    private List<Agent> agents;

    // 一時的に保持するエージェントと、次の行動
    private List<Pair<Agent, RSPEnum>> agentAndNextAction;

    public void init(){
        this.rockAgent = new OnlyAgent(RSPEnum.ROCK);
        this.scisorsAgent = new OnlyAgent(RSPEnum.SCISORS);
        this.paperAgent = new OnlyAgent(RSPEnum.PAPER);
        this.singleAgentWithProfitSharing = new SingleAgentWithProfitSharing();

        this.agents = new ArrayList<Agent>();
        agents.add(this.rockAgent);
        agents.add(this.scisorsAgent);
        agents.add(this.paperAgent);
        agents.add(this.singleAgentWithProfitSharing);
    }

    public void before(){
        for(Agent a: this.agents){
            a.before();
        }
        this.setNextAgent();
    }

    public void after(Result r){
        for(Agent a: this.agents){
            // resultが各エージェントで違うので注意
            a.after(r);
        }
    }

    public TagTeamAction getAction(){
        // 行動の集合を与える
        // RSPEnum actionA = RSPEnum.ROCK;
        // RSPEnum actionB = RSPEnum.ROCK;
        // for(Agent agent: this.agents){
        //     if(agent.equals(this.agentA)){
        //         RSPEnum tmp = agent.getNextAction();
        //         this.agentAndNextAction.add(new Pair<Agent, RSPEnum>(agent, tmp));
        //     } else if(agent.equals(this.agentB)) {
        //         RSPEnum tmp = agent.getNextAction();
        //         this.agentAndNextAction.add(new Pair<Agent, RSPEnum>(agent, tmp));
        //     } else {
        //         this.agentAndNextAction.add(new Pair<Agent, RSPEnum> (agent, agent.getNextAction()));
        //     }
        // }
        return new TagTeamAction(RSPEnum.ROCK, RSPEnum.SCISORS);
        // return new TagTeamAction(actionA, actionB);
    }

    public void setNextAgent(){
        Collections.sort(agents, new Comparator<Agent>(){
            @Override
            public int compare(Agent a, Agent b) {
                return a.getNumOfWins() - b.getNumOfWins();
            }
        });
        System.out.println(agents);
        this.agentA = this.singleAgentWithProfitSharing;
        this.agentA.setType("A");
        this.agentB = this.rockAgent;
        this.agentB.setType("B");
    }
}