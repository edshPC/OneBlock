package edsh.oneblock;

import cn.nukkit.lang.PluginI18n;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import edsh.oneblock.command.CreateGenerator;
import edsh.oneblock.command.CreateIsland;
import edsh.oneblock.command.PlayerIsland;
import edsh.oneblock.data.Database;
import edsh.oneblock.gen.GeneratorsManager;
import edsh.oneblock.island.Island;
import edsh.oneblock.island.IslandManager;
import edsh.oneblock.util.EventListener;
import edsh.oneblock.util.Scheduler;

import java.io.File;


public class OneBlockPlugin extends PluginBase {
    public static OneBlockPlugin INSTANCE;
    public static PluginI18n I18N;

    Database db;

    @Override
    public void onLoad() {
        getDataFolder().mkdirs();
        //save Plugin Instance
        INSTANCE = this;
        //register the plugin i18n
        I18N = PluginI18nManager.register(this);
        //register the command of plugin
        var commands = this.getServer().getCommandMap();
        commands.register("CreateGenerator", new CreateGenerator());
        commands.register("CreateIsland", new CreateIsland());
        commands.register("Island", new PlayerIsland());

        db = new Database("oneblock.db");

    }

    @Override
    public void onEnable() {
        Scheduler.instance = getServer().getScheduler();

        //Register the EventListener
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        IslandManager.level = getServer().getLevelByName("void");

        Config config = new Config(new File(this.getDataFolder(), "config.json"), Config.JSON);
        config.save();

        GeneratorsManager.init();

    }

    @Override
    public void onDisable() {
    }
}
