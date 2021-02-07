package agents.THTeam;

import java.util.Random;

import common.RSPEnum;
import common.Result;

public class TanimuraAgent extends agents.SampleAgent{
    private RSPEnum ret;
    private double enemyR = 1;
    private double enemyS = 1;
    private double enemyP = 1;



    public TanimuraAgent(RSPEnum ret) {
		super(ret);
    	this.ret = this.getRandom(enemyR, enemyS, enemyP);
	}

    public RSPEnum getAction(){
        return ret;
    }

    public void after(Result r) {
    	switch(r.EnemyTeamAction.actionA){
    	case ROCK:
    		this.enemyR += 1;
    		break;
    	case SCISORS:
    		this.enemyS += 1;
    		break;
    	case PAPER:
    		this.enemyP += 1;
    		break;
    	}
    	switch(r.EnemyTeamAction.actionB){
    	case ROCK:
    		this.enemyR += 1;
    		break;
    	case SCISORS:
    		this.enemyS += 1;
    		break;
    	case PAPER:
    		this.enemyP += 1;
    		break;
    	}
    	this.enemyR *= 0.9;
    	this.enemyS *= 0.9;
    	this.enemyP *= 0.9;
    	this.ret = this.getRandom(enemyR, enemyS, enemyP);
    }

    public RSPEnum before() {
    	return this.ret;
    }

    private RSPEnum getRandom(double weightR,double weightS,double weightP)
    {
        double totalWeight = weightR+weightS+weightP;
        Random rand = new Random();
        double randDouble = rand.nextDouble() * totalWeight;
        if (weightR + weightS <= randDouble)
        {
            return RSPEnum.SCISORS;
        }
        if (weightR <= randDouble)
        {
            return RSPEnum.ROCK;
        }
        return RSPEnum.PAPER;
    }
}
