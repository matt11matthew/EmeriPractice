package net.emeri.practice.commands;

import net.emeri.practice.listeners.PlayerListeners;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/15/2017.
 */
public class RankedMenuCommand implements CommandExecutor {
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
        if (sender instanceof ConsoleCommandSender) {
            Player player = Bukkit.getPlayer(args[0]);
            player.closeInventory();
            PlayerListeners.getInstance().openRankedQueueMenu(player);
            return true;
        }
        return true;
    }
}
