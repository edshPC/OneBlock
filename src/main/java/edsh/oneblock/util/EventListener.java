package edsh.oneblock.util;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.*;
import edsh.oneblock.OneBlockPlugin;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.gen.GeneratorsManager;
import edsh.oneblock.island.IslandManager;

import java.util.HashMap;
import java.util.UUID;

public class EventListener implements Listener {
    private final OneBlockPlugin plugin;

    public EventListener(OneBlockPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        GeneratorsManager.destroyBlockHandle(event);

    }



    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player pl = event.getPlayer();
        if(!IslandManager.tryLoadPlayerIsland(pl)) {
            pl.sendMessage("У вас нет своего острова! Напиши /is create чтобы создать его или скажи другу чтобы пригласил к себя!");
        }
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLoginEvent(PlayerPreLoginEvent event) {
        Player pl = event.getPlayer();
        String uuid = Util.db.getPlayerUUID(pl);
        pl.setUniqueId(UUID.fromString(uuid));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLocallyInitialized(PlayerLocallyInitializedEvent event) {
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

    }
}
