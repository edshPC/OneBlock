package edsh.oneblock.island;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import edsh.oneblock.data.PlayerData;
import edsh.oneblock.util.Util;

import java.util.HashMap;

public class IslandManager {

    public static Level level;
    // island id - island
    private static final HashMap<Long, Island> islands = new HashMap<>();
    // player - island
    private static final HashMap<Player, Island> islandsByPlayer = new HashMap<>();
    // level - xp for next level
    private static final HashMap<Integer, Long> levelsXp = new HashMap<>();
    // invited player - island
    private static final HashMap<Player, Island> invitations = new HashMap<>();

    private static Position lastPos;
    private static long lastId;

    public static void init() {
        level = Util.server.getLevelByName(Util.config.getString("islands-world-name"));
        var levelsConfig = Util.config.getSection("levels");
        for (String key : levelsConfig.getKeys()) {
            levelsXp.put(Integer.valueOf(key), levelsConfig.getLong(key));
        }

        lastId = Util.db.getLastIslandId();
        lastPos = Util.db.getIslandLastPos();
        lastPos.level = level;
    }

    public static boolean tryLoadIsland(long id) {
        if (islands.containsKey(id)) return true;
        Island island = Util.db.getIsland(id);
        if (island == null) return false;
        island.load();
        islands.put(id, island);
        return true;
    }

    public static Island createNewIsland() {
        Position pos = lastPos.add(2000);
        if (pos.x > 100000) {
            pos.x = 0;
            pos.z += 2000;
        }

        Island island = new Island(pos, ++lastId);
        island.load();
        islands.put(lastId, island);
        lastPos = pos.clone();

        return island;
    }

    public static boolean tryLoadPlayerIsland(Player pl) {
        PlayerData data = Util.db.getPlayerData(pl.getUniqueId());
        if (data == null) return false;

        if (tryLoadIsland(data.island_id)) {
            Island island = islands.get(data.island_id);
            island.online(pl);
            islandsByPlayer.put(pl, island);
            return true;
        }
        return false;
    }

    public static boolean createPlayerIsland(Player pl) {
        if (tryLoadPlayerIsland(pl)) return false;

        Island island = createNewIsland();
        island.setOwner(pl);
        islandsByPlayer.put(pl, island);
        island.online(pl);
        Position pos = island.getPosition();

        var regionManager = SRegionProtectorMain.getInstance().getRegionManager();
        regionManager.createRegion(
                "island #" + island.getId(),
                pl.getName(),
                pos.add(-999, 0, -999).setY(-64),
                pos.add(999, 0, 999).setY(319),
                pos.getLevel()
        );

        Util.savePlayer(pl, island.getId());
        Util.db.saveIsland(island);
        return true;
    }

    public static boolean tryUnloadPlayerIsland(Player pl) {
        if (!islandsByPlayer.containsKey(pl)) return false;
        Island island = islandsByPlayer.remove(pl);

        if (island.offline(pl)) {
            islands.remove(island.getId());
            island.unload();
            Util.db.saveIsland(island);
            return true;
        }
        return false;
    }

    public static boolean tryLeavePlayer(Player pl) {
        if (!islandsByPlayer.containsKey(pl)) return false;
        Island island = islandsByPlayer.remove(pl);
        island.offline(pl);

        var regionManager = SRegionProtectorMain.getInstance().getRegionManager();
        Region region = regionManager.getRegion("island #" + island.getId());
        if (island.removePlayer(pl.getUniqueId())) {
            regionManager.removeRegion(region);
            islands.remove(island.getId());
            island.unload();
        } else if(region.isMember(pl.getName())) regionManager.removeMember(region, pl.getName());

        Util.savePlayer(pl, -1);
        return true;
    }

    public static Island getIsland(Player pl) {
        return islandsByPlayer.get(pl);
    }

    public static long getRequiredXp(int lvl) {
        if (lvl <= 0) lvl = 1;
        while (!levelsXp.containsKey(lvl)) lvl--;
        return levelsXp.get(lvl);
    }

    public static void invitePlayer(Player from, Player to, Island is) {
        if (islandsByPlayer.containsKey(to)) {
            from.sendMessage("§cПриглашенный игрок уже имеет свой остров! Ему нужно покинуть его, прежде чем вы сможете кинуть запрос.");
            return;
        }
        invitations.put(to, is);
        from.sendMessage("§aВы пригласили игрока §b" + to.getName() + " §r§aк себе на остров!");
        to.sendMessage("§aИгрок §b" + from.getName() + " §r§aпригласил вас на свой остров!\nВведи §b/is accept §aчтобы принять приглшение или §b/is deny §aчтобы отклонить.\nВнимание! Вы можете состоять тольно на одном острове");
    }

    public static void manageInvitation(Player pl, boolean accept) {
        Island is = invitations.remove(pl);
        if (is == null) {
            pl.sendMessage("§cУ вас нет приглашений на остров :(");
            return;
        }
        if (!accept) {
            for (Player p : is.getOnline())
                p.sendMessage("§e" + pl.getName() + " §r§cотказался от приглашения на остров");
            pl.sendMessage("§eВы отказались от приглашения");
            return;
        }
        String msg = "§b" + pl.getName() + " §r§aприсоединился к острову, добро пожаловать!";
        is.online(pl);
        islandsByPlayer.put(pl, is);
        Util.savePlayer(pl, is.getId());
        for (Player p : is.getOnline()) p.sendMessage(msg);
        pl.sendMessage("§b/is home §aчтобы попасть на остров");

        var regionManager = SRegionProtectorMain.getInstance().getRegionManager();
        regionManager.addMember(
                regionManager.getRegion("island #" + is.getId()),
                pl.getName()
        );
    }

}
