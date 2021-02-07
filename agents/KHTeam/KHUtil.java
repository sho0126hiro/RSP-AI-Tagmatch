package agents.KHTeam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import common.RSPEnum;

/**
 * 共通の便利系メソッド軍
 */
public class KHUtil {
      /**
     * a vs b　のじゃんけんの結果を返す
     * @param a
     * @param b
     * @return  {1, 0, -1} (aの勝ち、あいこ、bの勝ち) 
     */
    public int RSP1v1(RSPEnum a, RSPEnum b){
        switch (a) {
            case ROCK:
                if (b.equals(RSPEnum.PAPER))
                    return -1;
                if (b.equals(RSPEnum.SCISORS))
                    return 1;
                break;
            case SCISORS:
                if (b.equals(RSPEnum.ROCK))
                    return -1;
                if (b.equals(RSPEnum.PAPER))
                    return 1;
                break;
            case PAPER:
                if (b.equals(RSPEnum.SCISORS))
                    return -1;
                if (b.equals(RSPEnum.ROCK))
                    return 1;
                break;
        }
        return 0;
    }

    /**
     * a vs b,c　の3人のじゃんけんの結果を返す
     * @param a
     * @param b
     * @param c
     * @return  {0.0, -0.5, -1.0, -2.0, -3.0}: 一人勝ち、どっちかに勝つ、あいこ、どっちかに負ける、二人に負ける
     */
    public double RSP1v3(RSPEnum a, RSPEnum b, RSPEnum c){
        if(a.getIndex() != b.getIndex() && a.getIndex() != c.getIndex() && b.getIndex() != c.getIndex()) return -1.0; // あいこ
        if(a.getIndex() == b.getIndex() && a.getIndex() == c.getIndex()) return -1.0; // あいこ
        int AvsB = this.RSP1v1(a, b);
        int AvsC = this.RSP1v1(a, c);
        // System.out.printf("[RSP1vs3] AvsB: %d, AvsC: %d\n", AvsB, AvsC);
        if(AvsB == 1 && AvsC == 1) return 0.0; //一人勝ち 
        if(AvsB == -1 && AvsC == -1) return -3.0;  // １人負け
        if(AvsB == 1 || AvsC == 1) return -0.5; //　どっちかに勝った（一度でも勝利していたらOK）
        if(AvsB == -1 || AvsC == -1) return -2.0; // どっちかに負けた （あいこ+負けの組み合わせのみ）
        return -500.0; // unreachable code
    }

    /**
     * indexを元に、RSPEnumを返す（決定的）
     * index -> RSPEnum の変換
     */
    public RSPEnum choiceRSPEnumByIndex(int i){
        for(RSPEnum r: RSPEnum.values()){
            if(r.getIndex() == i) return r;
        }
        return null;
    }

    /**
     * 確率的にRSPEnumを返す
     * poblist: 0 ~ 1の範囲
     * @param problist 確率 problist.length = RSPEnum.length
     * @return
     */
    public RSPEnum choiceRSPEnumByProb(double[] problist){
        RSPEnum[] r = RSPEnum.values();
        // System.out.printf("[choiceRSPEnumByProb-problist] ");
        for(int i=0;i<problist.length;i++){
            // System.out.printf("%f ", problist[i]);
        }
        // System.out.println();
        RandomCollection<RSPEnum> rc = new RandomCollection<RSPEnum>();
        for(int i=0; i < problist.length; i++){
            rc.add(problist[i], r[i]);
        }
        RSPEnum ret = rc.get();
        if(ret == null) {
            // System.out.printf("[choiceRSPEnumByProb-problist == null]");
            for(int i=0;i<problist.length;i++){
                // System.out.printf("%d ", problist);
            }
            // System.exit(0);
        }
        return ret;
    }

    /**
     * 0,1,2のみを完全ランダムで出力する
     */
    public int rand0to2(){
        int[] arr = {0,1,2};
        int i = new Random().nextInt(3);
        return arr[i];
    }

}
