package cc.infure.practice.player.holder;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.game.GameType;
import cc.infure.practice.player.DuelPlayer;
import cc.infure.practice.player.value.Value;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Matthew E on 6/12/2017.
 */
public interface EloHolder extends DuelPlayer {
    default int getElo(GameType gameType) {
        FileConfiguration configFile = getConfigFile();
        return (configFile.isSet(gameType.toString() + ".elo")) ? configFile.getInt(gameType.toString() + ".elo") : InfurePractice.getInstance().getDuelsConfig().getInteger("defaultElo");
    }

    default void setElo(GameType gameType, Value<Integer> value) {
        FileConfiguration configFile = getConfigFile();
        int elo = getElo(gameType);
        switch (value.getValueType()) {
            case ADD:
                configFile.set(gameType.toString() + ".elo", elo + value.getValue());
                break;
            case SET:
                configFile.set(gameType.toString() + ".elo", value.getValue());
                break;
            case TAKE:
                if (value.getValue() > elo) {
                    configFile.set(gameType.toString() + ".elo", 0);
                    break;
                }
                configFile.set(gameType.toString() + ".elo",  (elo - value.getValue()));
                break;
        }
        this.save();
    }
}
