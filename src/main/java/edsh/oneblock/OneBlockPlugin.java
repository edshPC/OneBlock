package edsh.oneblock;

import cn.nukkit.lang.PluginI18n;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import edsh.oneblock.command.CreateGenerator;
import edsh.oneblock.data.Database;
import edsh.oneblock.gen.GeneratorsManager;
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
        this.getServer().getCommandMap().register("oneblockplugin", new CreateGenerator());
        //dbHelper = new DBHelper("generators");
        db = new Database("oneblock.db");

    }

    @Override
    public void onEnable() {
        Scheduler.instance = getServer().getScheduler();

        //Register the EventListener
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);


        Config config = new Config(new File(this.getDataFolder(), "config.json"), Config.JSON);
        config.save();

        GeneratorsManager.init();

    }

    @Override
    public void onDisable() {
    }
}
