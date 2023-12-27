package edsh.oneblock.island;

import cn.nukkit.Player;
import cn.nukkit.camera.data.Pos;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.Generator;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.gen.GeneratorsManager;

import java.util.HashMap;

public class IslandManager {

    public static Level level;
    private static final HashMap<Long, Island> islands = new HashMap<>();
    private static long lastId;

    public static Position createIsland() {
        Position pos = new Position(0,0,0, level);
        if(islands.containsKey(lastId)) {
            Position previous = islands.get(lastId).getPosition();
            pos = previous.add(2000);
            if(pos.x > 100000) {
                pos.x = 0;
                pos.y += 2000;
            }
        }

        BlockGenerator gen = GeneratorsManager.loadGenerator(pos);
        Island island = new Island(gen);
        islands.put(++lastId, island);

        return pos;
    }

    public static void createPlayerIsland(Player pl) {

        Position pos = createIsland();

        pl.teleport(pos.add(0.5, 1, 0.5));
        pl.sendMessage("Остров создан на " + pos);

    }

}
