package edsh.oneblock.gen;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import edsh.oneblock.OneBlockPlugin;
import edsh.oneblock.island.Island;
import edsh.oneblock.util.Scheduler;
import edsh.oneblock.util.WeightedRandomBag;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GeneratorsManager {
    final static HashMap<Vector3, BlockGenerator> loadedGenerators = new HashMap<>();
    private final static HashMap<Integer, HashMap<Item, Double>> blockWeights = new HashMap<>();

    public static void init() {

        Config config = new Config(new File(OneBlockPlugin.INSTANCE.getDataFolder(),
                "block-weights.json"), Config.JSON);
        for(String key : config.getKeys()) {
            int lvl = Integer.parseInt(key);
            HashMap<Item, Double> curWeights = new HashMap<>();
            var section = config.getSection(key);
            for(String item : section.getKeys()) {
                double w = section.getDouble(item, 0);
                curWeights.put(Item.fromString(item), w);

            }
            blockWeights.put(lvl, curWeights);
        }

        Scheduler.repeat(() -> {
            for(BlockGenerator gen : loadedGenerators.values()) {
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
        for(int i=1; i<=lvl; i++) {
            if(!blockWeights.containsKey(i)) continue;
            for(var e : blockWeights.get(i).entrySet()) {
                gen.blockWeights.addEntry(e.getKey(), e.getValue());
            }
        }
    }

    public static void destroyBlockHandle(BlockBreakEvent event) {
        if(event.isCancelled()) return;
        Vector3 pos = event.getBlock().floor();

        BlockGenerator gen = loadedGenerators.get(pos);
        if(gen == null) return;

        gen.onDestroy(event.getPlayer());
        event.getPlayer().giveItem(event.getDrops());
        event.setDrops(Item.EMPTY_ARRAY);
    }



}
