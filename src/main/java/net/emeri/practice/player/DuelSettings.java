package net.emeri.practice.player;

import net.emeri.practice.EmeriPractice;
import net.emeri.practice.game.Arena;
import net.emeri.practice.game.ArenaManager;
import net.emeri.practice.game.GameType;
import net.emeri.practice.menu.MenuBuilder;
import net.emeri.practice.config.Config;
import net.emeri.practice.menu.Menu;
import net.emeri.practice.menu.item.MenuItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 6/15/2017.
 */
public class DuelSettings {
    private AbstractDuelPlayer sender;
    private AbstractDuelPlayer player;
    private GameType gameType;
    private Arena arena;

    public DuelSettings(AbstractDuelPlayer sender, AbstractDuelPlayer player) {
        this.sender = sender;
        this.player = player;
    }

    public void openMainMenu() {
        Menu menu = MenuBuilder.fastMenu("Duel " + player.getName(), 9, EmeriPractice.getInstance());
        int slot =1;
        for (GameType gameType : GameType.getUnRankedGames()) {
            Config duelsConfig = EmeriPractice.getInstance().getDuelsConfig();
            List<String> loreStringList = new ArrayList<>();
            for (String lore : duelsConfig.getStringList("duelMenus." + gameType.toString() + ".loreList")) {
                loreStringList.add(ChatColor.translateAlternateColorCodes('&', lore));
            }
            menu.setItem(slot, new MenuItemBuilder()
                    .type(getType(gameType))
                    .amount(1)
                    .data(0)
                    .lore(loreStringList)
                    .named(ChatColor.translateAlternateColorCodes('&', duelsConfig.getString("duelMenus." + gameType.toString() + ".name")))
                    .click((player1, clickType) -> {
                        player1.closeInventory();
                        this.gameType = gameType;
                       openDuelTypeMenu();
                        // sender.sendDuelRequest(player, this);
                    }));
            slot++;
        }
        menu.open(sender.getPlayer());
    }



    private void openDuelTypeMenu() {
        Menu menu = MenuBuilder.fastMenu("Duel " + player.getName(), 9, EmeriPractice.getInstance());
        int slot =1;
        for (Arena arena : ArenaManager.getInstance().getArenaList()) {
            menu.setItem(slot, new MenuItemBuilder()
                    .type(Material.EMPTY_MAP)
                    .amount(1)
                    .data(0)
                    .named(ChatColor.DARK_AQUA + arena.getName())
                    .click((player1, clickType) -> {
                        player1.closeInventory();
                        this.arena = arena;
                         sender.sendDuelRequest(player, this);
                    }));
            slot++;
        }
        menu.open(sender.getPlayer());
    }

    private Material getType(GameType gameType) {
        Config duelsConfig = EmeriPractice.getInstance().getDuelsConfig();
        try {
            Material material = Material.getMaterial(duelsConfig.getString("duelMenus." + gameType.toString() + ".type").toUpperCase());
            return material;
        } catch (Exception e)  {
            return Material.MUSHROOM_SOUP;
        }
    }

    public GameType getDuelType() {
        return gameType;
    }

    public Arena getArena() {
        return arena;
    }
}
