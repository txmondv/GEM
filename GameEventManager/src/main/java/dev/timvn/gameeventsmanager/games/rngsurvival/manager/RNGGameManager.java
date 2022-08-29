package dev.timvn.gameeventsmanager.games.rngsurvival.manager;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RNGGameManager {

    public static boolean allowBlockBreak = false;
    private final GameEventsManager plugin;
    public RNGGameStates RNGGameStates = dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates.INACTIVE;
    public static LocalDateTime lastItemDropTime;


    private BlockManager blockManager;
    private CraftManager craftManager;

    public RNGGameManager(GameEventsManager plugin) {
        this.plugin = plugin;

        this.blockManager = new BlockManager(this);
        this.craftManager = new CraftManager(this);
    }


    public void resetMap(String worldName){
        World world = Bukkit.getWorld(worldName);
        assert world != null;
        world.setKeepSpawnInMemory(false);
        Bukkit.unloadWorld(world, true);

        File dir = world.getWorldFolder();

        GameEventsManager.del(dir);
    }


    // get GameState
    public static String currentGameState = "INACTIVE";
    public static String getGameState() {
        return currentGameState;
    }


    public void setGameState(RNGGameStates RNGGameStates) {
        this.RNGGameStates = RNGGameStates;
        switch(RNGGameStates) {
            case STARTING:
                currentGameState = "STARTING";
                // Countdown Start!
                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final Runnable runnable = new Runnable() {
                    int countdownStarter = 20;

                    public void run() {

                        countdownStarter--;

                        // Default case: The countdown is still going (20-1); Display the remaining time
                        if(countdownStarter > 0) {
                            for(Player p : Bukkit.getOnlinePlayers()){
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7The game is starting in §e" + countdownStarter + " §7seconds."));
                            }
                        }

                        // Special case: The countdown is exactly at 1; display "starting in 1 second" :)
                        if (countdownStarter == 0) {
                            for(Player p : Bukkit.getOnlinePlayers()){
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7The game is starting in §e" + countdownStarter + " §7second."));
                            }
                        }

                        // Special case: The countdown is exactly at 0; Don't display "starting in 0 seconds" :)
                        if (countdownStarter == 0) {
                            for(Player p : Bukkit.getOnlinePlayers()){
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Starting..."));
                            }
                        }

                        // Special case: The countdown is lower than zero: Check if the world has been loaded (gameState changes synchronized with wc); if so, display success, if not, ask for users to wait; shutdown the scheduler
                        if (countdownStarter < 0) {
                            if(currentGameState == "STARTING") {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cPlease wait for the server to finish loading your destination world!"));
                                }
                            } else {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aThe game has started!"));
                                }
                            }

                            scheduler.shutdown();
                        }
                    }
                };
                scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
                // Countdown End!


                // A new world is being created (hope it is finished in 20s ^^) | Background task!

                WorldCreator wc = new WorldCreator("RNGSurvival");

                wc.environment(World.Environment.NORMAL);
                wc.type(WorldType.NORMAL);
                wc.createWorld();

                synchronized(wc) {
                    // Players are sent to the world
                    World SessionWorld = plugin.getServer().getWorld("RNGSurvival");
                    Location coords = SessionWorld.getSpawnLocation();
                    for(Player p : Bukkit.getOnlinePlayers()){
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Initializing..."));
                        p.teleport(coords);
                    }

                    // GameState can now change, because the world has been loaded and players have been teleported
                    setGameState(RNGGameStates.ACTIVE);
                }

                break;
            case ACTIVE:
                allowBlockBreak = true;
                currentGameState = "ACTIVE";
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "The game has started. [GAMESTATE]");
                break;
            case PREDEATHMATCH:
                allowBlockBreak = true;
                currentGameState = "PREDEATHMATCH";
                Bukkit.broadcastMessage("Deathmatch is about to begin.");
                break;
            case DEATHMATCH:
                allowBlockBreak = false;
                currentGameState = "DEATHMATCH";
                Bukkit.broadcastMessage("Deathmatch has begun.");
                break;
            case END:
                currentGameState = "END";
                Bukkit.broadcastMessage("Deathmatch has ended. Winner determined?");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "The game will automatically be terminated in 20 seconds.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        // Players are sent back to main world
                        World MainWorld = Bukkit.getServer().getWorld("world");
                        Location toTeleport = MainWorld.getSpawnLocation();

                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.teleport(toTeleport);
                        }
                        setGameState(RNGGameStates.INACTIVE);
                    }
                }, 400);

                resetMap("RNGSurvival");

                break;
            case INACTIVE:
                currentGameState = "INACTIVE";
                Bukkit.broadcastMessage("RNGSURVIVAL is now inactive.");
                GameEventsManager.managerBusy = false;
                GameEventsManager.currentGame = "None";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + RNGGameStates);
        }
    }

    // old function, might get used
    private String calculateTime(Integer time) {
        Integer hours = time / 3600;
        Integer minutes = (time % 3600) / 60;
        Integer seconds = time % 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeString;
    }

    public void stopGame(CommandSender sender, Integer seconds) {
        Bukkit.broadcastMessage(RNGSurvival.Prefix + ChatColor.RED + sender.getName() + " §7has stopped the game.");
        if(seconds > 0) {
            Bukkit.broadcastMessage(RNGSurvival.Prefix + "The game will stop in " + seconds + " seconds.");
        } else {
            Bukkit.broadcastMessage(RNGSurvival.Prefix + "The game will stop immedtialy.");
        }


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                setGameState(RNGGameStates.END);
            }
        }, seconds * 20);
    }


    public void cleanup() {

    }

    public BlockManager getBlockManager() { return blockManager; }
    public CraftManager getCraftManager() { return craftManager; }


}
