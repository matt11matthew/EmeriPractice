package net.emeri.practice.player.holder;

import net.emeri.practice.player.DuelPlayer;
import net.emeri.practice.player.value.Value;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Matthew E on 6/12/2017.
 */
public interface UnRankedWinsHolder extends DuelPlayer {

    default Integer getUnRankedWins() {
        FileConfiguration configFile = getConfigFile();
        return (configFile.isSet("unRankedWins")) ? configFile.getInt("unRankedWins") : 0;
    }

    default void setUnRankedWins(Value<Integer> value) {
        FileConfiguration configFile = getConfigFile();
        int credits = getUnRankedWins();
        switch (value.getValueType()) {
            case ADD:
                configFile.set("unRankedWins", credits + value.getValue());
                break;
            case SET:
                configFile.set("unRankedWins", value.getValue());
                break;
            case TAKE:
                if (value.getValue() > credits) {
                    configFile.set("unRankedWins", 0);
                    break;
                }
                configFile.set("unRankedWins", (credits - value.getValue()));
                break;
        }
        this.save();
    }
}
