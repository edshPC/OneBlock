package edsh.oneblock.util;

import java.util.*;
import java.util.function.Supplier;

public class WeightedRandomBag<T> implements Supplier<T> {

    private final NavigableMap<Double, T> map = new TreeMap<>();
    private final Random random = new Random();
    private double total = 0;

    public void addEntry(T object, double weight) {
        if (weight <= 0) return;
        total += weight;
        map.put(total, object);
    }

    @Override
    public T get() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }
}