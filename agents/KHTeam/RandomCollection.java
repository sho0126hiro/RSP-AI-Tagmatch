package agents.KHTeam;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * ランダムに選択する用のクラス
 */
public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;
    
    public RandomCollection() {
        this.random = new Random();
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E get() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}