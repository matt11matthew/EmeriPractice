package cc.infure.practice.commands;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.UUID;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class SetUpArenaCommand implements CommandExecutor {
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
            giveWand(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.startingSetUp")));
            return true;
        }
        return true;
    }

    private void giveWand(Player player) {
        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.AQUA + "Set positions " + ChatColor.GRAY + "(Right-Click for pos1 | Left-Click for pos2)");
        itemMeta.setLore(Collections.singletonList(ChatColor.GRAY + UUID.randomUUID().toString()));
        itemStack.setItemMeta(itemMeta);
        player.getInventory().addItem(itemStack);
    }
}
