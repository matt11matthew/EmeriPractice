package cc.infure.practice.game;

import cc.infure.practice.utilities.LocationUtil;
import org.bukkit.Location;

import java.io.IOException;

/**
 * Created by Matthew E on 6/16/2017.
 */
public class Arena {
    private String name;
    private Location positionOne;
    private Location positionTwo;
    private boolean isSetUp;
    private String builder;
    private ArenaConfig config;

    public Arena(String name) {
        this.name = name;
        this.config = new ArenaConfig(name);
        this.positionOne = LocationUtil.getBlockLocationFromString(config.getString("pos1"));
        this.positionTwo = LocationUtil.getBlockLocationFromString(config.getString("pos2"));
        this.isSetUp = config.getBoolean("isSetUp");
        this.builder = config.getString("builder");
    }

    public Arena() {
    }

    public Arena setName(String name) {
        if (config == null) {
            this.config = new ArenaConfig(name);
        }
        this.name = name;
        return this;
    }

    public Arena setPositionOne(Location positionOne) {
        this.positionOne = positionOne;
        return this;
    }

    public Arena setPositionTwo(Location positionTwo) {
        this.positionTwo = positionTwo;
        return this;
    }

    public Arena setSetUp(boolean setUp) {
        isSetUp = setUp;
        return this;
    }

    public Arena setBuilder(String builder) {
        this.builder = builder;
        return this;
    }

    public Arena setConfig(ArenaConfig config) {
        this.config = config;
        return this;
    }

    public Location getPositionOne() {
        return positionOne;
    }

    public Location getPositionTwo() {
        return positionTwo;
    }

    public boolean isSetUp() {
        return isSetUp;
    }

    public String getBuilder() {
        return builder;
    }

    public ArenaConfig getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public void save() {
        config.set("name", name);
        config.set("builder",builder);
        config.set("pos1", LocationUtil.getStringFromBlockLocation(positionOne));
        config.set("pos2", LocationUtil.getStringFromBlockLocation(positionTwo));
        config.set("isSetUp", isSetUp);
        try {
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exists() {
        return ((name != null) && (builder != null) && (positionOne != null) && (positionTwo != null));
    }
}
