package dev.timvn.gameeventsmanager.games.rngsurvival;

import dev.timvn.gameeventsmanager.GameEventsManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public class RNGSurvival {

    static GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);


    public static String Prefix = "§8» §e§lRNGS §8- §7";
    public static boolean diceRolling = false;
    public static LocalDateTime lastRollTime = LocalDateTime.now();



    public static int dice(Player p) {


        final int[] timesRolled = {0};
        final int finalNumber = 20; //(int) (Math.random() * 20) + 1

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(plugin, rollTask -> {
            if(timesRolled[0] < 15) {

                diceRolling = true;

                String number = String.valueOf((int) (Math.random() * 20) + 1);
                p.sendTitle("§6§l" + number, "§6Rolling...", 0, 70, 10);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

                timesRolled[0]++;
            } else if(timesRolled[0] == 15) {
                switch(finalNumber) {
                    case 20, 19, 18:
                        p.sendTitle("§a§l" + finalNumber, "§aHow can you be this lucky?!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 2, 1);
                        break;
                    case 17, 16, 15:
                        p.sendTitle("§a§l" + finalNumber, "§aGreat!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                        break;
                    case 14, 13, 12:
                        p.sendTitle("§7§l" + finalNumber, "§7Not bad.", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        break;
                    case 11, 10, 9:
                        p.sendTitle("§7§l" + finalNumber, "§7Acceptable.", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                        break;
                    case 8, 7, 6:
                        p.sendTitle("§7§l" + finalNumber, "§7Could be better...", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
                        break;
                    case 5, 4, 3:
                        p.sendTitle("§c§l" + finalNumber, "§cUh oh!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1, 1);
                        break;
                    case 2:
                        p.sendTitle("§4§l" + finalNumber, "§4So, what now?", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        break;
                    case 1:
                        p.sendTitle("§4§l" + finalNumber, "§4§lYou better give up!", 0, 80, 10);
                        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                        break;
                }
                timesRolled[0]++;
            } else {
                diceRolling = false;
                scheduler.cancelTask(rollTask.getTaskId());
            }
        }, 0, 5L);

        synchronized (scheduler) {
            return finalNumber;
        }

    }

}