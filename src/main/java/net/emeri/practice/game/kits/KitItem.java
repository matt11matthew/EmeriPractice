package net.emeri.practice.game.kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 6/15/2017.
 */
public class KitItem {
    private String string;
    private ItemStack itemStack;
    private int slot;

    public KitItem(String string) {
        this.string = string;
        this.slot = Integer.parseInt(string.split(":")[0].trim());
        Material type;
        Map<Enchantment, Integer> enchantmentIntegerMap = new HashMap<>();
        if (string.contains("(")) {
            type = Material.getMaterial(string.split("\\(")[0].trim().split(slot +":")[1].trim().toUpperCase());
            String trim = string.split("\\(")[1].trim().split("\\)")[0].trim();
            if (trim.contains(",")) {
                String[] enchants = trim.split(",");
                for (String enchant : enchants) {
                    enchantmentIntegerMap.put(Enchantment.getByName(enchant.split(":")[0].trim().toUpperCase()), (Integer.valueOf(enchant.split(":")[1].trim()) ));
                }
            } else {
                enchantmentIntegerMap.put(Enchantment.getByName(trim.split(":")[0].trim().toUpperCase()), (Integer.valueOf(trim.split(":")[1].trim()) ));
            }
        } else {
            type = Material.getMaterial(string.split(":")[1].trim().toUpperCase());
        }
        this.itemStack = new ItemStack(type);
        if (!enchantmentIntegerMap.isEmpty()) {
            itemStack.addUnsafeEnchantments(enchantmentIntegerMap);
        }
    }

    public String getString() {
        return string;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

}
