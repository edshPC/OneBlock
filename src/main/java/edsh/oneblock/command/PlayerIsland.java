package edsh.oneblock.command;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Location;
import cn.yescallop.essentialsnk.EssentialsAPI;
import edsh.oneblock.OneBlockPlugin;
import edsh.oneblock.island.Island;
import edsh.oneblock.island.IslandManager;

import java.util.List;
import java.util.Map;

public class PlayerIsland extends PluginCommand<OneBlockPlugin> {

    public PlayerIsland() {

        super("island", "Управление островом", OneBlockPlugin.INSTANCE);

        this.setAliases(new String[]{"is"});

        this.setPermission("oneblock.is");

        this.getCommandParameters().clear();

        /*
         * 1.getCommandParameters return a Map<String,cn.nukkit.command.data.CommandParameter[]>,
         * in which each entry can be regarded as a subcommand or a command pattern.
         * 2.Each subcommand cannot be repeated.
         * 3.Optional arguments must be used at the end of the subcommand or consecutively.
         */
        this.getCommandParameters().put("pattern1", new CommandParameter[]{
                CommandParameter.newEnum("operation", new String[]{
                        "create",
                        "leave",
                        "home",
                        "sethome",
                        "accept",
                        "deny"
                })
                //CommandParameter.newType("coords", false, CommandParamType.BLOCK_POSITION)
        });

        this.getCommandParameters().put("pattern2", new CommandParameter[]{
                CommandParameter.newEnum("operation", new String[]{
                        "invite"
                }),
                CommandParameter.newType("player", false, CommandParamType.TARGET)
        });

        this.enableParamTree();
    }


    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        Player pl = sender.asPlayer();
        Island island = IslandManager.getIsland(pl);
        boolean hasIsland = island != null;
        boolean isOwner = hasIsland && island.isOwner(pl);
        var list = result.getValue();

        switch ((String) list.getResult(0)) {
            case "create" -> {
                if (IslandManager.createPlayerIsland(pl)) {
                    pl.sendMessage("§aОстров был успешно создан! Напиши §b/is home §aчтобы телепортироавться");
                } else {
                    pl.sendMessage("§cУ вас уже есть остров!");
                }
            }
            case "leave" -> {
                if (IslandManager.tryLeavePlayer(pl)) {
                    pl.sendMessage("§bВы покинули остров!");
                } else {
                    pl.sendMessage("§cУ вас нет острова!");
                }
            }
            case "home" -> {
                Location home = EssentialsAPI.getInstance().getHome(pl, "is");
                if (home == null) pl.sendMessage("§cВам не принадлежит никакого острова!");
                else pl.teleport(home);
            }
            case "sethome" -> {
                if (!hasIsland) {
                    pl.sendMessage("§cВам не принадлежит никакого острова!");
                    break;
                }
                Location home = pl.getLocation();
                island.setHome(home);
                pl.sendMessage("§aДом установлен в новом месте!");
            }

            case "invite" -> {
                if (!isOwner) {
                    pl.sendMessage("§cВам не принадлежит никакого острова или вы не владелец!");
                    break;
                }
                List<Player> pls = list.getResult(1);
                if (pls.size() != 1) {
                    pl.sendMessage("§cВы можете пригласить только одного игрока!");
                    break;
                }
                IslandManager.invitePlayer(pl, pls.get(0), island);
            }

            case "accept" -> IslandManager.manageInvitation(pl, true);

            case "deny" -> IslandManager.manageInvitation(pl, false);

        }

        return 1;
    }
}
