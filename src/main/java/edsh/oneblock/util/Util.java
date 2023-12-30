package edsh.oneblock.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import edsh.oneblock.data.Database;
import edsh.oneblock.data.PlayerData;

public class Util {

    public static Server server;
    public static Config config;
    public static PluginLogger logger;
    public static Database db;

    public static String getId(Player pl) {
        var login = pl.getLoginChainData();
        if(login.isXboxAuthed()) return login.getXUID();
        return login.getDeviceId();
    }

    public static void savePlayer(Player pl, long island, boolean owner) {
        PlayerData data = new PlayerData();
        data.uuid = pl.getUniqueId();
        data.name = pl.getName();
        data.island_id = island;
        data.is_owner = owner;
        db.savePlayerData(data);
    }

}
