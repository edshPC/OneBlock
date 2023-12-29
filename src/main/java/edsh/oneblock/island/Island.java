package edsh.oneblock.island;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.gen.GeneratorsManager;

import java.util.LinkedList;
import java.util.UUID;

public class Island {

    private BlockGenerator generator;
    private final Position position;
    private int lvl;
    private long xp;

    private final LinkedList<UUID> allPlayers;
    private final LinkedList<Player> online = new LinkedList<>();

    public Island(Position pos, int lvl, long xp, LinkedList<UUID> allPlayers) {
        this.position = pos;
        if(pos.level == null) this.position.level = IslandManager.level;
        this.lvl = lvl;
        this.xp = xp;
        this.allPlayers = allPlayers;
    }

    public Island(Position pos) {
        this(pos, 1, 0, new LinkedList<>());
    }

    public void load() {
        generator = GeneratorsManager.loadGenerator(position, lvl);
    }

    public void online(Player pl) {
        online.add(pl);
    }

    public void offline(Player pl) {
        online.remove(pl);
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
}
