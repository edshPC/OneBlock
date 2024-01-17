package edsh.oneblock.gen;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ConfigSection;
import edsh.oneblock.island.Island;
import edsh.oneblock.util.Scheduler;
import edsh.oneblock.util.Util;
import edsh.oneblock.util.WeightedRandomBag;

import java.util.HashMap;
import java.util.Map;

public class GeneratorsManager {
    final static HashMap<Vector3, BlockGenerator> loadedGenerators = new HashMap<>();
    private final static HashMap<Integer, HashMap<Item, Double>> blockWeights = new HashMap<>();
    private final static HashMap<Integer, HashMap<Map<Integer, Item>, Double>> chestDataWeights = new HashMap<>();
    private final static HashMap<Integer, HashMap<String, Double>> mobWeights = new HashMap<>();

    public static void init() {

        var bw = Util.blockWeights.getSection("block");
        for (String lvlStr : bw.getKeys(false)) {
            int lvl = Integer.parseInt(lvlStr);
            HashMap<Item, Double> curWeights = new HashMap<>();
            for (var item : bw.getSection(lvlStr).entrySet()) {
                curWeights.put(Item.fromString(item.getKey()), (Double) item.getValue());
            }
            blockWeights.put(lvl, curWeights);
        }

        var chestw = Util.blockWeights.getSection("chest");
        for (String lvlStr : chestw.getKeys(false)) {
            int lvl = Integer.parseInt(lvlStr);
            double chestWeight = 0;
            HashMap<Map<Integer, Item>, Double> curWeights = new HashMap<>();
            for (var chestEntry : chestw.getMapList(lvlStr)) {
                Map<Integer, Item> itemMap = new HashMap<>();
                int slot = 0;
                for (var itemEntry : new ConfigSection(chestEntry).getSection("items").entrySet()) {
                    Item item = Item.fromString(itemEntry.getKey());
                    item.setCount(((Double) itemEntry.getValue()).intValue());
                    itemMap.put(slot++, item);
                }
                double weight = (double) chestEntry.get("weight");
                curWeights.put(itemMap, weight);
                chestWeight += weight;
            }
            chestDataWeights.put(lvl, curWeights);
            blockWeights.computeIfAbsent(lvl, HashMap::new)
                    .put(Item.get(Item.CHEST), chestWeight);
        }

        var mobw = Util.blockWeights.getSection("mob");
        for (String lvlStr : mobw.getKeys(false)) {
            int lvl = Integer.parseInt(lvlStr);
            HashMap<String, Double> curWeights = new HashMap<>();
            for (var item : mobw.getSection(lvlStr).entrySet()) {
                curWeights.put(item.getKey(), (Double) item.getValue());
            }
            mobWeights.put(lvl, curWeights);
        }


        Scheduler.repeat(() -> {
            for (BlockGenerator gen : loadedGenerators.values()) {
                gen.update();
            }
        }, 20);
    }

    public static BlockGenerator loadGenerator(Island island) {
        BlockGenerator gen = new BlockGenerator(island);
        updateBlockWeights(gen, island.getLvl());
        Position pos = island.getPosition();
        Vector3 vec = pos.floor();

        loadedGenerators.put(vec, gen);

        pos.getLevel().setBlock(pos.add(0, -1), Block.get(BlockID.BEDROCK));
        return gen;
    }

    public static boolean unloadGenerator(Position pos) {
        return loadedGenerators.remove(pos) != null;
    }

    public static void updateBlockWeights(BlockGenerator gen, int lvl) {
        gen.blockWeights = new WeightedRandomBag<>();
        gen.chestDataWeights = new WeightedRandomBag<>();
        gen.mobWeights = new WeightedRandomBag<>();
        for (int i = 1; i <= lvl; i++) {
            if (blockWeights.containsKey(i))
                for (var e : blockWeights.get(i).entrySet())
                    gen.blockWeights.addEntry(e.getKey(), e.getValue());
            if (chestDataWeights.containsKey(i))
                for (var e : chestDataWeights.get(i).entrySet())
                    gen.chestDataWeights.addEntry(e.getKey(), e.getValue());
            if (mobWeights.containsKey(i))
                for (var e : mobWeights.get(i).entrySet())
                    gen.mobWeights.addEntry(e.getKey(), e.getValue());
        }
    }

    public static void destroyBlockHandle(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Vector3 pos = event.getBlock().floor();

        BlockGenerator gen = loadedGenerators.get(pos);
        if (gen == null) return;

        gen.onDestroy(event.getPlayer());
        event.getPlayer().giveItem(event.getDrops());
        event.setDrops(Item.EMPTY_ARRAY);
    }


}
