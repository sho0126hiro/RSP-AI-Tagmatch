package common;

public interface Team {
    /**
     * 初期化関数
     */
    public void init();

    /**
     * 前処理を行う
     */
    public void before();

    /**
     * 後処理
     * @param r 対戦結果
     */
    public void after(Result r);
    
    /**
     * 次の手を取得
     * @return 次の手
     */
    public TagTeamAction getAction();
}
