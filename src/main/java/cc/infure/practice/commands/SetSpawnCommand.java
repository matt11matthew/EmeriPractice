package cc.infure.practice.commands;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.config.Config;
import cc.infure.practice.utilities.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class SetSpawnCommand implements CommandExecutor {

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
            String permission = config.getString("permissions.setSpawn");
            if (!player.hasPermission(permission)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.errorNoPermission").replace("{permission}", permission)));
                return true;
            }
            String location = LocationUtil.getStringFromPlayerLocation(player.getLocation());
            config.set("locations.spawnPoint", LocationUtil.getStringFromPlayerLocation(player.getLocation()));
            try {
                config.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.setSpawnPoint").replace("{location}", location)));
            return true;
        }
        return true;
    }
}
