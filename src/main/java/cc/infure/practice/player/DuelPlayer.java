package cc.infure.practice.player;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.UUID;

/**
 * Created by Matthew E on 6/12/2017.
 */
public interface DuelPlayer {
    UUID getUniqueId();

    String getName();

    void save();

    File getFile();

    FileConfiguration getConfigFile();

    void sendMessage(String message);
}
