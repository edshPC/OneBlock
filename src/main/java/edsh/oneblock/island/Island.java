package edsh.oneblock.island;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.gen.GeneratorsManager;

public class Island {

    public static Island instance;

    private final BlockGenerator generator;
    private final Position position;

    public Island(BlockGenerator generator) {
        this.generator = generator;
        this.position = generator.getPosition();
    }

    public Position getPosition() {
        return position;
    }
}
