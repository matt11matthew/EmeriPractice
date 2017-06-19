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

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

/**
 * Created by Matthew E on 6/13/2017.
 */
public class StatsCommand implements CommandExecutor {
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
        Config config = EmeriPractice.getInstance().getDuelsConfig();
        if (args.length == 0) {
            if (sender instanceof Player) {
                this.sendStatsToPlayer((Player) sender, ((Player) sender).getUniqueId());
                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.consoleHasNoStats")));
                return true;
            }
        } else if (args.length == 1) {
            String name = args[0];
            if (!DuelPlayerManager.getInstance().isPlayer(name)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.playerDoesNotExist").replace("{name}", name)));
                return true;
            }
            UUID uuid = DuelPlayerManager.getInstance().getUniqueId(name);
            if (uuid == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.playerDoesNotExist").replace("{name}", name)));
                return true;
            }
            sendStatsToPlayer((Player) sender, uuid);
            return true;
        }
        return true;
    }

    private void sendStatsToPlayer(Player player, UUID uuid) {
        Config config = EmeriPractice.getInstance().getDuelsConfig();
        AbstractDuelPlayer duelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(uuid);
        if (duelPlayer == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.playerDoesNotExist").replace("{name}", "?")));
            return;
        }
        List<String> playerStatsStringList = config.getStringList("messages.playerStats");
        int deaths = 0;
        for (GameType gameType : GameType.values()) {
            deaths += duelPlayer.getDeaths(gameType);
        }
        int kills = 0;
        for (GameType gameType : GameType.values()) {
            kills += duelPlayer.getKills(gameType);
        }
        for (String line : playerStatsStringList) {
            line = ChatColor
                    .translateAlternateColorCodes('&', line)
                    .replaceAll("\\{name}", duelPlayer.getName())
                    .replaceAll("\\{unranked_wins}", duelPlayer.getUnRankedWins() + "")
                    .replaceAll("\\{ranked_wins}", duelPlayer.getRankedWins() + "")
                    .replaceAll("\\{total_deaths}", deaths + "")
                    .replaceAll("\\{total_kills}", kills + "")
                    .replaceAll("\\{global_elo}", getGlobalElo(duelPlayer) + "")
                    .replaceAll("\\{total_kdr}", new DecimalFormat("###.#").format(getKdr(kills, deaths)));
            for (GameType gameType : GameType.values()) {
                line = line.replaceAll("\\{" + gameType.toString().toLowerCase() + "_kills}", duelPlayer.getKills(gameType) + "");
                line = line.replaceAll("\\{" + gameType.toString().toLowerCase() + "_deaths}", duelPlayer.getDeaths(gameType) + "");
                line = line.replaceAll("\\{" + gameType.toString().toLowerCase() + "_elo}", duelPlayer.getElo(gameType) + "");
                line = line.replaceAll("\\{" + gameType.toString().toLowerCase() + "_kdr}", new DecimalFormat("###.#").format(getKdr(duelPlayer.getKills(gameType), duelPlayer.getDeaths(gameType))));
            }
            player.sendMessage(line);
        }
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

    public double getKdr(double kills, double deaths) {
        if (kills == 0) {
            return 0;
        } else {
            if (deaths != 0) {
                return (kills/deaths);
            } else {
                return kills;
            }
        }
    }

    /*
      - "&8&m---------------------------------"
  - "&eUsername: &b{name}"
  - "&e&lElo"
  - "  &eRefill: &b{refill_elo}"
  - "  &eNo Refill: &b{no_refill_elo}"
  - "&e&lDeaths"
  - "  &eRefill: &b{refill_deaths}"
  - "  &eNo Refill: &b{no_refill_deaths}"
  - "&e&lKills"
  - "  &eRefill: &b{refill_kills}"
  - "  &eNo Refill: &b{no_refill_kills}"
  - "&eCredits: &b{credits}"
  - "&eTotal Kills: &b{total_kills}"
  - "&eTotal Deaths: &b{total_deaths}"
  - "&eTotal KDR: &b{total_kdr}"
  - "&eGlobal Elo: &b{global_elo}"
  - "&eRanked Wins: &b{ranked_wins}"
  - "&eUnranked Wins: &b{unranked_wins}"
  - "&8&m---------------------------------"
     */
}
