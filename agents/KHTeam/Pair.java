package agents.KHTeam;

/**
 * A型変数とB型のペアを保持するクラス
 */
public class Pair<A, B> {
    public A a;
    public B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getOne() {
        return this.a;
    }

    public B getTwo() {
        return this.b;
    }
}