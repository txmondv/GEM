package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Random;

public class RNGPlayerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        Player p = event.getPlayer();

        // check if a game is running to automatically set the player's game mode to spectator, so they do not interact with the game
        if(GameEventsManager.managerBusy) {
            // set the game mode and inform
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(GameEventsManager.PluginPrefix + "Your game mode has been set to spectator, because a game is currently running.");

            // get a random player name from all online players
            ArrayList<Player> allPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            int random = new Random().nextInt(allPlayers.size());
            Player randomPlayer = allPlayers.get(random);

            // teleport to that random player and inform
            p.teleport(randomPlayer);
            p.sendMessage(GameEventsManager.PluginPrefix + "You are now spectating §a" + randomPlayer.getDisplayName());
        }
    }
}
