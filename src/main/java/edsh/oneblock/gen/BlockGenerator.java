package edsh.oneblock.gen;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import edsh.oneblock.island.Island;
import edsh.oneblock.util.Scheduler;
import edsh.oneblock.util.WeightedRandomBag;

import java.util.Map;

public class BlockGenerator {
    WeightedRandomBag<Item> blockWeights;
    WeightedRandomBag<Map<Integer, Item>> chestDataWeights;
    WeightedRandomBag<String> mobWeights;

    private final Island island;
    private final Position position;
    private final Level level;

    private Block currentBlock;

    public BlockGenerator(Island island) {
        this.island = island;
        this.position = island.getPosition();
        this.level = position.getLevel();
    }

    public Block setBlock() {
        currentBlock = blockWeights.get().getBlock();
        level.setBlock(position, currentBlock, true, false);
        if(currentBlock instanceof BlockChest blockChest) {
            blockChest.position(position);
            blockChest.getOrCreateBlockEntity().getRealInventory()
                    .setContents(chestDataWeights.get());
        }

        String mob = mobWeights.get();
        if(!mob.isEmpty()) Entity.createEntity(mob, position.add(0, 1)).spawnToAll();

        return currentBlock;
    }

    public void onDestroy(Player pl) {
        Scheduler.delay(() -> {
            pl.breakingBlock = setBlock();
            pl.breakingBlockFace = BlockFace.UP;
        }, 0);

        if(island.isOnline(pl)) island.increaseXp();
    }

    public void update() {
        if(!position.getChunk().isLoaded()) return;
        Block inWorld = position.getLevelBlock();
        if(!Block.equals(currentBlock, inWorld) && !inWorld.isSolid()) {
            setBlock();
        }
    }


    public Position getPosition() {
        return position;
    }
}
