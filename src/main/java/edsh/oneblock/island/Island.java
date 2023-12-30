package edsh.oneblock.island;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.yescallop.essentialsnk.EssentialsAPI;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.gen.GeneratorsManager;

import java.util.LinkedList;
import java.util.UUID;

public class Island {

    private BlockGenerator generator;
    private final Position position;
    private long id;
    private int lvl;
    private long xp;

    private final LinkedList<UUID> allPlayers;
    private final LinkedList<Player> online = new LinkedList<>();

    public Island(Position pos, long id, int lvl, long xp, LinkedList<UUID> allPlayers) {
        this.position = pos.floor();
        this.id = id;
        if (pos.level == null) this.position.level = IslandManager.level;
        this.lvl = lvl;
        this.xp = xp;
        this.allPlayers = allPlayers;
    }

    public Island(Position pos, long id) {
        this(pos, id, 1, 0, new LinkedList<>());
    }

    public void increaseXp() {
        xp += 3;
    }


    public void load() {
        generator = GeneratorsManager.loadGenerator(this);
    }

    public void unload() {
        GeneratorsManager.unloadGenerator(position);
        online.clear();
    }

    public void online(Player pl) {
        online.add(pl);
    }

    public boolean offline(Player pl) {
        online.remove(pl);
        return online.isEmpty();
    }

    public void addPlayer(UUID uuid) {
        var essentials = EssentialsAPI.getInstance();
        Location home = Location.fromObject(position.add(0.5, 1, 0.5), position.level);
        if (!allPlayers.isEmpty() && essentials.isHomeExists(allPlayers.getFirst(), "is")) {
            home = essentials.getHome(allPlayers.getFirst(), "is");
        }
        essentials.setHome(uuid, "is", home);
        allPlayers.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        if (allPlayers.remove(uuid))
            EssentialsAPI.getInstance().removeHome(uuid,"is");
    }

    public Position getPosition() {
        return position;
    }

    public int getLvl() {
        return lvl;
    }

    public long getXp() {
        return xp;
    }

    public long getId() {
        return id;
    }
}
