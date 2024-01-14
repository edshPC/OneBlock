package edsh.oneblock.island;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.utils.BossBarColor;
import cn.nukkit.utils.DummyBossBar;
import cn.yescallop.essentialsnk.EssentialsAPI;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.gen.GeneratorsManager;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public class Island {

    private final long id;
    private BlockGenerator generator;
    private final Position position;
    private Location home;
    private int lvl;
    private long xp;
    private long xpRequired;

    private UUID owner;
    private final Set<UUID> allPlayers;
    private final Set<Player> online = new LinkedHashSet<>();

    public Island(Position pos, long id, int lvl, long xp, UUID owner, Set<UUID> allPlayers) {
        this.position = pos.floor();
        this.id = id;
        if (pos.level == null) this.position.level = IslandManager.level;
        this.lvl = lvl;
        this.xp = xp;
        this.xpRequired = IslandManager.getRequiredXp(lvl);
        this.owner = owner;
        this.allPlayers = allPlayers;

        if(owner != null)
            home = EssentialsAPI.getInstance().getHome(owner, "is");
        if (home == null)
            home = Location.fromObject(position.add(0.5, 1, 0.5), position.level);
    }

    public Island(Position pos, long id) {
        this(pos, id, 1, 0, null, new LinkedHashSet<>());
    }

    public void increaseXp() {
        xp += 3;
        if (xp >= xpRequired) levelUp();
        updateInfo();
    }

    public void levelUp() {
        lvl++;
        xp = 0;
        xpRequired = IslandManager.getRequiredXp(lvl);
        GeneratorsManager.updateBlockWeights(generator, lvl);
    }

    public void updateInfo() {
        float percentage = Math.max(((float) xp / xpRequired) * 100, 1);
        for (Player pl : online) {
            DummyBossBar bossBar = new DummyBossBar.Builder(pl)
                    .color(BossBarColor.PURPLE)
                    .length(percentage)
                    .text("§bУровень острова: §l§e" + lvl + "§r§b, Опыт: §e" + xp + "/" + xpRequired)
                    .build();
            pl.getDummyBossBars().values().forEach(DummyBossBar::destroy);
            pl.getDummyBossBars().clear();
            pl.createBossBar(bossBar);
        }

    }

    public void load() {
        generator = GeneratorsManager.loadGenerator(this);
    }

    public void unload() {
        GeneratorsManager.unloadGenerator(position);
        online.clear();
    }

    public void online(Player pl) {
        addPlayer(pl.getUniqueId());
        online.add(pl);
        updateInfo();
    }

    public boolean isOnline(Player pl) {
        return online.contains(pl);
    }

    public boolean offline(Player pl) {
        online.remove(pl);
        pl.getDummyBossBars().values().forEach(DummyBossBar::destroy);
        return online.isEmpty();
    }

    public void setHome(Location home) {
        var essentials = EssentialsAPI.getInstance();
        for (UUID uuid : allPlayers) {
            essentials.setHome(uuid, "is", home);
        }
        this.home = home;
    }

    public void addPlayer(UUID uuid) {
        if (!allPlayers.add(uuid)) return;
        setHome(home);
    }

    public boolean removePlayer(UUID uuid) {
        if (allPlayers.remove(uuid))
            EssentialsAPI.getInstance().removeHome(uuid, "is");
        return allPlayers.isEmpty();
    }

    public boolean isOwner(Player pl) {
        return pl.getUniqueId().equals(owner);
    }

    void setOwner(Player pl) {
        this.owner = pl.getUniqueId();
    }

    public UUID getOwner() {
        return owner;
    }

    public Position getPosition() {
        return position;
    }

    public Set<Player> getOnline() {
        return online;
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
