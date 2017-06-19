package net.emeri.practice.game;

import net.emeri.practice.EmeriPractice;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Matthew E on 6/16/2017.
 */
public class ArenaConfig {
    private FileConfiguration fileConfiguration;
    private File configurationFile;

    public ArenaConfig(String name) {
        this.configurationFile = new File(EmeriPractice.getInstance().getDataFolder() + "/arenas/" + name + ".yml");
        if (!EmeriPractice.getInstance().getDataFolder().exists()) {
            EmeriPractice.getInstance().getDataFolder().mkdirs();
        }
        if (!configurationFile.getParentFile().exists()) {
            configurationFile.getParentFile().mkdirs();
        }
        if (!this.configurationFile.exists()) {
            try {
                this.configurationFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.configurationFile);
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public void save() throws IOException {
        this.fileConfiguration.save(this.configurationFile);
    }

    public void set(String path, Object value) {
        this.fileConfiguration.set(path, value);
    }

    public List<String> getStringList(String path) {
        return fileConfiguration.getStringList(path);
    }

    public boolean isSet(String path) {
        return fileConfiguration.isSet(path);
    }

    public int getInteger(String path) {
        return fileConfiguration.getInt(path, 1200);
    }

    public String getString(String path) {
        return fileConfiguration.getString(path);
    }

    public boolean getBoolean(String path) {
        return fileConfiguration.getBoolean(path);
    }

    public double getDouble(String path) {
        return fileConfiguration.getDouble(path);
    }
}
