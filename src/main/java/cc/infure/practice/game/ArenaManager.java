package cc.infure.practice.game;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Matthew E on 6/13/2017.
 */
public class ArenaManager implements Listener {
    private static ArenaManager instance;
    private Map<String, Arena> arenaMap;

    public static ArenaManager getInstance() {
        if (instance == null) {
            instance = new ArenaManager();
        }
        return instance;
    }

    public ArenaManager() {
        instance = this;
        this.arenaMap = new HashMap<>();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    public void setPosition(String name, int position, Location location) {
        Arena arena = getArena(name);
        if (arena != null) {
           switch (position) {
               case 1:
                   arena.setPositionOne(location);
                   break;
               case 2:
                   arena.setPositionTwo(location);
                   break;
               default:
                   break;
           }
       }
    }

    public Arena getArena(String name) {
        for (Arena arena : arenaMap.values()) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public void giveSpawnItems(Player player) {
        player.getInventory().setItem(1, getRankedSword());
        player.getInventory().setItem(2, getUnRankedSword());
    }

    private ItemStack getRankedSword() {
        Config config = InfurePractice.getInstance().getDuelsConfig();
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("rankedSwordName")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack getUnRankedSword() {
        Config config = InfurePractice.getInstance().getDuelsConfig();
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("unRankedSwordName")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void onEnable() {
        File file = new File(InfurePractice.getInstance().getDataFolder() + "/arenas/");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (file.listFiles() == null) {
            return;
        }
        for (File file1 : file.listFiles()) {
            String name = file1.getName().replace(".yml", "");
            Arena arena = new Arena(name);
            if (arena.exists()) {
                this.arenaMap.put(arena.getName(), arena);
            }

        }
    }

    public boolean createArena(String name, String author, Location position1, Location position2) {
        if (isArena(name)) {
            return false;
        }
        Arena arena = new Arena()
                .setName(name)
                .setBuilder(author)
                .setPositionOne(position1)
                .setPositionTwo(position2)
                .setSetUp(true);
        this.arenaMap.put(arena.getName(),arena);
        arena.save();
        return true;
    }

    private boolean isArena(String name) {
        for (Arena arena : arenaMap.values()) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void onDisable() {
        for (Arena arena : arenaMap.values()) {
            arena.save();
        }
    }

    public List<Arena> getArenaList() {
        return new ArrayList<>(arenaMap.values());
    }
}