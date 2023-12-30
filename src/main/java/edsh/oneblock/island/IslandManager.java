package edsh.oneblock.island;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import edsh.oneblock.data.PlayerData;
import edsh.oneblock.util.Util;
import ru.dragonestia.dguard.DGuard;
import ru.dragonestia.dguard.exceptions.RegionException;
import ru.dragonestia.dguard.region.PlayerRegionManager;
import ru.dragonestia.dguard.util.Area;
import ru.dragonestia.dguard.util.Point;

import java.util.HashMap;

public class IslandManager {

    public static Level level;
    private static final HashMap<Long, Island> islands = new HashMap<>();
    private static long lastId;

    public static void init() {
        level = Util.server.getLevelByName(Util.config.getString("islands-world-name"));
        lastId = Util.db.getLastIslandId();
        tryLoadIsland(lastId);
    }

    public static boolean tryLoadIsland(long id) {
        if(islands.containsKey(id)) return true;
        Island island = Util.db.getIsland(id);
        if(island == null) return false;
        island.load();
        islands.put(id, island);
        return true;
    }

    public static Island createNewIsland() {
        Position pos = new Position(0,0,0, level);
        if(islands.containsKey(lastId)) {
            Position previous = islands.get(lastId).getPosition();
            pos = previous.add(2000);
            if(pos.x > 100000) {
                pos.x = 0;
                pos.y += 2000;
            }
        }

        Island island = new Island(pos, ++lastId);
        island.load();
        islands.put(lastId, island);
        Util.db.saveIsland(island);

        return island;
    }

    public static boolean tryLoadPlayerIsland(Player pl) {
        PlayerData data = Util.db.getPlayerData(pl.getUniqueId());
        if(data == null) return false;

        if(tryLoadIsland(data.island_id)) {
            islands.get(data.island_id).online(pl);
            return true;
        }
        return false;
    }

    public static void createPlayerIsland(Player pl) {
        Island island = createNewIsland();
        island.addPlayer(pl.getUniqueId());
        island.online(pl);
        Position pos = island.getPosition();

        Area area = new Area(new Point(pos.add(-999, 0, -999)),
                            new Point(pos.add(999, 0, 999)));
        try {
            new PlayerRegionManager(pl, DGuard.getInstance()).createRegion(pl.getName() + "'s island", area, pos.level);
            pl.sendMessage("§eРегион был успешно создан!");
        } catch (RegionException ex) {
            pl.sendMessage("§c"+ex.getMessage()+". Не удалось создать регион острова. Пожалуйста, обратитесь к админу");
        }

        Util.savePlayer(pl, island.getId(), true);

    }

    public static boolean tryUnloadPlayerIsland(Player pl) {
        PlayerData data = Util.db.getPlayerData(pl.getUniqueId());
        if(data == null || !islands.containsKey(data.island_id)) return false;
        Island island = islands.get(data.island_id);
        if(island.offline(pl)) {
            islands.remove(data.island_id);
            island.unload();
            Util.db.saveIsland(island);
            return true;
        }
        return false;
    }

}
