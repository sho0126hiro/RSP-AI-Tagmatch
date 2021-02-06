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
    private Agent agentWithProfitSharing;
    private Agent agentWithProfitSharing2;
    private Agent agentWithQ_learning;
    private Agent agentA;
    private Agent agentB;

    private List<Agent> agents;

    // 一時的に保持するエージェントと、次の行動
    private List<Pair<Agent, RSPEnum>> agentAndNextAction;

    public void init(){
        this.rockAgent = new OnlyAgent(RSPEnum.ROCK);
        this.scisorsAgent = new OnlyAgent(RSPEnum.SCISORS);
        this.paperAgent = new OnlyAgent(RSPEnum.PAPER);
        this.agentWithProfitSharing = new AgentWithProfitSharing();
        this.agentWithQ_learning = new AgentWithQ_learning();
        // this.agentWithProfitSharing2 = new AgentWithProfitSharing2();

        this.agents = new ArrayList<Agent>();
        agents.add(this.rockAgent);
        agents.add(this.scisorsAgent);
        agents.add(this.paperAgent);
        agents.add(this.agentWithProfitSharing);
        // agents.add(this.agentWithProfitSharing2);
        agents.add(this.agentWithQ_learning);

        this.agentAndNextAction = new ArrayList<Pair<Agent, RSPEnum>>();
    }

    public void before(){
        System.out.println("before--------------------------------------------------");
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
        this.agentA = this.agentWithQ_learning;
        //this.agentA = agents.get(0);
        this.agentB = agents.get(1);
    }

    public TagTeamAction getAction(){
        System.out.println("getAction--------------------------------------------------");
        // 行動の集合を与える
        RSPEnum actionA = RSPEnum.ROCK;
        RSPEnum actionB = RSPEnum.ROCK;
        for(Agent agent: this.agents){
            if(agent.equals(this.agentA)){
                RSPEnum tmp = agent.getNextAction();
                actionA = tmp;
                System.out.printf("[getAction Agent A] %s\n", tmp.name());
                this.agentAndNextAction.add(new Pair<Agent, RSPEnum>(agent, tmp));
            } else if(agent.equals(this.agentB)) {
                RSPEnum tmp = agent.getNextAction();
                actionB = tmp;
                System.out.printf("[getAction Agent B] %s\n", tmp.name());
                this.agentAndNextAction.add(new Pair<Agent, RSPEnum>(agent, tmp));
            } else {
                this.agentAndNextAction.add(new Pair<Agent, RSPEnum> (agent, agent.getNextAction()));
            }
        }
        return new TagTeamAction(actionA, actionB);
    }

    public void after(Result r){
        System.out.println("after--------------------------------------------------");
        for(Pair<Agent, RSPEnum> p: this.agentAndNextAction){
            // resultが各エージェントで違うので注意
            AgentResult ar = new AgentResult(r, p.getTwo());
            p.getOne().after(ar);
            p.getOne().addIsWinHistory(ar.isWin);
        }
        System.out.printf("[after] ");
        System.out.println(new AgentResult(r, r.AllyTeamAction.actionA));
    }
}