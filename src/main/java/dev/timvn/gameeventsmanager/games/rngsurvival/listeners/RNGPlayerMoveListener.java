package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

import static dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival.Prefix;
import static dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival.diceRolling;

public class RNGPlayerMoveListener implements Listener {

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

            switch(rolledNumber) {
                case 20, 19, 18:
                    // concept:
                    p.sendMessage(Prefix + "You got very lucky and rolled a §a" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                case 17, 16, 15:
                    // concept:
                    p.sendMessage(Prefix + "You got lucky rolled a §a" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                case 14, 13, 12:
                    // concept:
                    p.sendMessage(Prefix + "Good! You rolled a §6" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                case 11, 10, 9:
                    // concept:
                    p.sendMessage(Prefix + "You rolled a §6" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                case 8, 7, 6:
                    // concept:
                    p.sendMessage(Prefix + "Hm, you rolled a §6" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                case 5, 4, 3:
                    // concept:
                    p.sendMessage(Prefix + "You got unlucky and rolled a §c" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                case 2:
                    // concept:
                    p.sendMessage(Prefix + "You got super unlucky and rolled a §c" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                case 1:
                    // concept:
                    p.sendMessage(Prefix + "You got incredibly unlucky and rolled a §4" + rolledNumber + "§7!");
                    p.sendMessage(Prefix + "Right now, there is nothing set up to happen.");
                    break;
                default:
            }
        }

    }
}
