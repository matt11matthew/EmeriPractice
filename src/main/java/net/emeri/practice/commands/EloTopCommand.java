package net.emeri.practice.commands;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.game.GameType;
import net.emeri.practice.player.DuelPlayerManager;
import net.emeri.practice.config.Config;
import net.emeri.practice.player.AbstractDuelPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthew E on 6/13/2017.
 */
public class EloTopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Config config = EmeriPractice.getInstance().getDuelsConfig();
            List<String> stringList = config.getStringList("messages.eloTopList");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', stringList.get(0)));
            int amount = config.getInteger("messages.eloTopAmount");
            List<AbstractDuelPlayer> abstractDuelPlayerList = getTopPlayers();
            if (amount > abstractDuelPlayerList.size()) {
                amount = abstractDuelPlayerList.size();
            }
            for (int i = 0; i < amount; i++) {
                String line = stringList.get(1);
                line = line.replace("{number}", (i + 1) + "");
                line = line.replace("{name}", abstractDuelPlayerList.get(i).getName());
                line = line.replace("{elo}", getGlobalElo(abstractDuelPlayerList.get(i)) + "");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
            return true;
        }
        return true;
    }

    private int getGlobalElo(AbstractDuelPlayer duelPlayer) {
        int total = 0;
        for (GameType gameType : GameType.values()) {
            total += duelPlayer.getElo(gameType);
        }
        if (total > 0) {
            return total / GameType.values().length;
        } else {
            return EmeriPractice.getInstance().getDuelsConfig().getInteger("defaultElo");
        }
    }

    public List<AbstractDuelPlayer> getTopPlayers() {
        List<AbstractDuelPlayer> abstractDuelPlayers =  new ArrayList<>();
        File file = new File(EmeriPractice.getInstance().getDataFolder() + "/playerdata/");
        for (File file1 : file.listFiles()) {
            AbstractDuelPlayer duelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(UUID.fromString(file1.getName().replace(".yml", "")));
            abstractDuelPlayers.add(duelPlayer);
        }
        abstractDuelPlayers.sort((o1, o2) -> getGlobalElo(o2) - getGlobalElo(o1));
        return abstractDuelPlayers;
    }
    /*
  eloTopAmount: 10
  eloTopList:
  - "&e&lElo Top"
  - "&c{number}. &c{name} &e{elo} elo"
     */
}
