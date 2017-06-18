package cc.infure.practice;

import cc.infure.practice.commands.*;
import cc.infure.practice.config.Config;
import cc.infure.practice.game.ArenaManager;
import cc.infure.practice.listeners.ArenaListeners;
import cc.infure.practice.listeners.PlayerListeners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class InfurePractice extends JavaPlugin {
    private static InfurePractice instance;
    private Config duelsConfig;


    public static InfurePractice getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.duelsConfig = new Config("config");
        this.registerCommands();
        this.registerListeners();
        ArenaManager.getInstance().onEnable();
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ArenaListeners(), this);
        pluginManager.registerEvents(new PlayerListeners(), this);
    }

    private void registerCommands() {
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand());
        this.getCommand("setuparena").setExecutor(new SetUpArenaCommand());
        this.getCommand("duelsreload").setExecutor(new DuelsReloadCommand());
        this.getCommand("confirmcreation").setExecutor(new ConfirmCreationCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("duel").setExecutor(new DuelCommand());
        this.getCommand("setarenaspawnpoint").setExecutor(new SetArenaSpawnPointCommand());
        this.getCommand("rankedmenu").setExecutor(new RankedMenuCommand());
        this.getCommand("unrankedmenu").setExecutor(new UnRankedMenuCommand());
        this.getCommand("dev").setExecutor(new DevCommand());
        this.getCommand("elotop").setExecutor(new EloTopCommand());
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', duelsConfig.getString("messages.kickMessage")));
        }
        ArenaManager.getInstance().onDisable();
    }

    public Config getDuelsConfig() {
        return duelsConfig;
    }
}
