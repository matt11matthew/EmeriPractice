package net.emeri.practice.commands;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class DuelsReloadCommand implements CommandExecutor {

    /**
     * Executes the given command, returning its success
     *
     * @param sender  Source of the command
     * @param command CoEmmand which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Config config = EmeriPractice.getInstance().getDuelsConfig();
            Player player = (Player) sender;
            String permission = config.getString("permissions.reloadDuels");
            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.errorNoPermission").replace("{permission}", permission)));
                return true;
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.reloadConfig")));
            return true;
        }
        return true;
    }
}
