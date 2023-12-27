package edsh.oneblock.gen;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import edsh.oneblock.util.Scheduler;
import edsh.oneblock.util.WeightedRandomBag;

public class BlockGenerator {
    final WeightedRandomBag<Item> blockWeights = new WeightedRandomBag<>();

    private final Position position;
    private final Level level;

    private Block currentBlock;

    public BlockGenerator(Position position) {
        this.position = position;
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
    }

    public void update() {
        Block inWorld = position.getLevelBlock();
        if(!Block.equals(currentBlock, inWorld)) {
            setBlock();
        }
    }


    public Position getPosition() {
        return position;
    }
}
