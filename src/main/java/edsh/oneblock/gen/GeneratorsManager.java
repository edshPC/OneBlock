package edsh.oneblock.gen;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import edsh.oneblock.OneBlockPlugin;
import edsh.oneblock.util.Scheduler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GeneratorsManager {
    final static HashMap<Vector3, BlockGenerator> loadedGenerators = new HashMap<>();
    private final static HashMap<Item, Double> defaultBlockWeights = new HashMap<>();

    public static void init() {

        Config config = new Config(new File(OneBlockPlugin.INSTANCE.getDataFolder(),
                "block-weights.json"), Config.JSON);
        for(String key : config.getKeys()) {
            double w = config.getDouble(key, 0);
            defaultBlockWeights.put(Item.fromString(key), w);
        }

        Scheduler.repeat(() -> {
            for(BlockGenerator gen : loadedGenerators.values()) {
                gen.update();
            }
        }, 20);
    }

    public static BlockGenerator loadGenerator(Position pos, int lvl) {
        BlockGenerator gen = new BlockGenerator(pos);
        for(Map.Entry<Item, Double> e : defaultBlockWeights.entrySet()) {
            gen.blockWeights.addEntry(e.getKey(), e.getValue());
        }
        Vector3 vec = pos.floor();

        loadedGenerators.put(vec, gen);

        pos.getLevel().setBlock(pos.add(0, -1), Block.get(BlockID.BEDROCK));
        return gen;
    }

    public static void destroyBlockHandle(BlockBreakEvent event) {
        Vector3 pos = event.getBlock().floor();

        if(loadedGenerators.containsKey(pos)) {
            BlockGenerator gen = loadedGenerators.get(pos);
            gen.onDestroy(event.getPlayer());
            event.getPlayer().giveItem(event.getDrops());
            event.setDrops(Item.EMPTY_ARRAY);
            //event.setCancelled();
        }
    }



}
