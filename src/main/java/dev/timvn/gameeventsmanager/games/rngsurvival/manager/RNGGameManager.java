package dev.timvn.gameeventsmanager.games.rngsurvival.manager;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;

import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;


import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RNGGameManager {

    // disable block breaking and moving (!?) for all phases except active/predeathmatch
    public static boolean allowBlockBreak = true;

    // initialize the plugin for later use
    private final GameEventsManager plugin;

    // initialize the main class!
    GameEventsManager mainClass = GameEventsManager.getPlugin(GameEventsManager.class);

    // set default state to be inactive
    public RNGGameStates RNGGameStates = dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates.INACTIVE;

    // lastItemDropTime is used to prevent item drop spamming (in RNGPlayerItemDropListener)
    public static LocalDateTime lastItemDropTime;


    private final BlockManager blockManager;
    private final CraftManager craftManager;

    public RNGGameManager(GameEventsManager plugin) {
        this.plugin = plugin;

        this.blockManager = new BlockManager(this);
        this.craftManager = new CraftManager(this);
    }


    // get GameState
    public static String currentGameState = "INACTIVE";
    public static String getGameState() {
        return currentGameState;
    }
    public static boolean cancelMainTaskTimer = false;


    public void setGameState(RNGGameStates RNGGameStates) {

        BukkitScheduler mainScheduler = Bukkit.getScheduler();
        BukkitScheduler preDMScheduler = Bukkit.getScheduler();

        Location coords = plugin.getConfig().getLocation("RNGSurvivalSpawn");

        this.RNGGameStates = RNGGameStates;
        switch(RNGGameStates) {
            case STARTING:
                currentGameState = "STARTING";
                // Countdown Start!

                final int[] countdownStarter = {5}; // Game starting countdown

                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final Runnable runnable = () -> {

                    countdownStarter[0]--;

                    // Default case: The countdown is still going (20-1); Display the remaining time
                    if(countdownStarter[0] > 0) {
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7The game is starting in §e" + countdownStarter[0] + " §7seconds."));
                        }
                    }

                    // Special case: The countdown is exactly at 1; display "starting in 1 second" :)
                    if (countdownStarter[0] == 1) {
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7The game is starting in §e" + countdownStarter[0] + " §7second."));
                        }
                    }

                    // Special case: The countdown is exactly at 0; Don't display "starting in 0 seconds" :)
                    if (countdownStarter[0] == 0) {
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Starting..."));
                        }
                    }

                    // Special case: The countdown is lower than zero: set game state & shutdown the scheduler
                    if (countdownStarter[0] < 0) {
                        // GameState can now change
                        setGameState(dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates.ACTIVE);
                        scheduler.shutdown();
                    }
                };
                scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
                // Countdown End!

                // prevent bad weather or night
                Bukkit.getWorld("RNGSurvival").setStorm(false);
                Bukkit.getWorld("RNGSurvival").setTime(1000);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        // Players are sent to the world here because p.teleport() cannot be executed from a repeat scheduler
                        Location coords = plugin.getConfig().getLocation("RNGSurvivalSpawn");
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§aThe game has started!"));
                            p.sendTitle("§6The game has started!", "§aGood Luck! :)", 10, 130, 10);
                            p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_5, 2, 1);

                            int coordsY = coords.getWorld().getHighestBlockYAt(coords.getBlockX(), coords.getBlockZ()) + 1;
                            coords.setY(coordsY);

                            // ensure safe teleport to a solid block

                            while(!coords.getBlock().getType().isSolid() && coords.getBlock().getBiome().equals(Biome.ICE_SPIKES)) {
                                coords.setX(coords.getX() + 1);
                            }

                            p.teleport(coords);
                        }

                        Bukkit.getWorld("RNGSurvival").getWorldBorder().setCenter(coords);
                        Bukkit.getWorld("RNGSurvival").getWorldBorder().setSize(20000);
                    }
                }, countdownStarter[0] * 20L);

                break;
            case ACTIVE:
                allowBlockBreak = true;
                currentGameState = "ACTIVE";
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "The game has started.");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "You now have §620 minutes §7until deathmatch starts.");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "Good Luck! :)");

                cancelMainTaskTimer = false;

                /*/ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard scoreboard = manager.getNewScoreboard();

                Objective objective = scoreboard.registerNewObjective("RNGSurvival", "dummy", "§e§lRNGSurvival");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                Score score = objective.getScore("§aHi");
                score.setScore(1);

                for(Player p : Bukkit.getOnlinePlayers()){
                    p.setScoreboard(scoreboard);
                }/*/

                // set game time to 20 minutes (1200 seconds)
                AtomicInteger mainGameTimer = new AtomicInteger(1200);

                mainScheduler.runTaskTimer(plugin, mainTask -> {

                    if(cancelMainTaskTimer) {
                        mainScheduler.cancelTask(mainTask.getTaskId());
                        for(Player p : Bukkit.getOnlinePlayers()){


                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cThe timer has expired."));
                        }
                    }

                    String timeString = String.format("%02d:%02d", (mainGameTimer.get() % 3600) / 60, mainGameTimer.get() % 60);

                    //  Default case: The countdown is still goin
                    if(mainGameTimer.get() > 0) {
                        for(Player p : Bukkit.getOnlinePlayers()){


                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Time until end-game: " + timeString));
                            mainGameTimer.getAndDecrement();
                        }
                    }

                    if(mainGameTimer.get() == 599) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 10 minutes!");
                    } if(mainGameTimer.get() == 299) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 5 minutes!");
                    } if(mainGameTimer.get() == 179) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 3 minutes!");
                    } if(mainGameTimer.get() == 59) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 1 minute!");
                    } if(mainGameTimer.get() == 29) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 30 seconds!");
                    } if(mainGameTimer.get() == 19) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 20 seconds!");
                    } if(mainGameTimer.get() == 9) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 10 seconds!");
                    } if(mainGameTimer.get() == 4) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 5 seconds!");
                    } if(mainGameTimer.get() == 3) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 4 seconds!");
                    } if(mainGameTimer.get() == 2) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 3 seconds!");
                    } if(mainGameTimer.get() == 1) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 2 seconds!");
                    } if(mainGameTimer.get() == 0) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game starts in 1 second!");
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7End-game is starting!"));
                            mainGameTimer.getAndDecrement();
                        }
                    }


                    // Special case: The countdown is lower than zero
                    if (mainGameTimer.get() < 0) {
                        // GameState can now change
                        setGameState(dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates.PREDEATHMATCH);
                        mainScheduler.cancelTask(mainTask.getTaskId());
                    }
                }, 0L, 20L);



                break;
            case PREDEATHMATCH:
                allowBlockBreak = true;
                currentGameState = "PREDEATHMATCH";
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "End-game has begun!");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "The border is now way smaller (§c500 blocks §7diameter)");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "You now have §c2 minutes §7to prepare for the final fight.");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "During this time, you cannot attack other players.");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "§cBe careful: During deathmatch, you can no longer break blocks.");

                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p.getGameMode() != GameMode.SPECTATOR) {
                        p.sendTitle("§cDeathmatch begins soon!", "§6Prepare for the fight!", 10, 80, 10);
                        p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_7, 2, 1);
                    }

                    int coordsY = coords.getWorld().getHighestBlockYAt(coords.getBlockX(), coords.getBlockZ()) + 1;
                    coords.setY(coordsY);
                    p.teleport(coords);
                }

                Bukkit.getWorld("RNGSurvival").getWorldBorder().setCenter(coords);
                Bukkit.getWorld("RNGSurvival").getWorldBorder().setSize(500);

                // set pre dm time to 2 minutes (120s)
                AtomicInteger preDMGameTimer = new AtomicInteger(120);

                coords.getWorld().setPVP(false);

                preDMScheduler.runTaskTimer(plugin, preDMTask -> {

                    String timeString = String.format("%02d:%02d", (preDMGameTimer.get() % 3600) / 60, preDMGameTimer.get() % 60);

                    //  Default case: The countdown is still goin
                    if(preDMGameTimer.get() > 0) {
                        for(Player p : Bukkit.getOnlinePlayers()){


                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Time until deathmatch: " + timeString));
                            preDMGameTimer.getAndDecrement();
                        }
                    }

                    if(preDMGameTimer.get() == 60) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "Deathmatch starts in 1 minute!");
                    } if(preDMGameTimer.get() == 30) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "Deathmatch starts in 30 seconds!");
                    } if(preDMGameTimer.get() == 20) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "Deathmatch starts in 20 seconds!");
                    } if(preDMGameTimer.get() == 10) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "Deathmatch starts in 10 seconds!");
                    } if(preDMGameTimer.get() == 1) {
                        Bukkit.broadcastMessage(RNGSurvival.Prefix + "Deathmatch is now starting!");
                    }


                    // Special case: The countdown is exactly at 0
                    if (preDMGameTimer.get() == 0) {
                        for(Player p : Bukkit.getOnlinePlayers()){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§7Deathmatch is starting!"));
                            preDMGameTimer.getAndDecrement();
                        }
                    }

                    // Special case: The countdown is lower than zero
                    if (preDMGameTimer.get() < 0) {
                        // GameState can now change
                        setGameState(dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates.DEATHMATCH);
                        preDMScheduler.cancelTask(preDMTask.getTaskId());
                    }
                }, 0L, 20L);

                break;
            case DEATHMATCH:
                allowBlockBreak = false;
                currentGameState = "DEATHMATCH";

                coords.getWorld().setPVP(true);
                // let the world border shrink to 50 diameter in 300 seconds
                Bukkit.getWorld("RNGSurvival").getWorldBorder().setSize(50, 300);

                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p.getGameMode() != GameMode.SPECTATOR) {
                        p.sendTitle("§cDeathmatch begins!", "§6FIGHT!", 10, 80, 10);
                        p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_3, 2, 1);
                    }
                }

                Bukkit.broadcastMessage(RNGSurvival.Prefix + "Deathmatch has begun!");
                break;
            case END:
                currentGameState = "END";
                Bukkit.broadcastMessage("Deathmatch has ended. Winner determined?");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "The game will automatically be terminated in 20 seconds.");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        setGameState(dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates.INACTIVE);
                    }
                }, 400);


                break;
            case INACTIVE:
                currentGameState = "INACTIVE";

                cancelMainTaskTimer = true;

                // Players are sent back to main world
                World MainWorld = Bukkit.getServer().getWorld("world");
                Location toTeleport = MainWorld.getSpawnLocation();

                for(Player p : Bukkit.getOnlinePlayers()){
                    p.teleport(toTeleport);

                    if(p.getGameMode().equals(GameMode.SPECTATOR)) {
                        p.setGameMode(GameMode.SURVIVAL);
                    }

                }


                Location oldSpawnLocation = plugin.getConfig().getLocation("RNGSurvivalSpawn");
                Location newSpawnLocation = oldSpawnLocation.add(10000, 0, 10000);
                Bukkit.getWorld("RNGSurvival").getWorldBorder().reset();
                plugin.getConfig().set("RNGSurvivalSpawn", newSpawnLocation);
                plugin.saveConfig();

                Bukkit.broadcastMessage("GAME OVER!");
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
                setGameState(dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates.INACTIVE);
            }
        }, seconds * 20);
    }




    public BlockManager getBlockManager() { return blockManager; }
    public CraftManager getCraftManager() { return craftManager; }


}
