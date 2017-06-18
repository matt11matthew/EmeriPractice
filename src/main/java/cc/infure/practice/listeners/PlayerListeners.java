package cc.infure.practice.listeners;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.config.Config;
import cc.infure.practice.game.*;
import cc.infure.practice.menu.Menu;
import cc.infure.practice.menu.MenuBuilder;
import cc.infure.practice.menu.item.MenuItemBuilder;
import cc.infure.practice.player.AbstractDuelPlayer;
import cc.infure.practice.player.DuelPlayerManager;
import cc.infure.practice.utilities.LocationUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class PlayerListeners implements Listener {

    private static PlayerListeners instance;

    public static PlayerListeners getInstance() {
        if (instance == null) {
            instance = new PlayerListeners();
        }
        return instance;
    }


    public PlayerListeners() {
        instance = this;
        for (GameType gameType : GameType.values()) {
            gameTypeListMap.put(gameType, new ArrayList<>());
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(InfurePractice.getInstance(), () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isInQueue(player) || (isInGame(player))) {
                    continue;
                }
                if ((player.getInventory().getItem(1) == null)) {
                    player.getInventory().setItem(1, getRankedSword());
                }
                if ((player.getInventory().getItem(2) == null)) {
                    player.getInventory().setItem(2, getUnRankedSword());
                }
                scoreBoard(player);
            }
        }, 5L, 5L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(InfurePractice.getInstance(), () -> {
            for (GameType gameStyle : gameTypeListMap.keySet()) {
                List<AbstractDuelPlayer> gameStyleQueue = gameTypeListMap.get(gameStyle);
                AbstractDuelPlayer looking = null;
                AbstractDuelPlayer found = null;
                AbstractDuelPlayer pl = null;
                for (AbstractDuelPlayer player : gameStyleQueue) {
                    pl = player;
                    for (AbstractDuelPlayer possible : gameStyleQueue) {
                        if (possible != player) {
                            if (isRanked(gameStyle)) {
                               if (inRange(pl.getElo(gameStyle), possible.getElo(gameStyle), 100)) {
                                   looking = player;
                                   found = possible;
                                   break;
                               }
                            } else {
                                looking = player;
                                found = possible;
                                break;
                            }
                        }
                    }
                    if (looking != null) {
                        break;
                    }
                }
                if (looking != null) {
                    removeFromQueue(pl, gameStyle);
                    removeFromQueue(found, gameStyle);
                    pl.getPlayer().getInventory().clear();
                    found.getPlayer().getInventory().clear();
                    GameManager.getInstance().setupGame(new Game(UUID.randomUUID().toString(), gameStyle, isRanked(gameStyle), pl, found, getRandomArena()));
                }
            }
        }, 5L, 5L);
    }

    private Arena getRandomArena() {
        List<Arena> arenaList = ArenaManager.getInstance().getArenaList();
        if (arenaList.size() == 1) {
            return arenaList.get(0);
        }
        Random random = new Random();
        Arena arena = arenaList.get(random.nextInt(arenaList.size()));
        if (arena != null) {
            return arena;
        }
        return null;
    }

    public boolean isInGame(Player player) {
        for (Game game : GameManager.getInstance().getGameMap().values()) {
            if (game.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean inRange(int lookingElo, int possibleElo, int range) {
        if (range > 400) {
            range = 400;
        }
        return (possibleElo > (lookingElo - range)) && (possibleElo < (lookingElo + range));

    }
    private boolean isRanked(GameType gameStyle) {
        return Arrays.asList(GameType.getRankedGames()).contains(gameStyle);
    }

    private void removeFromQueue(AbstractDuelPlayer duelPlayer, GameType gameType) {
        List<AbstractDuelPlayer> abstractDuelPlayers = gameTypeListMap.get(gameType);
        if (abstractDuelPlayers.contains(duelPlayer)) {
            abstractDuelPlayers.remove(duelPlayer);
        }
        gameTypeListMap.remove(gameType);
        gameTypeListMap.put(gameType, abstractDuelPlayers);
        duelPlayer.getPlayer().getInventory().clear();
        duelPlayer.getPlayer().getInventory().setItem(1, getRankedSword());
        duelPlayer.getPlayer().getInventory().setItem(2, getUnRankedSword());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.PLAYER) {
            if (inventory.getTitle().contains("Queue")) {
                 event.getPlayer().getInventory().setItem(1, getRankedSword());
                 event.getPlayer().getInventory().setItem(2, getUnRankedSword());
            }
        }
    }

    private Map<GameType, List<AbstractDuelPlayer>> gameTypeListMap = new ConcurrentHashMap<>();

    public void joinQueue(GameType gameType, AbstractDuelPlayer duelPlayer) {
        Config config = InfurePractice.getInstance().getDuelsConfig();
        List<AbstractDuelPlayer> abstractDuelPlayers = gameTypeListMap.get(gameType);
        if (abstractDuelPlayers.contains(duelPlayer)) {
            abstractDuelPlayers.remove(duelPlayer);
        }
        abstractDuelPlayers.add(duelPlayer);
        gameTypeListMap.remove(gameType);
        gameTypeListMap.put(gameType, abstractDuelPlayers);
        duelPlayer.getPlayer().getInventory().setItem(4, getLeaveRedStone());
        duelPlayer.sendMessage(config.getString("messages.joinedQueue").replace("{game}", gameType.toString().replaceAll("_", " ")));

    }

    public void scoreBoard(Player player) {
        Config config = InfurePractice.getInstance().getDuelsConfig();
        ScoreboardWrapper scoreboardWrapper = new ScoreboardWrapper(ChatColor.translateAlternateColorCodes('&', config.getString("spawnScoreboardTitle")));
        for (String s : config.getStringList("spawnScoreboardLines")) {
            scoreboardWrapper.addLine(ChatColor.translateAlternateColorCodes('&', s).replace("{ign}", player.getName()).replace("{global_elo}", getGlobalElo(player) + ""));
        }
        player.setScoreboard(scoreboardWrapper.getScoreboard());
    }

    private int getGlobalElo(Player player) {
        AbstractDuelPlayer duelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(player.getUniqueId());
        int total = 0;
        for (GameType gameType : GameType.values()) {
            total += duelPlayer.getElo(gameType);
        }
        if (total > 0) {
            return total / GameType.values().length;
        } else {
            return InfurePractice.getInstance().getDuelsConfig().getInteger("defaultElo");
        }
    }

    /*\
    spawnScoreboardTitle: "&b&lInfure Practice"
spawnScoreboardLines:
- " "
- "&bIGN: &f{ign}"
- "&bElo: &f{global_elo}"
- " "
- "&binfure.cc"
     */
    public void leaveQueue(GameType gameType, AbstractDuelPlayer duelPlayer) {
        Config config = InfurePractice.getInstance().getDuelsConfig();
        List<AbstractDuelPlayer> abstractDuelPlayers = gameTypeListMap.get(gameType);
        if (abstractDuelPlayers.contains(duelPlayer)) {
            abstractDuelPlayers.remove(duelPlayer);
        }
        gameTypeListMap.remove(gameType);
        gameTypeListMap.put(gameType, abstractDuelPlayers);
        duelPlayer.getPlayer().getInventory().clear();
        duelPlayer.getPlayer().getInventory().setItem(1, getRankedSword());
        duelPlayer.getPlayer().getInventory().setItem(2, getUnRankedSword());
        duelPlayer.sendMessage(config.getString("messages.leftQueue").replace("{game}", gameType.toString().replaceAll("_", " ")));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);
        Config config = InfurePractice.getInstance().getDuelsConfig();
        if (config.isSet("joinMessageList")) {
            for (String messageString : config.getStringList("joinMessageList")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', messageString.replaceAll("\\{player}", player.getName())));
            }
        }


        DuelPlayerManager playerManager = DuelPlayerManager.getInstance();
        if (!playerManager.isPlayer(player.getUniqueId())) {
            AbstractDuelPlayer abstractDuelPlayer = playerManager.create(player.getUniqueId(), player.getName());
            //new player
        } else {
            AbstractDuelPlayer duelPlayer = playerManager.getOnlineDuelPlayer(player.getUniqueId());
            if (duelPlayer != null) {
                // not new player
            }
        }
        if (config.isSet("locations.spawnPoint")) {
            Location location = LocationUtil.getPlayerLocationFromString(config.getString("locations.spawnPoint"));
            player.teleport(location);
        }
        player.getInventory().setItem(1, getRankedSword());
        player.getInventory().setItem(2, getUnRankedSword());
    }



    public void sendDeveloper(Player player) {
        player.sendMessage(" ");
        player.sendMessage(ChatColor.DARK_AQUA + "Author: " + ChatColor.AQUA + "Matthew E (matt11matthew)");

        TextComponent component6 = new TextComponent(ChatColor.DARK_AQUA +"Commission developers ");
        net.md_5.bungee.api.chat.TextComponent component = new TextComponent("[SpigotMc Click]");
        net.md_5.bungee.api.chat.TextComponent component1 = new TextComponent();
        component.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        component.setBold(true);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/threads/matthew-e-development-fast-experienced-professional.131251/"));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.GOLD + "SpigotMC Shop")}));
        component1.addExtra(" ");
        component1.addExtra(component);

        net.md_5.bungee.api.chat.TextComponent component2 = new TextComponent("[McMarket Click]");
        net.md_5.bungee.api.chat.TextComponent component3 = new TextComponent();
        component2.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        component2.setBold(true);
        component2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.mc-market.org/threads/109004/"));
        component2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.AQUA + "McMarket Shop")}));
        component3.addExtra(" ");
        component3.addExtra(component2);
        component6.addExtra(component1);
        component6.addExtra(" ");
        component6.addExtra(component3 );
        player.spigot().sendMessage(component6);
        player.sendMessage(" ");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setItem(1, getRankedSword());
        player.getInventory().setItem(2, getUnRankedSword());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Config config = InfurePractice.getInstance().getDuelsConfig();
        ItemStack itemStack = player.getItemInHand();
        AbstractDuelPlayer duelPlayer = DuelPlayerManager.getInstance().getDuelPlayer(player.getUniqueId());
        if ((itemStack != null) && (itemStack.getType() != Material.AIR)) {
            if (itemStack.getType() == Material.MUSHROOM_SOUP) {
                event.setCancelled(true);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.DENY);
                player.setItemInHand(new ItemStack(Material.BOWL));
                double health = (player.getHealth() + config.getDouble("soupHealAmount"));
                if (health >= player.getMaxHealth()) {
                    health = player.getMaxHealth();
                }
                player.setHealth(health);
                if (player.getFoodLevel() == 20) {
                    player.setFoodLevel(19);
                }
                return;
            } else if (itemStack.equals(getLeaveRedStone())) {
                player.closeInventory();
                if (isInQueue(player)) {
                    leaveQueue(getQueueType(player), duelPlayer);
                }
                return;
            } else if (itemStack.equals(getRankedSword())) {
                player.closeInventory();
                if (!isInQueue(player)) {
                    if (duelPlayer.getUnRankedWins() < 10) {
                        duelPlayer.sendMessage(config.getString("messages.notEnoughWins"));
                        return;
                    }
                    player.getInventory().clear();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rankedmenu " + player.getName());
                    return;
                }
            } else if (itemStack.equals(getUnRankedSword())) {
                player.closeInventory();
                if (!isInQueue(player)) {
                    player.getInventory().clear();
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unrankedmenu " + player.getName());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if ((item != null) && (item.getType().toString().contains("_SWORD"))) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
        }
    }

    private boolean isInQueue(Player player) {
        for (GameType gameType : gameTypeListMap.keySet()) {
            for (AbstractDuelPlayer abstractDuelPlayer : gameTypeListMap.get(gameType)) {
                if (abstractDuelPlayer.getUniqueId().equals(player.getUniqueId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public GameType getQueueType(Player player) {
        for (GameType gameType : gameTypeListMap.keySet()) {
            for (AbstractDuelPlayer abstractDuelPlayer : gameTypeListMap.get(gameType)) {
                if (abstractDuelPlayer.getUniqueId().equals(player.getUniqueId())) {
                    return gameType;
                }
            }
        }
        return null;
    }

    public void openUnRankedQueueMenu(Player player) {
        Menu menu = MenuBuilder.fastMenu("Unranked Queue", 9, InfurePractice.getInstance());
        Config config = InfurePractice.getInstance().getDuelsConfig();
        int slot = 1;
        for (GameType gameType : GameType.getUnRankedGames()) {
            List<String> loreStringList = new ArrayList<>();
            for (String s : config.getStringList("queueItemLore")) {
                loreStringList.add(ChatColor.translateAlternateColorCodes('&', s.replace("{in_queue_amount}", gameTypeListMap.get(gameType).size() + "").replace("{in_game_amount}", getInGameAmount(gameType) + "")));
            }
            menu.setItem(slot, new MenuItemBuilder()
                    .type(Material.getMaterial(config.getString("duelMenus." + gameType.toString() + ".type").toUpperCase()))
                    .amount(1)
                    .lore(loreStringList)
                    .data(0)
                    .click((player1, clickType) -> {
                        player1.closeInventory();
                        if (!isInQueue(player1)) {
                            player1.getInventory().clear();
                            joinQueue(gameType, DuelPlayerManager.getInstance().getDuelPlayer(player1.getUniqueId()));
                        }
                    })
                    .named(ChatColor.translateAlternateColorCodes('&', config.getString("queueItemName").replace("{ladder}", gameType.toString().replaceAll("_", " ")))));
            slot++;
        }
        menu.open(player);
    }

    public  void openRankedQueueMenu(Player player) {
        Menu menu = MenuBuilder.fastMenu("Ranked Queue", 9, InfurePractice.getInstance());
        Config config = InfurePractice.getInstance().getDuelsConfig();
        int slot = 1;
        for (GameType gameType : GameType.getRankedGames()) {
            List<String> loreStringList = new ArrayList<>();
            for (String s : config.getStringList("queueItemLore")) {
                loreStringList.add(ChatColor.translateAlternateColorCodes('&', s.replace("{in_queue_amount}", gameTypeListMap.get(gameType).size() + "").replace("{in_game_amount}", getInGameAmount(gameType) + "")));
            }
            menu.setItem(slot, new MenuItemBuilder()
            .type(Material.getMaterial(config.getString("duelMenus." + gameType.toString() + ".type").toUpperCase()))
                    .amount(1)
                    .lore(loreStringList)
                    .data(0)
                    .click((player1, clickType) -> {
                        player1.closeInventory();
                        if (!isInQueue(player1)) {
                            player1.getInventory().clear();
                            joinQueue(gameType, DuelPlayerManager.getInstance().getDuelPlayer(player1.getUniqueId()));
                        }
                    })
            .named(ChatColor.translateAlternateColorCodes('&', config.getString("queueItemName").replace("{ladder}", gameType.toString().replaceAll("_", " ")))));
            slot++;
        }
        menu.open(player);
    }

    private int getInGameAmount(GameType gameType) {
        int inGame = 0;
        for (Game game : GameManager.getInstance().getGameMap().values()) {
            if (game.getGameType() == gameType) {
                inGame += game.getPlayers().size();
            }
        }
        return inGame;
    }

    /*
    queueItemName: "&a&l{ladder} &7(Click)"
queueItemLore:
- "&eIn Queue: &a{in_queue_amount}"
- "&eIn Game: &a{in_game_amount}"
     */

    private ItemStack getRankedSword() {
        Config config = InfurePractice.getInstance().getDuelsConfig();
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("rankedSwordName")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private ItemStack getLeaveRedStone() {
        Config config = InfurePractice.getInstance().getDuelsConfig();
        ItemStack itemStack = new ItemStack(Material.REDSTONE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("leaveQueueItemName")));
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        event.setQuitMessage(null);
        DuelPlayerManager playerManager = DuelPlayerManager.getInstance();
        AbstractDuelPlayer duelPlayer = playerManager.getOnlineDuelPlayer(player.getUniqueId());
        if (duelPlayer != null) {
            duelPlayer.setInGame(false);
            duelPlayer.cancelRequests();
            duelPlayer.save();
        }
        if (isInQueue(player)) {
            removeFromQueue(duelPlayer, getQueueType(player));
        }
    }

    public Game getGame(Player player) {
        for (Game game : GameManager.getInstance().getGameMap().values()) {
            if (game.getPlayers().contains(player)) {
                return game;
            }
        }
        return null;
    }
}
