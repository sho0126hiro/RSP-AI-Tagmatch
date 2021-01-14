package common;

/**
 * 次の行動を定義するクラス
 */
public class TagTeamAction {
    public RSPEnum actionA;
    public RSPEnum actionB;

    public TagTeamAction(RSPEnum actionA, RSPEnum actionB){
        this.actionA = actionA;
        this.actionB = actionB;
    }

    @Override
    public String toString(){
        return "A: " + this.actionA.name() + ", B: " + this.actionB.name();
    }
}
