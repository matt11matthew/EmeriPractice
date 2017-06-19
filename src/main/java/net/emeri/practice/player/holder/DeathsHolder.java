package net.emeri.practice.player.holder;

import net.emeri.practice.player.value.Value;
import net.emeri.practice.game.GameType;
import net.emeri.practice.player.DuelPlayer;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Matthew E on 6/12/2017.
 */
public interface DeathsHolder extends DuelPlayer {
    default int getDeaths(GameType gameType) {
        FileConfiguration configFile = getConfigFile();
        return (configFile.isSet(gameType.toString() + ".deaths")) ? configFile.getInt(gameType.toString() + ".deaths") : 0;
    }

    default void setDeaths(GameType gameType, Value<Integer> value) {
        FileConfiguration configFile = getConfigFile();
        int elo = getDeaths(gameType);
        switch (value.getValueType()) {
            case ADD:
                configFile.set(gameType.toString() + ".deaths", elo + value.getValue());
                break;
            case SET:
                configFile.set(gameType.toString() + ".deaths", value.getValue());
                break;
            case TAKE:
                if (value.getValue() > elo) {
                    configFile.set(gameType.toString() + ".deaths", 0);
                    break;
                }
                configFile.set(gameType.toString() + ".deaths",  (elo - value.getValue()));
                break;
        }
        this.save();
    }
}
