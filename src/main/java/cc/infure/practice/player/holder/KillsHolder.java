package cc.infure.practice.player.holder;

import cc.infure.practice.game.GameType;
import cc.infure.practice.player.DuelPlayer;
import cc.infure.practice.player.value.Value;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Matthew E on 6/12/2017.
 */
public interface KillsHolder extends DuelPlayer {
    default int getKills(GameType gameType) {
        FileConfiguration configFile = getConfigFile();
        return (configFile.isSet(gameType.toString() + ".kills")) ? configFile.getInt(gameType.toString() + ".kills") : 0;
    }

    default void setKills(GameType gameType, Value<Integer> value) {
        FileConfiguration configFile = getConfigFile();
        int elo = getKills(gameType);
        switch (value.getValueType()) {
            case ADD:
                configFile.set(gameType.toString() + ".kills", elo + value.getValue());
                break;
            case SET:
                configFile.set(gameType.toString() + ".kills", value.getValue());
                break;
            case TAKE:
                if (value.getValue() > elo) {
                    configFile.set(gameType.toString() + ".kills", 0);
                    break;
                }
                configFile.set(gameType.toString() + ".kills",  (elo - value.getValue()));
                break;
        }
        this.save();
    }
}
