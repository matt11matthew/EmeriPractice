package cc.infure.practice.commands;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.config.Config;
import cc.infure.practice.game.ArenaManager;
import cc.infure.practice.listeners.ArenaListeners;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/13/2017.
 */
public class ConfirmCreationCommand implements CommandExecutor {
    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Config config = InfurePractice.getInstance().getDuelsConfig();
            Player player = (Player) sender;
            String permission = config.getString("permissions.setupArena");
            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.errorNoPermission").replace("{permission}", permission)));
                return true;
            }
            if (ArenaListeners.getInstance().getPosition1() == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.positionNotSet").replace("{position}", "1")));
                return true;
            }
            if (ArenaListeners.getInstance().getPosition2() == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.positionNotSet").replace("{position}", "2")));
                return true;
            }
            if (args.length == 2) {
                String name = args[0];
                String author = args[1];
                boolean b = ArenaManager.getInstance().createArena(name, author,ArenaListeners.getInstance().getPosition1(),ArenaListeners.getInstance().getPosition2());
                if (b) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.createArena")));
                    return true;
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.arenaCreateFail")));
                    return true;

                }
            } else {


            }
            return true;
        }
        return true;
    }
}
