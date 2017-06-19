package net.emeri.practice.game.kits;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.game.GameType;
import net.emeri.practice.config.Config;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 6/15/2017.
 */
public class Kit {
    private GameType gameType;
    private Map<Integer, ItemStack> armorMap = new HashMap<>();
    private Map<Integer, ItemStack> itemStackMap = new HashMap<>();

    public Kit(GameType gameType) {
        this.gameType = gameType;
        Config duelsConfig = EmeriPractice.getInstance().getDuelsConfig();
        for (String item : duelsConfig.getStringList("kits." + gameType.toString() + ".items")) {
            KitItem kitItem = new KitItem(item);
            this.itemStackMap.put(kitItem.getSlot(), kitItem.getItemStack());
        }
        for (String item : duelsConfig.getStringList("kits." + gameType.toString() + ".armor")) {
            KitItem kitItem = new KitItem(item);
            this.armorMap.put(kitItem.getSlot(), kitItem.getItemStack());
        }
    }

    public void give(Player player) {
        itemStackMap.forEach((integer, itemStack) -> player.getInventory().setItem(integer, itemStack));
        ItemStack[] armorItemStacks = new ItemStack[armorMap.values().size()];
        for (int i = 0; i < armorMap.values().size(); i++) {
            armorItemStacks[i] = armorMap.get(i);
        }
        player.getInventory().setArmorContents(armorItemStacks);
    }

    public GameType getGameType() {
        return gameType;
    }

    public Map<Integer, ItemStack> getArmorMap() {
        return armorMap;
    }

    public Map<Integer, ItemStack> getItemStackMap() {
        return itemStackMap;
    }
}
