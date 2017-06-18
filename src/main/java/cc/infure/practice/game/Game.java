package cc.infure.practice.game;

import cc.infure.practice.InfurePractice;
import cc.infure.practice.config.Config;
import cc.infure.practice.game.kits.Kit;
import cc.infure.practice.listeners.ArenaListeners;
import cc.infure.practice.player.AbstractDuelPlayer;
import cc.infure.practice.player.value.Value;
import cc.infure.practice.player.value.ValueType;
import cc.infure.practice.utilities.EloUtil;
import cc.infure.practice.utilities.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Matthew E on 6/13/2017.
 */
public class Game implements Listener {
    private String name;
    private GameType gameType;
    private boolean isRanked;
    private AbstractDuelPlayer player1;
    private AbstractDuelPlayer player2;
    private GameState gameState;
    private int countDownTaskId;
    private boolean player1Left;
    private boolean player2Left;
    private int countDownTime;
    private int tickTaskId;
    private Arena arena;

    public GameType getGameType() {
        return gameType;
    }

    public boolean isRanked() {
        return isRanked;
    }

    public AbstractDuelPlayer getPlayer1() {
        return player1;
    }

    public AbstractDuelPlayer getPlayer2() {
        return player2;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getCountDownTaskId() {
        return countDownTaskId;
    }

    public boolean isPlayer1Left() {
        return player1Left;
    }

    public boolean isPlayer2Left() {
        return player2Left;
    }

    public int getCountDownTime() {
        return countDownTime;
    }

    public int getTickTaskId() {
        return tickTaskId;
    }

    public int getPlayer1Hp() {
        return player1Hp;
    }

    public int getPlayer2Hp() {
        return player2Hp;
    }

    public Game(String name, GameType gameType, boolean isRanked, AbstractDuelPlayer player1, AbstractDuelPlayer player2, Arena arena) {
        this.name = name;
        this.gameType = gameType;
        this.isRanked = isRanked;
        this.player1 = player1;
        this.player2 = player2;
        this.gameState = GameState.BUILDING;
        this.arena = arena;
        build();
        Bukkit.getServer().getPluginManager().registerEvents(this, InfurePractice.getInstance());
        player1.setInGame(true);
        player2.setInGame(true);
        player1.cancelRequests();
        player2.cancelRequests();
    }

    public void sendMessage(String message) {
        if (player1 != null) {
            player1.sendMessage(message);
        }
        if (player2 != null) {
            player2.sendMessage(message);
        }
    }

    public void tick() {
        hidePlayers();

    }

    public void build() {
        hidePlayers();
        this.gameState = GameState.TELEPORTING;
        teleportPlayersToArena();

    }

    private void teleportPlayersToArena() {
        Location spawnPointTwo =arena.getPositionTwo();
        Location spawnPointOne =arena.getPositionOne();
        Player player2Player = player2.getPlayer();
        Player player1Player = player1.getPlayer();
        if (player1Player.isOnline() && (player2Player.isOnline())) {
            player1Player.teleport(spawnPointOne);
            player2Player.teleport(spawnPointTwo);
        }
        this.gameState = GameState.COUNT_DOWN;
        startCountDown();
    }

    private void startCountDown() {
        Config duelsConfig = InfurePractice.getInstance().getDuelsConfig();
        this.countDownTime = duelsConfig.getInteger("arenaCountDownTime");
        this.gameState = GameState.COUNT_DOWN;
        this.countDownTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(InfurePractice.getInstance(), () -> {
            if (this.countDownTime > 0) {
                sendMessage(duelsConfig.getString("messages.arenaCountDown").replace("{time}", countDownTime + ""));
                this.countDownTime--;
            } else {
                sendMessage(duelsConfig.getString("messages.duelStart"));
                startGame();
            }
        }, 20L, 20L);
    }

    private void startGame() {
        Bukkit.getScheduler().cancelTask(this.countDownTaskId);
        this.gameState = GameState.IN_GAME;
        this.tickTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(InfurePractice.getInstance(), this::tick, 20L, 20L);
        //giveKit(gameType);
        for (Player player : getPlayers()) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
            player.setMaxHealth(20);
            player.setHealth(player.getMaxHealth());
            player.setLevel(0);
            player.setExp(0);
            player.setFoodLevel(20);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setGameMode(GameMode.SURVIVAL);
            Kit kit = new Kit(gameType);
            if (ArenaListeners.getInstance().isCombo(gameType)) {
                player.setMaximumNoDamageTicks(3);
            } else {
                player.setMaximumNoDamageTicks(10);

            }
            kit.give(player);
        }
    }


    private int player1Hp;
    private int player2Hp;

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (gameState == GameState.IN_GAME) {
                if (player1.getUniqueId().equals(player.getUniqueId())) {
                    player1Hp = (int) player.getHealth();
                }
                if (player2.getUniqueId().equals(player.getUniqueId())) {
                    player2Hp = (int) player.getHealth();
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        player.spigot().respawn();
        event.getDrops().clear();
        if (gameState == GameState.IN_GAME) {
            if (player.getUniqueId().equals(player1.getUniqueId())) {
                player1Left = true;
                this.gameState = GameState.ENDED;
                player1.setDeaths(gameType, new Value<>(1, ValueType.ADD));
                player2.setKills(gameType, new Value<>(1, ValueType.ADD));
                endGame();
                return;
            } else if (player.getUniqueId().equals(player2.getUniqueId())) {
                player2Left = true;
                this.gameState = GameState.ENDED;
                player2.setDeaths(gameType, new Value<>(1, ValueType.ADD));
                player1.setKills(gameType, new Value<>(1, ValueType.ADD));
                endGame();
                return;
            }
        }
    }

    public void hidePlayers() {
        Player player2Player = player2.getPlayer();
        Player player1Player = player1.getPlayer();
        if (player1Player.isOnline() && (player2Player.isOnline())) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if ((player.getUniqueId().equals(player1.getUniqueId())) || (player.getUniqueId().equals(player2.getUniqueId()))) {
                    continue;
                }
                player2Player.hidePlayer(player);
                player1Player.hidePlayer(player);
            }
        }
    }

    public void showPlayers() {
        Player player2Player = player2.getPlayer();
        Player player1Player = player1.getPlayer();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if ((player.getUniqueId().equals(player1.getUniqueId())) || (player.getUniqueId().equals(player2.getUniqueId()))) {
                continue;
            }
            if (player1Player.isOnline()) {
                player1Player.showPlayer(player);
            }
            if (player2Player.isOnline()) {
                player2Player.showPlayer(player);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (getPlayers().contains(event.getPlayer())) {
            leave(event.getPlayer());
        }
    }

    public void leave(Player player) {
        if (gameState == GameState.COUNT_DOWN) {
            Bukkit.getScheduler().cancelTask(countDownTaskId);
        }

        Config duelsConfig = InfurePractice.getInstance().getDuelsConfig();
        if (player.getUniqueId().equals(player1.getUniqueId())) {
            player1Left = true;
            player1.setInGame(false);
            player1.setDeaths(gameType, new Value<>(1, ValueType.ADD));
            player2.setKills(gameType, new Value<>(1, ValueType.ADD));
        }
        if (player.getUniqueId().equals(player2.getUniqueId())) {
            player2Left = true;
            player2.setInGame(false);
            player2.setDeaths(gameType, new Value<>(1, ValueType.ADD));
            player1.setKills(gameType, new Value<>(1, ValueType.ADD));
        }
        sendMessage(duelsConfig.getString("messages.playerLeaveGame").replace("{player}", player.getName()));
        endGame();
    }

    private void endGame() {
        this.gameState = GameState.ENDED;
        Bukkit.getScheduler().cancelTask(this.tickTaskId);
        teleportPlayersToSpawn();
        showPlayers();
        handleWinner();
    }

    private void teleportPlayersToSpawn() {
        Location spawn = LocationUtil.getPlayerLocationFromString(InfurePractice.getInstance().getDuelsConfig().getString("locations.spawnPoint"));
        Player player2Player = player2.getPlayer();
        Player player1Player = player1.getPlayer();
        if (spawn != null) {

            if (player1Player.isOnline()) {
                player1Player.getInventory().clear();
                player1Player.getInventory().setArmorContents(null);
                player1Player.setFoodLevel(20);
                player1Player.getActivePotionEffects().clear();
                player1Player.teleport(spawn);
                ArenaManager.getInstance().giveSpawnItems(player1Player);
            }
            if (player2Player.isOnline()) {
                player2Player.teleport(spawn);
                player2Player.getInventory().clear();
                player2Player.getInventory().setArmorContents(null);
                player2Player.setFoodLevel(20);
                player2Player.getActivePotionEffects().clear();
                ArenaManager.getInstance().giveSpawnItems(player2Player);
            }
        }
    }

    private int getAverageElo(AbstractDuelPlayer duelPlayer, GameType gameType) {
        return duelPlayer.getElo(gameType);
    }

    private void handleWinner() {
        Config duelsConfig = InfurePractice.getInstance().getDuelsConfig();
        int eloSwitch = 0;
        AbstractDuelPlayer winner = null;
        if (player1Left) {
            int wonElo = getAverageElo(player2, gameType);
            int lostElo = getAverageElo(player1, gameType);
            int newElo = newElo = EloUtil.newRating(player2.getElo(gameType), lostElo, 1);
            int switchedElo = (newElo - player2.getElo(gameType));
            if (isRanked) {
                //player1.setElo(gameType, new Value<>(EloUtil.newRating(player1.getElo(gameType), wonElo, 0), ValueType.SET));
                player2.setElo(gameType, new Value<>(switchedElo, ValueType.ADD));
                player1.setElo(gameType, new Value<>(switchedElo, ValueType.TAKE));
            }
            eloSwitch = switchedElo;
            player1.setInGame(false);
            winner = player2;
            player2.setInGame(false);
        } else if (player2Left) {
            int wonElo = getAverageElo(player1, gameType);
            int lostElo = getAverageElo(player2, gameType);
            int newElo = newElo = EloUtil.newRating(player1.getElo(gameType), lostElo, 1);
            int switchedElo = (newElo - player1.getElo(gameType));
            if (isRanked) {
                player1.setElo(gameType, new Value<>(switchedElo, ValueType.ADD));
                player2.setElo(gameType, new Value<>(switchedElo, ValueType.TAKE));
            }
            player1.setInGame(false);
            winner = player1;
            player2.setInGame(false);
            eloSwitch = switchedElo;
        }
        if (winner != null) {
            List<String> winMessageStringList;
            if (isRanked) {
                winMessageStringList = duelsConfig.getStringList("messages.rankedDuelWinnerMessages");
                winner.setRankedWins(new Value<>(1, ValueType.ADD));
            } else {
                winMessageStringList = duelsConfig.getStringList("messages.unRankedDuelWinnerMessages");
                winner.setUnRankedWins(new Value<>(1, ValueType.ADD));
            }
            String winnerName = "";
            String loserName = "";
            double loserHealth = 0;
            double winnerHealth = 0;
            if (player2Left) {
                winnerName = player1.getName();
                loserName = player2.getName();
                winnerHealth = player1.getPlayer().getHealth();
            }
            if (player1Left) {
                winnerName = player2.getName();
                loserName = player1.getName();
                winnerHealth = player2.getPlayer().getHealth();
            }
            for (String line : winMessageStringList) {
                line = ChatColor
                        .translateAlternateColorCodes('&', line)
                        .replace("{winner}", winnerName)
                        .replace("{loser}", loserName)
                        .replace("{elo_loss}", ChatColor.RED + "-" + eloSwitch)
                        .replace("{loser_health}", (int) loserHealth + "")
                        .replace("{winner_health}", (int) winnerHealth + "")
                        .replace("{elo_gain}", ChatColor.GREEN + "+" + eloSwitch);
                sendMessage(line);

            }
            GameManager.getInstance().removeGame(this);
            try {
                finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return Arrays.asList(player1.getPlayer(), player2.getPlayer());
    }

    public Arena getArena() {
        return arena;
    }

    public Game setArena(Arena arena) {
        this.arena = arena;
        return this;
    }
}
