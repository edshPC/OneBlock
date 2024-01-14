package edsh.oneblock.gen;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import edsh.oneblock.island.Island;
import edsh.oneblock.util.Scheduler;
import edsh.oneblock.util.WeightedRandomBag;

public class BlockGenerator {
    WeightedRandomBag<Item> blockWeights;

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
        Item randItem = blockWeights.getRandom();
        currentBlock = randItem.getBlock();
        level.setBlock(position, currentBlock, true, false);
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
