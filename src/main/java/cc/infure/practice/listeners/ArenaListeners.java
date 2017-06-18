package cc.infure.practice.listeners;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.game.Game;
import cc.infure.practice.game.GameManager;
import cc.infure.practice.game.GameType;
import cc.infure.practice.utilities.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew E on 6/12/2017.
 */
public class ArenaListeners implements Listener {
    private Location position1;
    private Location position2;
    private static ArenaListeners instance;

    public static ArenaListeners getInstance() {
        if (instance == null) {
            instance = new ArenaListeners();
        }
        return instance;
    }

    public ArenaListeners() {
        instance = this;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if ((block != null) && (block.getType() != Material.AIR)) {
            ItemStack itemStack = player.getItemInHand();
            if ((itemStack != null) && (itemStack.getType() == Material.STICK) && (itemStack.hasItemMeta()) && (itemStack.getItemMeta().hasDisplayName()) && (itemStack.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Set positions " + ChatColor.GRAY + "(Right-Click for pos1 | Left-Click for pos2)"))) {
                if (player.hasPermission(InfurePractice.getInstance().getConfig().getString("permissions.setupArena"))) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        this.position2 = block.getLocation();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', InfurePractice.getInstance().getConfig().getString("messages.setPosition").replace("{number}", "2").replace("{location}", LocationUtil.getStringFromBlockLocation(block.getLocation()))));
                        return;
                    }
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        this.position1 = block.getLocation();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', InfurePractice.getInstance().getConfig().getString("messages.setPosition").replace("{number}", "1").replace("{location}", LocationUtil.getStringFromBlockLocation(block.getLocation()))));
                        return;
                    }
                }
            }
        }
    }
/*
options:
  RANKED_DEBUFF_POTION:
    build: false
    fastHittingSpeed: false
  RANKED_NO_DEBUFF_POTION:
    build: false
    fastHittingSpeed: false
  RANKED_UHC_BUILD:
    build: true
    fastHittingSpeed: false
  RANKED_MCSG:
    build: false
    fastHittingSpeed: false
  RANKED_VANILLA:
    build: false
    fastHittingSpeed: false
  UNRANKED_DEBUFF_POTION:
    build: false
    fastHittingSpeed: false
  UNRANKED_NO_DEBUFF_POTION:
    build: false
    fastHittingSpeed: false
  UNRANKED_UHC_BUILD:
    build: true
    fastHittingSpeed: false
  UNRANKED_MCSG:
    build: false
    fastHittingSpeed: false
  UNRANKED_VANILLA:
    build: false
    fastHittingSpeed: false
 */
    public boolean canBuild(GameType gameType) {
        return  InfurePractice.getInstance().getDuelsConfig().getBoolean("options." + gameType.toString() + ".build");
    }
    public boolean isCombo(GameType gameType) {
        return  InfurePractice.getInstance().getDuelsConfig().getBoolean("options." + gameType.toString() + ".fastHittingSpeed");
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp()) {
            if (PlayerListeners.getInstance().isInGame(event.getPlayer())) {
                Game game = PlayerListeners.getInstance().getGame(event.getPlayer());
                if (canBuild(game.getGameType())) {
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp()) {
            if (PlayerListeners.getInstance().isInGame(event.getPlayer())) {
                Game game = PlayerListeners.getInstance().getGame(event.getPlayer());
                if (canBuild(game.getGameType())) {
                    return;
                }
            }
            event.setCancelled(true);
        }
    }
//
//    @EventHandler
//    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
//        if ((event.getEntity() instanceof Player) && (event.getDamager() instanceof Player)) {
//            PlayerListeners instance = PlayerListeners.getInstance();
//            Player player = (Player) event.getEntity();
//            Player attacker = (Player) event.getDamager();
//            if ((instance.isInGame(player)) && (instance.isInGame(attacker)) && (instance.getGame(player).getName().equalsIgnoreCase(instance.getGame(attacker).getName()))) {
//                Game game = instance.getGame(attacker);
//                if (isCombo(game.getGameType())) {
//                    event.setDamage(EntityDamageEvent.DamageModifier.BASE, 0);
//                    double health = player.getHealth() - event.getFinalDamage();
//                    if (health <= 0.0D) {
//                        health = 0.0D;
//                    }
//                    event.setDamage(0);
//                    player.setHealth(health);
//                    player.setLastDamageCause(event);
//                }
//            }
//        }
//    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player)) {
            Player attacker = (Player) event.getDamager();
            Player player = (Player) event.getEntity();
            if (isInGame(attacker, player)) {
                return;
            }
            event.setCancelled(true);
        } else if ((event.getDamager() instanceof Arrow) && (event.getEntity() instanceof Player)) {
            Arrow arrow = (Arrow) event.getDamager();
            if ((arrow != null) && (arrow.getShooter() != null) && (arrow.getShooter() instanceof Player)) {
                Player attacker = (Player) arrow.getShooter();
                Player player = (Player) event.getEntity();
                if (isInGame(attacker, player)) {
                    return;
                }
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    private boolean isInGame(Player attacker, Player player) {
        for (Game game : GameManager.getInstance().getGameMap().values()) {
            if ((game.getPlayer1().getUniqueId().equals(attacker.getUniqueId())) && (game.getPlayer2().getUniqueId().equals(player.getUniqueId()))) {
                return true;
            }
            if ((game.getPlayer2().getUniqueId().equals(attacker.getUniqueId())) && (game.getPlayer1().getUniqueId().equals(player.getUniqueId()))) {

                return true;
            }
        }
        return false;
    }

    public ArenaListeners setPosition1(Location position1) {
        this.position1 = position1;
        return this;
    }

    public ArenaListeners setPosition2(Location position2) {
        this.position2 = position2;
        return this;
    }




    public Location getPosition1() {
        return position1;
    }

    public Location getPosition2() {
        return position2;
    }
}