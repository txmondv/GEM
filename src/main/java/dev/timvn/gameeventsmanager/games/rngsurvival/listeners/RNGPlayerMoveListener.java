package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

import static dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival.diceRolling;

public class RNGPlayerMoveListener implements Listener {

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {

        Player p = event.getPlayer();

        // Guard class: check if the game is RNGSurvival and security check to prevent bugs!
        if(!GameEventsManager.managerBusy || !Objects.equals(GameEventsManager.currentGame, "rngsurvival")) { return; }

        if(!RNGGameManager.allowBlockBreak) { return; }

        // Guard class: If dice is rolling, dont roll.
        if(diceRolling) { return; }


        // if dice isnt already rolling and from 1/5000 the movement is selected to roll; roll!
        if((int) (Math.random() * 5000) + 1 == 1) {
            int rolledNumber = RNGSurvival.dice(p);
        }

    }
}
