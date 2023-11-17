package edsh.oneblock.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WeightedRandomBag<T> {

    private class Entry {
        double accumulatedWeight;
        T object;
    }

    private final List<Entry> entries = new LinkedList<>();
    private double accumulatedWeight;
    private final Random rand = new Random();

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry e = new Entry();
        e.object = object;
        e.accumulatedWeight = accumulatedWeight;
        entries.add(e);
    }

    public T getRandom() {
        double r = rand.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null; //should only happen when there are no entries
    }
}