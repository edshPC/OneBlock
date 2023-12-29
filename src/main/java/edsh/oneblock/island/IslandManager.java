package edsh.oneblock.island;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.yescallop.essentialsnk.EssentialsAPI;
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
        Island island = Util.db.getIsland(id);
        if(island == null) return false;
        island.load();
        islands.put(id, island);
        return true;
    }



    public static Position createNewIsland() {
        Position pos = new Position(0,0,0, level);
        if(islands.containsKey(lastId)) {
            Position previous = islands.get(lastId).getPosition();
            pos = previous.add(2000);
            if(pos.x > 100000) {
                pos.x = 0;
                pos.y += 2000;
            }
        }

        Island island = new Island(pos);
        island.load();
        islands.put(++lastId, island);
        Util.db.saveIsland(island, lastId);

        return pos;
    }

    public static boolean tryLoadPlayerIsland(Player pl) {
        PlayerData data = Util.db.getPlayerData(pl.getUniqueId());
        if(data == null) return false;

        return tryLoadIsland(data.island_id);
    }

    public static void createPlayerIsland(Player pl) {
        Position pos = createNewIsland();
        Position home = pos.add(0.5, 1, 0.5);

        var essentials = EssentialsAPI.getInstance();
        essentials.setHome(pl, "is", Location.fromObject(home, home.level));

        Area area = new Area(new Point(pos.add(-999, 0, -999)), new Point(pos.add(999, 0, 999)));

        try {
            new PlayerRegionManager(pl, DGuard.getInstance()).createRegion(pl.getName() + "'s island", area, pos.level);
            pl.sendMessage("§eРегион был успешно создан!");
        } catch (RegionException ex) {
            pl.sendMessage("§c"+ex.getMessage()+". Не удалось создать регион острова. Пожалуйста, обратитесь к админу");
        }

        pl.teleport(home);

        PlayerData data = new PlayerData();
        data.uuid = pl.getUniqueId();
        data.name = pl.getName();
        data.island_id = lastId;
        data.is_owner = true;
        Util.db.savePlayerData(data);

    }

}
