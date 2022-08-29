package dev.timvn.gameeventsmanager;

import dev.timvn.gameeventsmanager.commands.CheckStateCommand;
import dev.timvn.gameeventsmanager.commands.EventStartCommand;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGCraftItemEvent;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGPlayerItemDropListener;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGPlayerMoveListener;
import dev.timvn.gameeventsmanager.games.rngsurvival.listeners.RNGBlockBreakListener;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GameEventsManager extends JavaPlugin {

    // Plugin chat strings
    public static String PluginPrefix = "§8» §e§lGEM §8- §7";
    public static String noPermission = PluginPrefix + "§cYou do not have permission to perform this action.";

    // Manager security checks
    public static boolean managerBusy = false;
    public static String currentGame = "None";


    // initialize RNGGameManager
    private RNGGameManager RNGGameManager;

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        File dir = Bukkit.getWorld("RNGSurvival").getWorldFolder();
        if(dir.isDirectory()) {
            Bukkit.unloadWorld("RNGSurvival", true);
            del(dir);
        }
    }




    public void registerCommands() {
        getCommand("event").setExecutor(new EventStartCommand());
        getCommand("check").setExecutor(new CheckStateCommand());
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new RNGBlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new RNGPlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new RNGPlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new RNGPlayerItemDropListener(), this);
        getServer().getPluginManager().registerEvents(new RNGCraftItemEvent(), this);
    }

    public static void del(File file) {
        for (File f : file.listFiles()) {
            if (f.isDirectory())
                del(f);
            else
                f.delete();
        }
        file.delete();
    };
}
