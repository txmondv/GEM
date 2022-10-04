package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival.*;

public class RNGPlayerMoveListener implements Listener {

    GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {

        Player p = event.getPlayer();

        // Guard class: check if the game is RNGSurvival and security check to prevent bugs!
        if(!GameEventsManager.managerBusy || !Objects.equals(GameEventsManager.currentGame, "rngsurvival")) { return; }

        // do not roll when the game state is not ACTIVE or PREDEATHMATCH
        if(!RNGGameManager.startRollingActions) { return; }

        // Guard class: If dice is rolling, don't roll.
        if(diceRolling) { return; }


        // if dice isn't already rolling and from 1/10000 the movement is selected to roll; roll!
        if((int) (Math.random() * 10000) + 1 == 1) {
            int rolledNumber = RNGSurvival.roll(p);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                switch (rolledNumber) {
                    case 20, 19, 18:
                        // concept: speed and regen
                        p.addPotionEffect(PotionEffectType.SPEED.createEffect(1200, 3));
                        p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(1200, 3));


                        p.sendMessage(Prefix + "You got very lucky and rolled a §a" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "Therefore you get some buffs!");
                        break;
                    case 17, 16, 15:
                        // concept: apples
                        p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.APPLE,  rolledNumber));

                        p.sendMessage(Prefix + "You got lucky rolled a §a" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "As a result, " + rolledNumber + " apples appeared.");
                        break;
                    case 14, 13, 12:
                        // concept: pig
                        Pig piggyPigson = (Pig) p.getWorld().spawnEntity(p.getLocation(), EntityType.PIG);
                        piggyPigson.setGlowing(true);

                        p.sendMessage(Prefix + "Good! You rolled a §6" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "Wait, did you see that pig?!");
                        break;
                    case 11, 10, 9:
                        p.sendMessage(Prefix + "You rolled a §6" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "This is a boring day, just go on.");
                        break;
                    case 8, 7, 6:
                        // concept: 1 heart damage (2 health)
                        p.setHealth(p.getHealth() - 2);

                        p.sendMessage(Prefix + "Hm, you rolled a §6" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "Ouch!");
                        break;
                    case 5, 4, 3:
                        // concept: freeze the player for 10s
                        p.addPotionEffect(PotionEffectType.SLOW.createEffect(200, 20));

                        p.sendMessage(Prefix + "You got unlucky and rolled a §c" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "Therefore you get frozen!");
                        break;
                    case 2:
                        // concept: spawn 2 creepers
                        p.getWorld().spawnEntity(p.getLocation(), EntityType.CREEPER);
                        p.getWorld().spawnEntity(p.getLocation(), EntityType.CREEPER);

                        p.sendMessage(Prefix + "You got super unlucky and rolled a §c" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "There are creepers hunting you!");
                        break;
                    case 1:
                        // concept: TNT
                        p.getWorld().spawn(event.getPlayer().getLocation().add(0, 0, 0), TNTPrimed.class);

                        p.sendMessage(Prefix + "You got incredibly unlucky and rolled a §4" + rolledNumber + "§7!");
                        p.sendMessage(Prefix + "§4EXPLODE!");
                        break;
                    default:
                }

            }, timeRollShouldLast);
        }
    }
}
