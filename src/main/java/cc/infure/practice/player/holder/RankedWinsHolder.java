package cc.infure.practice.player.holder;

import cc.infure.practice.player.DuelPlayer;
import cc.infure.practice.player.value.Value;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Matthew E on 6/12/2017.
 */
public interface RankedWinsHolder extends DuelPlayer {

    default Integer getRankedWins() {
        FileConfiguration configFile = getConfigFile();
        return (configFile.isSet("rankedWins")) ? configFile.getInt("rankedWins") : 0;
    }

    default void setRankedWins(Value<Integer> value) {
        FileConfiguration configFile = getConfigFile();
        int credits = getRankedWins();
        switch (value.getValueType()) {
            case ADD:
                configFile.set("rankedWins", credits + value.getValue());
                break;
            case SET:
                configFile.set("rankedWins", value.getValue());
                break;
            case TAKE:
                if (value.getValue() > credits) {
                    configFile.set("rankedWins", 0);
                    break;
                }
                configFile.set("rankedWins", (credits - value.getValue()));
                break;
        }
        this.save();
    }
}
