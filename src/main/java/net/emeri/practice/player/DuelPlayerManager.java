package net.emeri.practice.player;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.player.value.Value;
import net.emeri.practice.game.GameType;
import net.emeri.practice.player.value.ValueType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class DuelPlayerManager {
    private static DuelPlayerManager instance;
    private Map<UUID, AbstractDuelPlayer> duelPlayerMap;

    public static DuelPlayerManager getInstance() {
        if (instance == null) {
            instance = new DuelPlayerManager();
        }
        return instance;
    }

    public DuelPlayerManager() {
        instance = this;
        this.duelPlayerMap = new HashMap<>();
    }

    public boolean isPlayer(UUID uniqueId) {
        if (this.duelPlayerMap.containsKey(uniqueId)) {
            return true;
        }
        File file = new File(EmeriPractice.getInstance().getDataFolder() + "/playerdata/", uniqueId.toString() + ".yml");
        if (file.exists()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (configuration.getString("uniqueId").equalsIgnoreCase(uniqueId.toString())) {
                return true;
            }
        }
        return false;
    }

    public AbstractDuelPlayer getDuelPlayer(UUID uniqueId) {
        if (this.duelPlayerMap.containsKey(uniqueId)) {
            return duelPlayerMap.get(uniqueId);
        }
        File file = new File(EmeriPractice.getInstance().getDataFolder() + "/playerdata/", uniqueId.toString() + ".yml");
        if (file.exists()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (configuration.getString("uniqueId").equalsIgnoreCase(uniqueId.toString())) {
                return new AbstractDuelPlayerImpl(uniqueId, configuration.getString("name"));
            }
        }
        return null;
    }

    public AbstractDuelPlayer getDuelPlayer(String name) {
        for (AbstractDuelPlayer abstractDuelPlayer : this.duelPlayerMap.values()) {
            if (abstractDuelPlayer.getName().equalsIgnoreCase(name)) {
                return abstractDuelPlayer;
            }
        }
        File file = new File(EmeriPractice.getInstance().getDataFolder() + "/playerdata/");
        if ((file.exists()) && (file.listFiles()!=null)&&(file.listFiles().length>0)) {
            for (File file1 : file.listFiles()) {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(file1);
                if (configuration.getString("name").equalsIgnoreCase(name)) {
                    return new AbstractDuelPlayerImpl(UUID.fromString(configuration.getString("uniqueId")), configuration.getString("name"));
                }
            }
        }

        return null;
    }

    public AbstractDuelPlayer create(UUID uuid, String username) {
        if (duelPlayerMap.containsKey(uuid)) {
            return duelPlayerMap.get(uuid);
        }
        File file = new File(EmeriPractice.getInstance().getDataFolder() + "/playerdata/", uuid.toString() + ".yml");
        if (file.exists()) {
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (configuration.getString("uniqueId").equalsIgnoreCase(uuid.toString())) {
                AbstractDuelPlayer duelPlayer = new AbstractDuelPlayerImpl(uuid, configuration.getString("name"));
                this.duelPlayerMap.put(uuid, duelPlayer);
                return duelPlayer;
            }
        }
        AbstractDuelPlayerImpl abstractDuelPlayer = new AbstractDuelPlayerImpl(uuid, username);
        abstractDuelPlayer.setRankedWins(new Value<>(0, ValueType.SET));
        abstractDuelPlayer.setUnRankedWins(new Value<>(0, ValueType.SET));
        for (GameType gameType : GameType.values()) {
            if (Arrays.asList(GameType.getRankedGames()).contains(gameType)) {
                abstractDuelPlayer.setElo(gameType, new Value<>(EmeriPractice.getInstance().getDuelsConfig().getInteger("defaultElo"), ValueType.SET));
            }
            abstractDuelPlayer.setKills(gameType, new Value<>(0, ValueType.SET));
            abstractDuelPlayer.setDeaths(gameType, new Value<>(0, ValueType.SET));
        }
        FileConfiguration configFile = abstractDuelPlayer.getConfigFile();
        configFile.set("uniqueId", uuid.toString());
        configFile.set("name", username);
        try {
            configFile.save(abstractDuelPlayer.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.duelPlayerMap.put(uuid, abstractDuelPlayer);
        return abstractDuelPlayer;
    }

    public AbstractDuelPlayer getOnlineDuelPlayer(UUID uniqueId) {
        if (duelPlayerMap.containsKey(uniqueId)) {
            return duelPlayerMap.get(uniqueId);
        }
        AbstractDuelPlayer duelPlayer = getDuelPlayer(uniqueId);
        if (duelPlayer == null) {
            return null;
        }
        this.duelPlayerMap.put(uniqueId, duelPlayer);
        return duelPlayer;
    }

    public boolean isPlayer(String name) {
        for (AbstractDuelPlayer abstractDuelPlayer : this.duelPlayerMap.values()) {
            if (abstractDuelPlayer.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        File file = new File(EmeriPractice.getInstance().getDataFolder() + "/playerdata/");
        if ((file.exists()) && (file.listFiles()!=null)&&(file.listFiles().length>0)) {
            for (File file1 : file.listFiles()) {
                FileConfiguration configuration = YamlConfiguration.loadConfiguration(file1);
                if (configuration.getString("name").equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public UUID getUniqueId(String name) {
        if (isPlayer(name)) {
            AbstractDuelPlayer duelPlayer = getDuelPlayer(name);
            if (duelPlayer != null) {
                return duelPlayer.getUniqueId();
            }
        }
        return null;
    }
}

