package edsh.oneblock;

import cn.nukkit.lang.PluginI18n;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import edsh.oneblock.command.CreateGenerator;
import edsh.oneblock.command.AdminIsland;
import edsh.oneblock.command.PlayerIsland;
import edsh.oneblock.data.Database;
import edsh.oneblock.gen.GeneratorsManager;
import edsh.oneblock.island.IslandManager;
import edsh.oneblock.util.EventListener;
import edsh.oneblock.util.Scheduler;
import edsh.oneblock.util.Util;

import java.io.File;


public class OneBlockPlugin extends PluginBase {
    public static OneBlockPlugin INSTANCE;
    public static PluginI18n I18N;

    @Override
    public void onLoad() {
        getDataFolder().mkdirs();
        INSTANCE = this;
        I18N = PluginI18nManager.register(this);
        Util.logger = getLogger();
        Util.server = getServer();

        var commands = this.getServer().getCommandMap();
        commands.register("CreateGenerator", new CreateGenerator());
        commands.register("CreateIsland", new AdminIsland());
        commands.register("Island", new PlayerIsland());

        Util.db = new Database("oneblock.db");
    }

    @Override
    public void onEnable() {
        Scheduler.instance = getServer().getScheduler();
        var commands = this.getServer().getCommandMap();
        commands.getCommand("luckperms").setPermission("luckperms");
        commands.getCommand("spark").setPermission("spark");

        //Register the EventListener
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        Util.config = new Config(new File(getDataFolder(), "config.json"), Config.JSON);
        Util.config.save();

        GeneratorsManager.init();
        IslandManager.init();

    }

    @Override
    public void onDisable() {
    }
}
