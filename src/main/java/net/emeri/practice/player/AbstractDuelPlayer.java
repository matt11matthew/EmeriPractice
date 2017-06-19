package net.emeri.practice.player;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.config.Config;
import net.emeri.practice.game.Game;
import net.emeri.practice.game.GameManager;
import net.emeri.practice.player.holder.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Matthew E on 6/12/2017.
 */
public abstract class AbstractDuelPlayer implements DuelPlayer, EloHolder, RankedWinsHolder, UnRankedWinsHolder, DeathsHolder, KillsHolder {
    private UUID uniqueId;
    private String username;
    private File file;
    private FileConfiguration configFile;

    public AbstractDuelPlayer(UUID uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.file = new File(EmeriPractice.getInstance().getDataFolder() + "/playerdata/",this.getUniqueId() + ".yml");
        if (!this.file.getParentFile().exists()) {
            this. file.getParentFile().mkdirs();
        }
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.configFile = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AbstractDuelPlayer{");
        sb.append("uniqueId=").append(uniqueId);
        sb.append(", username='").append(username).append('\'');
        sb.append(", file=").append(file);
        sb.append(", configFile=").append(configFile);
        sb.append('}');
        return sb.toString();
    }

    public static AbstractDuelPlayer get(UUID uniqueId) {
        return DuelPlayerManager.getInstance().getDuelPlayer(uniqueId);
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void save() {
        try {
            this.configFile.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(this.uniqueId);
        if ((player != null) && (player.isOnline())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    @Override
    public FileConfiguration getConfigFile() {
        return configFile;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }

    private Map<AbstractDuelPlayer, DuelSettings> pendingDuelRequests = new ConcurrentHashMap<>();
    private Map<AbstractDuelPlayer, DuelSettings> inComingDuelRequests = new ConcurrentHashMap<>();

    public boolean hasPendingDuelRequest(String name) {
        for (DuelPlayer pendingDuelRequest : pendingDuelRequests.keySet()) {
            if (pendingDuelRequest.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasIncomingDuelRequest(String name) {
        for (DuelPlayer duelPlayer : inComingDuelRequests.keySet()) {
            if (duelPlayer.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void acceptDuelRequest(String name) {
        Config config = EmeriPractice.getInstance().getDuelsConfig();
        if (!hasIncomingDuelRequest(name)) {
            sendMessage(config.getString("messages.noPendingRequest").replace("{name}", name));
            return;
        }
        if (isInGame) {
            sendMessage(config.getString("messages.yourIngame"));
            return;
        }
        AbstractDuelPlayer abstractDuelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(name);
        if (abstractDuelPlayer == null) {
            return;
        }
        if (abstractDuelPlayer.isInGame()) {
            sendMessage(config.getString("messages.ingame").replace("{name}", name));
            return;
        }
        DuelSettings duelSettings = inComingDuelRequests.get(abstractDuelPlayer);
        removeDuelRequest(abstractDuelPlayer);
        abstractDuelPlayer.removeDuelRequest(this);
        sendMessage(config.getString("messages.acceptedDuelRequest").replace("{name}", name));
        abstractDuelPlayer.sendMessage(config.getString("messages.acceptDuelRequest").replace("{name}", getName()));
        isInGame = true;
        abstractDuelPlayer.setInGame(true);
        GameManager.getInstance().setupGame(new Game(UUID.randomUUID().toString(), duelSettings.getDuelType(), false, this, abstractDuelPlayer, duelSettings.getArena()));
    }

    public void declineDuelRequest(String name) {
        Config config = EmeriPractice.getInstance().getDuelsConfig();
        if (!hasIncomingDuelRequest(name)) {
            sendMessage(config.getString("messages.noPendingRequest").replace("{name}", name));
            return;
        }
        if (isInGame) {
            sendMessage(config.getString("messages.yourIngame"));
            return;
        }
        AbstractDuelPlayer abstractDuelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(name);
        if (abstractDuelPlayer == null) {
            return;
        }
        if (abstractDuelPlayer.isInGame()) {
            sendMessage(config.getString("messages.ingame").replace("{name}", name));
            return;
        }
        removeDuelRequest(abstractDuelPlayer);
        abstractDuelPlayer.removeDuelRequest(this);
        sendMessage(config.getString("messages.declinedDuelRequest").replace("{name}", name));
        abstractDuelPlayer.sendMessage(config.getString("messages.declineDuelRequest").replace("{name}", getName()));
        isInGame = false;
        abstractDuelPlayer.setInGame(false);

        //GameManager.getInstance().setupGame(new Game(UUID.randomUUID().toString(), GameType.REFILL, true, this, abstractDuelPlayer));
    }

    public void removeDuelRequest(AbstractDuelPlayer abstractDuelPlayer) {
        if (pendingDuelRequests.containsKey(abstractDuelPlayer)) {
            pendingDuelRequests.remove(abstractDuelPlayer);
        }
        if (inComingDuelRequests.containsKey(abstractDuelPlayer)) {
            inComingDuelRequests.remove(abstractDuelPlayer);
        }
    }

    public boolean isInGame() {
        return isInGame;
    }

    public AbstractDuelPlayer setInGame(boolean inGame) {
        isInGame = inGame;
        return this;
    }

    private boolean isInGame;
    /*
      ingame: "&c{name} is already in a game."
 declinedDuelRequest: "&eYou've declined the duel request from {name}"
  declineDuelRequest: "&e{name} has declined the duel request from you"
  acceptedDuelRequest: "&eAccepted the duel request from {name}"
  acceptDuelRequest: "&e{name} has accepted the duel request"
     */

    public void sendDuelRequest(AbstractDuelPlayer duelPlayer, DuelSettings duelSettings) {
        Config config = EmeriPractice.getInstance().getDuelsConfig();
        if (hasPendingDuelRequest(duelPlayer.username)) {
            sendMessage(config.getString("messages.alreadyHavePendingRequest").replace("{name}", duelPlayer.getName()));
            return;
        }
        if (hasIncomingDuelRequest(duelPlayer.getName())) {
            acceptDuelRequest(duelPlayer.getName());
            return;
        }
sendMessage(config.getString("messages.sentDuelRequest").replace("{name}", duelPlayer.getName()));
        duelPlayer.sendMessage(config.getString("messages.incomingDuelRequest").replace("{name}", getName()));
        TextComponent yesComponent = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', config.getString("messages.yesText")));
        yesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', config.getString("messages.yesHoverMessage")))}));
        yesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + getName()));
        TextComponent noComponent = new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', config.getString("messages.noText")));
        noComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', config.getString("messages.noHoverMessage")))}));
        noComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline " + getName()));
        TextComponent component = new TextComponent();
        component.addExtra(yesComponent);
        component.addExtra(" ");
        component.addExtra(noComponent);
        duelPlayer.getPlayer().spigot().sendMessage(component);
        pendingDuelRequests.put(duelPlayer, duelSettings);
        duelPlayer.addInComingDuelRequest(this, duelSettings);
    }

    private void addInComingDuelRequest(AbstractDuelPlayer duelPlayer, DuelSettings duelSettings) {
        this.inComingDuelRequests.put(duelPlayer, duelSettings);
    }

    public void cancelRequests() {
        setInGame(false);
        for (AbstractDuelPlayer inComingDuelRequest : inComingDuelRequests.keySet()) {
            declineDuelRequest(inComingDuelRequest.getName());
        }
        for (AbstractDuelPlayer pendingDuelRequest : pendingDuelRequests.keySet()) {
            pendingDuelRequest.removeInComing(this);
        }
        inComingDuelRequests.clear();
        pendingDuelRequests.clear();

    }

    private void removeInComing(AbstractDuelPlayer duelPlayer) {
        if (inComingDuelRequests.containsKey(duelPlayer)) {
            inComingDuelRequests.remove(duelPlayer);
        }
    }

        /*

         yesHoverMessage: "&aClick to accept duel"
  yesText: "&a(accept)"
  noHoverMessage: "&cClick to decline duel"
  noText: "&c(decline)"

      sentDuelRequest: "&eSent duel request to {name}"
  declinedDuelRequest: "&eYou've declined the duel request from {name}"
  declineDuelRequest: "&e{name} has declined the duel request from you"
  acceptedDuelRequest: "&eAccepted the duel request from {name}"
  acceptDuelRequest: "&e{name} has accepted the duel request"

    incomingDuelRequest: "&e{name} wants to duel you {yes} or {no}"
  yesHoverMessage: "&aClick to accept duel"
  yesText: "&a(accept)"
  noHoverMessage: "&cClick to decline duel"
  noText: "&c(decline)"

     */
}
