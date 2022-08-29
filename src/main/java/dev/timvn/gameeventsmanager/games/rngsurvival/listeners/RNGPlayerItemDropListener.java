package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager.lastItemDropTime;

public class RNGPlayerItemDropListener implements Listener {


    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {

        Player p = event.getPlayer();

        // Guard class: check if the game is RNGSurvival and security check to prevent bugs!
        if(!GameEventsManager.managerBusy || !Objects.equals(GameEventsManager.currentGame, "rngsurvival")) { return; }

        // Guard class: Cancel event if the GameState is not ACTIVE or PREDEATHMATCH
        if(!RNGGameManager.allowBlockBreak) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cPlease wait for the game to start!"));
            event.setCancelled(true);
            return;
        }

        // determine what to do:
        int doesDropCauseEvent = (int) (Math.random() * 50) + 1;
        int itemAmount = event.getItemDrop().getItemStack().getAmount();

        // prevent spamming/abusing by adding a 5 second cooldown
        if(lastItemDropTime != null) {
            Duration duration = Duration.between(lastItemDropTime, LocalDateTime.now());
            if(duration.toSeconds() < 5) { return; }
        }

        if(event.getItemDrop().getItemStack().getType() == Material.DIRT) { return; }

        switch (doesDropCauseEvent) {
            case 1, 2, 3, 4, 5:
                if (itemAmount == 1) {
                    p.sendMessage(RNGSurvival.Prefix + "Ha! Your §c" + event.getItemDrop().getName() + " §7has been stolen!");
                } else {
                    p.sendMessage(RNGSurvival.Prefix + "Ha! Your §c" + event.getItemDrop().getName() + "s §7have been stolen!");
                }
                event.getItemDrop().getItemStack().setAmount(0);

                lastItemDropTime = LocalDateTime.now();
                break;
            case 6, 7, 8, 9, 10:
                if (itemAmount == 1) {
                    p.sendMessage(RNGSurvival.Prefix + "Whoa! Your §a" + event.getItemDrop().getName() + " §7has duplicated!");
                } else {
                    p.sendMessage(RNGSurvival.Prefix + "Whoa! Your §a" + event.getItemDrop().getName() + "s §7have been duplicated!");
                }
                event.getItemDrop().getItemStack().setAmount(event.getItemDrop().getItemStack().getAmount() * 2);

                lastItemDropTime = LocalDateTime.now();
                break;
            case 11, 12, 13:
                if (itemAmount == 1) {
                    p.sendMessage(RNGSurvival.Prefix + "Yeah! Your §a" + event.getItemDrop().getName() + " §7has been turned to Iron!");
                } else {
                    p.sendMessage(RNGSurvival.Prefix + "Yeah! Your §a" + event.getItemDrop().getName() + "s §7have been turned to Iron!");
                }
                event.getItemDrop().getItemStack().setAmount(1);
                event.getItemDrop().getItemStack().setType(Material.IRON_INGOT);

                lastItemDropTime = LocalDateTime.now();
                break;
            case 14, 15, 16, 17:
                if (itemAmount == 1) {
                    p.sendMessage(RNGSurvival.Prefix + "Meh.. Your §c" + event.getItemDrop().getName() + " §7has been turned to Dirt§7.");
                } else {
                    p.sendMessage(RNGSurvival.Prefix + "Meh.. Your §c" + event.getItemDrop().getName() + "s §7have been turned to Dirt§7.");
                }
                event.getItemDrop().getItemStack().setType(Material.DIRT);

                lastItemDropTime = LocalDateTime.now();
                break;
            default:

        }


    }
}
