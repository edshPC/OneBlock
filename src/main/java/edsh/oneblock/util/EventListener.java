package edsh.oneblock.util;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.*;
import edsh.oneblock.OneBlockPlugin;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.gen.GeneratorsManager;

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

    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLoginEvent(PlayerLoginEvent event) {

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLoginEvent(PlayerPreLoginEvent event) {

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLocallyInitialized(PlayerLocallyInitializedEvent event) {

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

    }
}
