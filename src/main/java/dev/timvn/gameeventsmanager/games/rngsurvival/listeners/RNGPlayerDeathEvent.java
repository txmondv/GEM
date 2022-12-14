package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;

public class RNGPlayerDeathEvent implements Listener {

    RNGGameManager RNGGameManager;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        Player p = e.getEntity().getPlayer();

        if(GameEventsManager.managerBusy && GameEventsManager.currentGame.equals("rngsurvival")) {

            p.setGameMode(GameMode.SPECTATOR);
            p.spigot().respawn();

            ArrayList<Player> Players = new ArrayList<>();
            for(Player alive : Bukkit.getServer().getOnlinePlayers()) {
                if(alive.getGameMode().equals(GameMode.SURVIVAL)) {
                    Players.add(alive);
                }
            }

            if(Players.size() == 1) {
                RNGGameManager.naturalGameEnd(p);
            }

            if(Players.size() == 0) {
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "§7You have died and the game is over!");
                Bukkit.broadcastMessage(RNGSurvival.Prefix + "§7Hint: The game is more fun if you don't play alone :)");
                RNGGameManager.setGameState(RNGGameStates.END);
            }

            EntityDamageEvent lastDamageCause = e.getEntity().getLastDamageCause();

            if(lastDamageCause instanceof Player) {
                e.setDeathMessage(RNGSurvival.Prefix + "§c" + p.getDisplayName() + " §7has been killed by §c" + ((Player) lastDamageCause).getDisplayName() + "§7. " + Players.size() + " players remaining.");
            } else {
                e.setDeathMessage(RNGSurvival.Prefix + "§c" + p.getDisplayName() + " §7died. " + Players.size() + " players remaining.");
            }




        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(GameEventsManager.managerBusy && GameEventsManager.currentGame.equals("rngsurvival")) {
            e.setRespawnLocation(e.getPlayer().getLocation());
        }

    }

}
