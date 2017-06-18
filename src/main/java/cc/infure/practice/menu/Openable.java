package cc.infure.practice.menu;

import org.bukkit.entity.Player;

/**
 * Created by matt1 on 3/21/2017.
 */
public interface Openable {

    default void open() {}
    default void open(Player player) {}

}
