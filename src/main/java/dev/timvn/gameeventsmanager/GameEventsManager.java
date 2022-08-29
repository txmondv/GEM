package dev.timvn.gameeventsmanager;

import dev.timvn.gameeventsmanager.commands.CheckStateCommand;
import dev.timvn.gameeventsmanager.commands.EventStartCommand;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGCraftItemEvent;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGPlayerItemDropListener;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGPlayerMoveListener;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGBlockBreakListener;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import org.bukkit.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GameEventsManager extends JavaPlugin {


    // Plugin chat strings
    public static String PluginPrefix = "§8» §e§lGEM §8- §7";
    public static String noPermission = PluginPrefix + "§cYou do not have permission to perform this action.";


    // Manager security checks
    public static boolean managerBusy = false;
    public static String currentGame = "None";



    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();

        // initialize config
        getConfig().options().copyDefaults();
        getConfig();

        File worldFile = new File(getServer().getWorldContainer(), "RNGSurvival");
        if (!worldFile.exists()) {
            // create the world for RNGSurvival
            WorldCreator wc = new WorldCreator("RNGSurvival");

            wc.environment(World.Environment.NORMAL);
            wc.type(WorldType.NORMAL);
            wc.createWorld();

            Location newWorldWorldSpawn = Bukkit.getWorld("RNGSurvival").getSpawnLocation();
            getConfig().set("RNGSurvivalSpawn", newWorldWorldSpawn);
            saveConfig();

            getLogger().info("A new world has been created for RNGSurvival!");
        }

        getLogger().info("Successfully enabled the plugin.");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("Successfully disabled the plugin.");
    }




    public void registerCommands() {
        getCommand("event").setExecutor(new EventStartCommand());
        getCommand("check").setExecutor(new CheckStateCommand());
    }

    public void registerListeners() {
        // less code = better
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new RNGBlockBreakListener(), this);
        pm.registerEvents(new RNGPlayerMoveListener(), this);
        pm.registerEvents(new RNGPlayerMoveListener(), this);
        pm.registerEvents(new RNGPlayerItemDropListener(), this);
        pm.registerEvents(new RNGCraftItemEvent(), this);
    }

}
