package dev.timvn.gameeventsmanager.commands;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameStates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Array;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class EventStartCommand implements CommandExecutor {

    GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);
    private RNGGameManager RNGGameManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        String[] eventsList = {
                "RNGSurvival"
        };

        if(sender.hasPermission("gem.execute")) {

            if(args.length == 0) {
                sender.sendMessage(GameEventsManager.PluginPrefix + "§cYou have to specify an event.");
                sender.sendMessage(GameEventsManager.PluginPrefix + "§7Choose one from the ones listed below:");
                sender.sendMessage("");

                for(int i = 0; i < eventsList.length; i++) {
                    sender.sendMessage("§8» §7" + eventsList[i]);
                }

                sender.sendMessage("");

                return false;
            }

            String eventName = args[0].toLowerCase(Locale.ROOT);

            switch(eventName) {
                case "rngsurvival":
                    this.RNGGameManager = new RNGGameManager(plugin);

                    // stop command
                    if(args.length > 1) {
                        if(GameEventsManager.managerBusy && GameEventsManager.currentGame == "rngsurvival") {
                            if (args[1].equalsIgnoreCase("stop")) {
                                if (args.length == 2) {
                                    RNGGameManager.stopGame(sender, 30);
                                    break;
                                } else {
                                    RNGGameManager.stopGame(sender, parseInt(args[2]));
                                    break;
                                }
                            }
                        } else {
                            sender.sendMessage(GameEventsManager.PluginPrefix + "RNGSurvival is not active/running.");
                            sender.sendMessage(GameEventsManager.PluginPrefix + "Use '/check manager'.");
                            break;
                        }

                    }


                    if(GameEventsManager.managerBusy) {
                        sender.sendMessage(GameEventsManager.PluginPrefix + "§cAnother event is already running!");
                        return true;
                    }

                    sender.sendMessage(GameEventsManager.PluginPrefix + "You have started RNG-Survival.");
                    GameEventsManager.managerBusy = true;
                    GameEventsManager.currentGame = "rngsurvival";
                    RNGGameManager.setGameState(RNGGameStates.STARTING);
                    break;
                default:
                    sender.sendMessage(GameEventsManager.PluginPrefix + "§cThis event/game does not exist.");
                    sender.sendMessage(GameEventsManager.PluginPrefix + "§7Choose one from the ones listed below:");
                    sender.sendMessage("");

                    for(int i = 0; i < eventsList.length; i++) {
                        sender.sendMessage("§8» §7" + eventsList[i]);
                    }

                    sender.sendMessage("");
            }
        } else {
            sender.sendMessage(GameEventsManager.noPermission);
        }



        return true;
    }
}
