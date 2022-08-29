package dev.timvn.gameeventsmanager.commands;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CheckStateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("gem.checkstate")) {

            // guard class: prevent user not passing an option
            if(args.length == 0) {
                sender.sendMessage(GameEventsManager.PluginPrefix + "Please specify a state/option to check (e. g. manager)");
                return true;
            }

            switch(args[0]) {
                case "manager":
                    if(GameEventsManager.managerBusy) { sender.sendMessage(GameEventsManager.PluginPrefix + "The GameManager is currently busy (" + GameEventsManager.currentGame + ")."); }
                    else { sender.sendMessage(GameEventsManager.PluginPrefix + "The GameManager is inactive (no events are active)"); }
                    return true;
                default:
                    sender.sendMessage(GameEventsManager.PluginPrefix + "This option is not available.");
                    return true;
            }
        } else {
            sender.sendMessage(GameEventsManager.noPermission);
        }
        return true;
    }
}
