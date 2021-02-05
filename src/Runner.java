
import java.util.Arrays;
/**
 * 実行クラス
 */
public class Runner {
    public static void main(String[] args){
        boolean ignoresAiko = Arrays.asList(args).contains("--ignores-aiko");
        BattleManager bm = new BattleManager(ignoresAiko);
        bm.run();
        
    }
}
