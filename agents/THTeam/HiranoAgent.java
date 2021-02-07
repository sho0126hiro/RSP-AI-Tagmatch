package agents.THTeam;

import common.RSPEnum;
import common.Result;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;


public class HiranoAgent {
    private List<Integer>[] results;
    public RSPEnum ret;
    final int WIN_REWARD = 1;
    final int DRAW_REWARD = 0;
    final int LOSE_REWARD = -1;
    final double EPSILON=0.1;
    
    public HiranoAgent(){
        this.results=new List[3];
        for (int i = 0; i < 3; i++) {
            this.results[i]=new ArrayList<Integer>();
        }
    }

    public int judgeRSP(RSPEnum myHand, RSPEnum enemyHand) {
        int reward = 0;
        if (myHand == RSPEnum.ROCK) {
            switch (enemyHand) {
                case ROCK:
                    reward = DRAW_REWARD;
                    break;
                case SCISORS:
                    reward = WIN_REWARD;
                    break;
                case PAPER:
                    reward = LOSE_REWARD;
                    break;
            }
        } else if (myHand == RSPEnum.SCISORS) {
            switch (enemyHand) {
                case ROCK:
                    reward = LOSE_REWARD;
                    break;
                case SCISORS:
                    reward = DRAW_REWARD;
                    break;
                case PAPER:
                    reward = WIN_REWARD;
                    break;
            }
        }else {
            switch (enemyHand) {
                case ROCK:
                    reward = WIN_REWARD;
                    break;
                    case SCISORS:
                    reward = LOSE_REWARD;
                    break;
                    case PAPER:
                    reward = DRAW_REWARD;
                    break;
            }
        }
        return reward;
    }

    public void addResult(Result a) {//じゃんけんの結果を保存する
        RSPEnum myAction = a.AllyTeamAction.actionB;
        int reward = 0;
        reward += judgeRSP(myAction, a.EnemyTeamAction.actionA);
        reward += judgeRSP(myAction, a.EnemyTeamAction.actionB);
        this.results[myAction.getIndex()].add(reward);
    }

    private double[] calExpectedValue(){
        double[] expected_value = new double[3];
        double sum=0;
        for (int i = 0; i < 3; i++) {
            sum=0;
            for (Integer reward :
                    this.results[i]) {
                sum += reward.doubleValue();
            }
            expected_value[i] = this.results[i].size() != 0 ? sum / this.results[i].size() : 0;
        }
        return expected_value;
    }

    private int getMaximumIndex(double[] array){
        int index=0;
        double max_value=array[0];
        for (int i = 0; i < array.length; i++) {
            if (max_value < array[i]) {
                index = i;
                max_value = array[i];
            }
        }
        return index;
    }

    private RSPEnum convertRSPEnum(int a) {
        if (a == 0) {
            return RSPEnum.ROCK;
        } else if (a == 1) {
            return RSPEnum.SCISORS;
        } else if (a == 2) {
            return RSPEnum.PAPER;
        }else{
            System.out.println("@convertRSPEnum:範囲外");
            return RSPEnum.ROCK;
        }
    }
    private RSPEnum choiceHand(){
        double[] expected_value=this.calExpectedValue();
        int maxHand = getMaximumIndex(expected_value);
        List maxHands=new ArrayList<RSPEnum>();
        for (int i = 0; i < expected_value.length; i++) {
            if (expected_value[maxHand] == expected_value[i]) {
                maxHands.add(convertRSPEnum(i));
            }
        }
        return choiceRandom(maxHands);
    }

    private RSPEnum choiceHand(double epsilon){
        Random rand = new Random();
        double r = rand.nextDouble();
        if (r < epsilon) {
            return choiceRandom();
        }else{
            return choiceHand();
        }
    }

    private RSPEnum choiceRandom(List<RSPEnum> hands){
        Random rand = new Random();
        double r = rand.nextDouble();
        double sum=0;
        int i;
        for (i = 0; i < hands.size(); i++) {
            sum+=(double)1/hands.size();
            if (sum>=r){
                break;
            }
        }
        return hands.get(i);
    }

    private RSPEnum choiceRandom(){
        List a = new ArrayList<RSPEnum>();
        a.add(RSPEnum.ROCK);
        a.add(RSPEnum.SCISORS);
        a.add(RSPEnum.PAPER);
        return this.choiceRandom(a);
    }

    public RSPEnum getAction(){
        return choiceHand(EPSILON);
    }

    public static void main(String[] args) {
        HiranoAgent a = new HiranoAgent();
        RSPEnum hoge=a.getAction();
        // System.out.println(hoge);
    }
}