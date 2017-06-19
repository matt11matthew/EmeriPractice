package net.emeri.practice.commands;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.config.Config;
import net.emeri.practice.game.Arena;
import net.emeri.practice.game.ArenaManager;
import net.emeri.practice.utilities.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class SetArenaSpawnPointCommand implements CommandExecutor {

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
            Config config = EmeriPractice.getInstance().getDuelsConfig();
            Player player = (Player) sender;
            String permission = config.getString("permissions.setArenaSpawnPoint");
            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.errorNoPermission").replace("{permission}", permission)));
                return true;
            }
            if (args.length == 2) {
                String name = args[0];
                Arena arena = ArenaManager.getInstance().getArena(name);
                String location = LocationUtil.getStringFromPlayerLocation(player.getLocation());
                if (args[1].equalsIgnoreCase("one")) {
                    arena.setPositionOne(player.getLocation());
                } else if (args[1].equalsIgnoreCase("two")) {
                    arena.setPositionTwo(player.getLocation());
                }
               arena.save();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.setSpawnPoint").replace("{location}", location)));
                return true;
            }
        }
        return true;
    }
}
