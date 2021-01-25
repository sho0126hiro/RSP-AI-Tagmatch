package agents.KHTeam;

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
            case SCISORS:
                if (b.equals(RSPEnum.ROCK))
                    return -1;
                if (b.equals(RSPEnum.PAPER))
                    return 1;
            case PAPER:
                if (b.equals(RSPEnum.SCISORS))
                    return -1;
                if (b.equals(RSPEnum.ROCK))
                    return 1;
        }
        return 0;
    }

    /**
     * index to RSPEnum
     */
    public RSPEnum idxToRSPEnum(int i){
        for(RSPEnum r: RSPEnum.values()){
            if(r.getIndex() == i) return r;
        }
        return null;
    }

    /**
     * 0,1,2のみを完全ランダムで出力する
     */
    public int rand0to2(){
        int[] arr = {0,1,2};
        int i = new Random().nextInt(3);
        return arr[i];
    }

    /**
     * A型変数とB型のペアを保持するクラス
     */
    public class Pair<A,B>{
        public A a;
        public B b;
        public Pair(A a, B b){
            this.a = a;
            this.b = b;
        }

        public A getOne(){
            return this.a;
        }
        
        public B getTwo(){
            return this.b;
        }
    }
}
