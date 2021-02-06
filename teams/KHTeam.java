package teams;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import agents.KHTeam.*;
import common.RSPEnum;
import common.Result;
import common.TagTeamAction;
import common.Team;

public class KHTeam implements Team {
    private Agent rockAgent;
    private Agent scisorsAgent;
    private Agent paperAgent;
    private Agent agentWithProfitSharing3;
    private Agent agentWithProfitSharing10;
    private Agent agentWithProfitSharing15;
    private Agent agentWithQ_learning03;
    private Agent agentWithQ_learning05;
    private Agent agentWithQ_learning1;
    private Agent agentA;
    private Agent agentB;

    private List<Agent> agents;

    // 一時的に保持するエージェントと、次の行動
    private List<Pair<Agent, RSPEnum>> agentAndNextAction;

    public void init(){
        this.rockAgent = new OnlyAgent(RSPEnum.ROCK);
        this.scisorsAgent = new OnlyAgent(RSPEnum.SCISORS);
        this.paperAgent = new OnlyAgent(RSPEnum.PAPER);
        this.agentWithProfitSharing3 = new AgentWithProfitSharing(3);
        this.agentWithProfitSharing10 = new AgentWithProfitSharing(10);
        this.agentWithProfitSharing15 = new AgentWithProfitSharing(15);
        this.agentWithQ_learning03 = new AgentWithQ_learning(0.3);
        this.agentWithQ_learning05 = new AgentWithQ_learning(0.5);
        this.agentWithQ_learning1 = new AgentWithQ_learning(1.0);

        this.agents = new ArrayList<Agent>();
        agents.add(this.rockAgent);
        agents.add(this.scisorsAgent);
        agents.add(this.paperAgent);
        agents.add(this.agentWithProfitSharing3);
        agents.add(this.agentWithProfitSharing10);
        agents.add(this.agentWithProfitSharing15);
        agents.add(this.agentWithQ_learning03);
        agents.add(this.agentWithQ_learning05);
        agents.add(this.agentWithQ_learning1);

        this.agentAndNextAction = new ArrayList<Pair<Agent, RSPEnum>>();
    }

    public void before(){
        // System.out.println("before--------------------------------------------------");
        for(Agent a: this.agents){
            a.before();
        }
        this.agentAndNextAction = new ArrayList<Pair<Agent, RSPEnum>>();
        this.setNextAgent();
    }

    public void setNextAgent(){
        Collections.sort(agents, new Comparator<Agent>(){
            @Override
            public int compare(Agent a, Agent b) {
                return b.getNumOfWins() - a.getNumOfWins();
            }
        });
        System.out.printf("[setNextAgent]");
        System.out.println(agents);
        // this.agentA = this.agentWithQ_learning;
        this.agentA = agents.get(0);
        this.agentB = agents.get(1);
    }

    public TagTeamAction getAction(){
        // System.out.println("getAction--------------------------------------------------");
        // 行動の集合を与える
        RSPEnum actionA = RSPEnum.ROCK;
        RSPEnum actionB = RSPEnum.ROCK;
        for(Agent agent: this.agents){
            if(agent.equals(this.agentA)){
                RSPEnum tmp = agent.getNextAction();
                actionA = tmp;
                // System.out.printf("[getAction Agent A] %s\n", tmp.name());
                this.agentAndNextAction.add(new Pair<Agent, RSPEnum>(agent, tmp));
            } else if(agent.equals(this.agentB)) {
                RSPEnum tmp = agent.getNextAction();
                actionB = tmp;
                // System.out.printf("[getAction Agent B] %s\n", tmp.name());
                this.agentAndNextAction.add(new Pair<Agent, RSPEnum>(agent, tmp));
            } else {
                this.agentAndNextAction.add(new Pair<Agent, RSPEnum> (agent, agent.getNextAction()));
            }
        }
        return new TagTeamAction(actionA, actionB);
    }

    public void after(Result r){
        // System.out.println("after--------------------------------------------------");
        for(Pair<Agent, RSPEnum> p: this.agentAndNextAction){
            // resultが各エージェントで違うので注意
            AgentResult ar = new AgentResult(r, p.getTwo());
            p.getOne().after(ar);
            p.getOne().addIsWinHistory(ar.isWin);
        }
        // System.out.printf("[after] ");
        // System.out.println(new AgentResult(r, r.AllyTeamAction.actionA));
    }
}