package edsh.oneblock.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import edsh.oneblock.gen.BlockGenerator;
import edsh.oneblock.OneBlockPlugin;
import edsh.oneblock.gen.GeneratorsManager;

import java.util.Map;

public class CreateGenerator extends PluginCommand<OneBlockPlugin> {

    public CreateGenerator() {
        /*
        1.the name of the command must be lowercase
        2.Here the description is set in with the key in the language file,Look at en_US.lang or zh_CN.lang.
        This can send different command description to players of different language.
        You must extends PluginCommand to have this feature.
        */
        super("creategenerator", "Creates generator", OneBlockPlugin.INSTANCE);

        //Set the alias for this command
        this.setAliases(new String[]{});

        this.setPermission("oneblock.c");

        /*
         * The following begins to set the command parameters, first need to clean,
         * because NK will fill in several parameters by default, we do not need.
         * */
        this.getCommandParameters().clear();

        /*
         * 1.getCommandParameters return a Map<String,cn.nukkit.command.data.CommandParameter[]>,
         * in which each entry can be regarded as a subcommand or a command pattern.
         * 2.Each subcommand cannot be repeated.
         * 3.Optional arguments must be used at the end of the subcommand or consecutively.
         */
        this.getCommandParameters().put("pattern1", new CommandParameter[]{
                CommandParameter.newType("coords", false, CommandParamType.BLOCK_POSITION)
        });
        /*
         * You'll find two `execute()` methods,
         * where `boolean execute()` is the old NK method,
         * and if you want to use the new `int execute()`,
         * you must add `enableParamTree` at the end of the constructor.
         *
         * Note that you can only choose one of these two execute methods
         */
        this.enableParamTree();
    }

    /**
     * This method is executed only if the command syntax is correct, which means you don't need to verify the parameters yourself.
     * In addition, before executing the command, will check whether the executor has the permission for the command.
     * If these conditions are not met, an error message is automatically displayed.
     *
     * @param sender       The sender of the command
     * @param commandLabel Command label. For example, if `/test 123` is used, the value is `test`
     * @param result       The parsed matching subcommand pattern
     * @param log          The command output tool, which is used to output info, can be controlled by the world's sendCommandFeedback rule
     */
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        //getPlugin().getLogger().info(list.getResult(0).getClass().getName());
        Vector3 pos = list.getResult(0);
        Level level = sender.getPosition().level;

        GeneratorsManager.loadGenerator(Position.fromObject(pos, level), 1);

        return 1;
    }
}
