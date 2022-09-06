package dev.timvn.gameeventsmanager.games.rngsurvival;

import dev.timvn.gameeventsmanager.GameEventsManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.util.Random;


public class RNGSurvival {

    // import main class to be able to work with the plugin and server
    static GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);

    // central managed Prefix-String
    public static String Prefix = "§8» §e§lRNGS §8- §7";
    
    // possibility to check if the dice is rolling (e.g. to prevent double rolls)
    public static boolean diceRolling = false;

    // public long to set the time the roll lasts centrally
    public static long timeRollShouldLast = 50L;

    // store when the dice has been rolled
    public static LocalDateTime lastRollTime = LocalDateTime.now();


    // old method to be deleted
    /*/public static int dice(Player p) throws InterruptedException {

        // timesRolled has to be an array because otherwise it could not be accessed from the scheduler
        final int[] timesRolled = {0};

        // pre-declare the finalNumber to be able to display it at the end of the roll
        int finalNumber = (int) (Math.random() * 20) + 1;

        diceRolling = true;

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, rollTask -> {
            if(timesRolled[0] < 14) {
                diceRolling = true;
                // get different random numbers and display them in a title on screen and play a sound
                String number = String.valueOf((int) (Math.random() * 20) + 1);
                p.sendTitle("§6§l" + number, "§6Rolling...", 0, 70, 10);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                timesRolled[0]++;
            } else if(timesRolled[0] == 14) {
                // on the 15th roll, display the final number with a comment
                switch (finalNumber) {
                    case 20, 19, 18 -> {
                        p.sendTitle("§a§l" + finalNumber, "§aHow can you be this lucky?!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 2, 1);
                    }
                    case 17, 16, 15 -> {
                        p.sendTitle("§a§l" + finalNumber, "§aGreat!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    }
                    case 14, 13, 12 -> {
                        p.sendTitle("§7§l" + finalNumber, "§7Not bad.", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                    case 11, 10, 9 -> {
                        p.sendTitle("§7§l" + finalNumber, "§7Acceptable.", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                    }
                    case 8, 7, 6 -> {
                        p.sendTitle("§7§l" + finalNumber, "§7Could be better...", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                    }
                    case 5, 4, 3 -> {
                        p.sendTitle("§c§l" + finalNumber, "§cUh oh!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1, 1);
                    }
                    case 2 -> {
                        p.sendTitle("§4§l" + finalNumber, "§4So, what now?", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    }
                    case 1 -> {
                        p.sendTitle("§4§l" + finalNumber, "§4§lYou better give up!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                    }
                }
                timesRolled[0]++;
            } else {
                // if the dice has been rolled more than 15 times, shut everything down
                diceRolling = false;
                lastRollTime = LocalDateTime.now();
                scheduler.cancelTask(rollTask.getTaskId());
            }
        }, 0, 5L);

        while(diceRolling) {
        }

        return finalNumber;
    }/*/

    private static Random r = new Random();

    public static int roll(final Player p) {
        if(!p.getGameMode().equals(GameMode.CREATIVE) && !p.getGameMode().equals(GameMode.SPECTATOR)) {

            final int diceResult = /*/r.nextInt(20) + 1;/*/ 20;

            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) timeRollShouldLast, 5000, false, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) timeRollShouldLast, 128, false, false, false));

            final BukkitRunnable runnable = new BukkitRunnable() {
                public void run() {
                    p.sendTitle("§l§6" + r.nextInt(20), "§l§6Rolling...", 0, 5, 0);
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }
            };
            (new BukkitRunnable() {
                public void run() {
                    runnable.cancel();
                    switch (diceResult) {
                        case 20, 19, 18:
                            p.sendTitle("§a§l" + diceResult, "§aHow can you be this lucky?!", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 2, 1);
                            break;
                        case 17, 16, 15:
                            p.sendTitle("§a§l" + diceResult, "§aGreat!", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                            break;
                        case 14, 13, 12:
                            p.sendTitle("§7§l" + diceResult, "§7Not bad.", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                            break;
                        case 11, 10, 9:
                            p.sendTitle("§7§l" + diceResult, "§7Acceptable.", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                            break;
                        case 8, 7, 6:
                            p.sendTitle("§7§l" + diceResult, "§7Could be better...", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                            break;
                        case 5, 4, 3:
                            p.sendTitle("§c§l" + diceResult, "§cUh oh!", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1, 1);
                            break;
                        case 2:
                            p.sendTitle("§4§l" + diceResult, "§4So, what now?", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                            break;
                        case 1:
                            p.sendTitle("§4§l" + diceResult, "§4§lYou better give up!", 0, 80, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                            break;
                    }

                }
            }).runTaskLater(plugin, timeRollShouldLast); // timeRollShouldLast is set so that all delays are managed centrally
            runnable.runTaskTimer(plugin, 0L, 5L);
            return diceResult;

        } else {
            return -1;
        }
    }

}