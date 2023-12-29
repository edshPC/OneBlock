package edsh.oneblock.util;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import edsh.oneblock.data.Database;

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

}
