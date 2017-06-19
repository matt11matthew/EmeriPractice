package net.emeri.practice.commands;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.config.Config;
import net.emeri.practice.player.AbstractDuelPlayer;
import net.emeri.practice.player.DuelPlayerManager;
import net.emeri.practice.player.DuelSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Matthew E on 6/13/2017.
 */
public class DuelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Config config = EmeriPractice.getInstance().getDuelsConfig();
            AbstractDuelPlayer duelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(player.getUniqueId());
            if (args.length == 2) {
                String name = args[1];
                if (name.equalsIgnoreCase(player.getName())) {
                    return true;
                }
                if (args[0].equalsIgnoreCase("accept")) {
                    duelPlayer.acceptDuelRequest(name);
                    return true;
                } else if (args[0].equalsIgnoreCase("decline")) {
                    duelPlayer.declineDuelRequest(name);
                    return true;
                }
            }
            if (args.length == 1) {
                String name = args[0];

                if (name.equalsIgnoreCase(player.getName())) {
                    return true;
                }
                AbstractDuelPlayer abstractDuelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(name);
                if (abstractDuelPlayer == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.playerDoesNotExist").replace("{name}", name)));
                    return true;
                }
                DuelSettings duelSettings = new DuelSettings(duelPlayer, abstractDuelPlayer);
                duelSettings.openMainMenu();
                //GameManager.getInstance().setupGame(new Game("testgame", GameType.REFILL, true,duelPlayer, abstractDuelPlayer));
                return true;
            }
        }
        return true;
    }
    /*
      sentDuelRequest: "&eSent duel request to {name}"
  declinedDuelRequest: "&eYou've declined the duel request from {name}"
  declineDuelRequest: "&e{name} has declined the duel request from you"
  acceptedDuelRequest: "&eAccepted the duel request from {name}"
  acceptDuelRequest: "&e{name} has accepted the duel request"
     */
}
