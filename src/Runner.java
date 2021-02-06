
import java.util.Arrays;
/**
 * 実行クラス
 */
public class Runner {
    public static void main(String[] args){
        boolean ignoresAiko = Arrays.asList(args).contains("--ignores-aiko");
        boolean ignoresLogs = Arrays.asList(args).contains("--ignores-logs");
        boolean result = Arrays.asList(args).contains("--result");
        BattleManager bm = new BattleManager(ignoresAiko, ignoresLogs, result);
        bm.run();
        
    }
}
