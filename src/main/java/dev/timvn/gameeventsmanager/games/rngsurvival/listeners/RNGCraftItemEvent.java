package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.BlockManager;

import dev.timvn.gameeventsmanager.games.rngsurvival.manager.CraftManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


public class RNGCraftItemEvent implements Listener {

    GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);

    @EventHandler
    private void onCraft(CraftItemEvent event) {

        Player p = (Player) event.getWhoClicked();

        // Guard class: check if the game is RNGSurvival and security check to prevent bugs!
        if(!GameEventsManager.managerBusy || !Objects.equals(GameEventsManager.currentGame, "rngsurvival")) { return; }



        // Guard class: Cancel event if the GameState is not ACTIVE or PREDEATHMATCH
        if(!RNGGameManager.allowBlockBreak) { return; }

        String eventType = CraftManager.checkCraftingEvent(event.getCurrentItem());
        switch(eventType) {
            case "tool_craft_event":
                int durability_percentage = ThreadLocalRandom.current().nextInt((int) 0.1, 1);
                p.sendMessage(String.valueOf(durability_percentage));

                ItemStack item = event.getCurrentItem();
                p.sendMessage(String.valueOf(item));

                if(!(item.getItemMeta() instanceof Damageable)) { return; }

                Short maxDur = item.getType().getMaxDurability();
                p.sendMessage(String.valueOf(maxDur));

                ((Damageable) item.getItemMeta()).setDamage((int) (maxDur * 0.9));
                p.sendMessage(String.valueOf(maxDur * 0.9));
                break;


            default:
                break;
        }
    }



}
