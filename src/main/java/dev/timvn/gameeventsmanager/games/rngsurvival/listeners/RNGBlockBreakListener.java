package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.BlockManager;

import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

import static dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival.*;

public class RNGBlockBreakListener implements Listener {

    GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);


    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {

        Player p = event.getPlayer();

        // Guard class: check if the game is RNGSurvival and security check to prevent bugs!
        if(!GameEventsManager.managerBusy || !Objects.equals(GameEventsManager.currentGame, "rngsurvival")) { return; }

        // Guard class: Cancel event if the dice is busy rolling
        if(diceRolling) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cYou cannot break blocks during a roll..."));
            event.setCancelled(true);
            return;
        }


        // Guard class: Cancel event if the GameState is not ACTIVE or PREDEATHMATCH
        if(!RNGGameManager.allowBlockBreak) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§cYou cannot break blocks during this game phase!"));
            event.setCancelled(true);
            return;
        }

        String eventType = BlockManager.checkEvent(event.getBlock());
        switch (eventType) {
            case "wood_event" -> {
                // make wood breaking a 1/3 chance to trigger the roll
                if ((int) (Math.random() * 3) + 1 != 1) {
                    break;
                }

                int woodEventRolledNumber = RNGSurvival.roll(p);

                // simply delay task by 4.25 seconds (time dice needs to run)
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        switch (woodEventRolledNumber) {
                            case 20:
                                // concept:
                            case 19:
                                // concept:
                            case 18:
                                // concept: Drop 20 to 40 Oak Logs
                                int newOakLogsAmount = (int) ((Math.random() * (40 - 20)) + 20);
                                ItemStack newLogs = new ItemStack(event.getBlock().getType(), newOakLogsAmount);
                                p.sendMessage(String.valueOf(event.getBlock().getType()));
                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), newLogs);
                                event.setDropItems(false);

                                p.sendMessage(Prefix + "You got amazingly lucky and rolled a §a" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "As luck should be rewarded, you get §a" + newOakLogsAmount + " §7instead of 1 log!");
                                break;
                            case 17:
                                // concept:
                            case 16:
                                // concept:
                            case 15:
                                // concept: Give Haste for a while
                                int AmountOfTicksThatHasteIsGranted = (int) ((Math.random() * (4500 - 1500)) + 1500);
                                // 20 Ticks = 1 Second :)
                                p.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(AmountOfTicksThatHasteIsGranted, 3));

                                p.sendMessage(Prefix + "Somehow you got lucky and rolled a §a" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Because your tree likes this number, you get " + AmountOfTicksThatHasteIsGranted / 20 + " seconds of Haste III!");
                                break;
                            case 14:
                                // concept:
                            case 13:
                                // concept:
                            case 12:
                                // concept:
                            case 11:
                                // concept:
                            case 10:
                                // concept:
                            case 9:
                                // concept:
                            case 8:
                                // concept:
                            case 7:
                                // concept:
                            case 6:
                                // concept:
                            case 5:
                                // concept:
                            case 4:
                                // concept:
                            case 3:
                                // concept: spawn bees!
                                int BeeSwarmSize = (int) ((Math.random() * 20) + 1);
                                event.getBlock().breakNaturally();

                                final int[] BeeTimesRolled = {0};
                                BukkitTask task = new BukkitRunnable() {
                                    public void run() {

                                        if (BeeSwarmSize == BeeTimesRolled[0]) {
                                            cancel();
                                        }

                                        Bee ThePlayersNewUnbestBeeFriend = (Bee) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.BEE);
                                        ThePlayersNewUnbestBeeFriend.setAnger(50);
                                        ThePlayersNewUnbestBeeFriend.setTarget(p);

                                        BeeTimesRolled[0]++;

                                    }

                                }.runTaskTimer(plugin, 0L, 0L);


                                p.sendMessage(Prefix + "You got very unlucky and rolled a §c" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "You unfortunately hit a bee nest §7§linside of §7the tree... §cNow they are angry!");
                                break;
                            case 2:
                                // concept:
                            case 1:
                                // concept:
                            default:
                                break;
                        }
                    }
                }, 70L);

            }
            case "mine_event" -> {
                // make mining event 1/10
                if ((int) (Math.random() * 10) + 1 != 1) {
                    break;
                }
                int MineEventRolledNumber = RNGSurvival.roll(p);

                // simply delay task by 4.25 seconds (time dice needs to run)
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        switch (MineEventRolledNumber) {
                            case 18, 19, 20:
                                // disable block item drops, then drop 1-5 diamonds at the exact same poistion
                                int tempRandomNumber = (int) ((Math.random() * 3) + 1);
                                ItemStack diamondItem = new ItemStack(Material.DIAMOND, tempRandomNumber);
                                event.setDropItems(false);
                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), diamondItem);

                                p.sendMessage(Prefix + "You got super lucky and rolled a §a" + MineEventRolledNumber + "§7!");
                                if (tempRandomNumber == 1) {
                                    p.sendMessage(Prefix + "Therefore you get §b1 Diamond§7, congrats!");
                                } else {
                                    p.sendMessage(Prefix + "Therefore you get §b" + tempRandomNumber + " Diamonds§7, congrats!");
                                }
                                break;
                            case 15, 16, 17:
                                int tempRandomNumber1 = (int) ((Math.random() * (4800 - 1200)) + 1200);
                                p.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(tempRandomNumber1, 1));

                                p.sendMessage(Prefix + "You got lucky and rolled a §a" + MineEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "As a reward, you get " + tempRandomNumber1 / 20 + " seconds of Night Vision!");

                                break;
                            case 12, 13, 14:
                                World worldtoSpawnIn = p.getWorld();
                                Location locToSpawnAt = event.getBlock().getLocation();
                                Allay Allay = (Allay) worldtoSpawnIn.spawnEntity(locToSpawnAt, EntityType.ALLAY, true);


                                p.sendMessage(Prefix + "You got somewhat lucky and rolled a " + MineEventRolledNumber + "!");
                                p.sendMessage(Prefix + "You get an Allay, take care of him :)");

                                break;
                            case 9, 10, 11:
                                event.getBlock().getLocation().getBlock().setType(Material.LAVA);

                                p.sendMessage(Prefix + "Hm, you got away with a " + MineEventRolledNumber + ", not too bad.");
                                p.sendMessage(Prefix + "Still, your block has turned into lava ^^");
                                break;
                            case 6, 7, 8:
                                int tempRandomNumber3 = (int) ((Math.random() * (400 - 200)) + 1);
                                p.addPotionEffect(PotionEffectType.POISON.createEffect(tempRandomNumber3, 1));
                                p.getLocation().getBlock().setType(Material.COBWEB);
                                Endermite spider = (Endermite) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ENDERMITE);

                                p.sendMessage(Prefix + "You rolled a §c" + MineEventRolledNumber + "§7 and upset the witches of the cave.");
                                p.sendMessage(Prefix + "As a punishment, they call " + tempRandomNumber3 / 20 + " seconds of poison as well as a magical mouse upon you.");

                                break;
                            case 3, 4, 5:
                                int tempRandomNumber2 = (int) ((Math.random() * 10) + 1);
                                World worldtoSpawnIn1 = p.getWorld();
                                Location locToSpawnAt1 = event.getBlock().getLocation();
                                event.getBlock().breakNaturally();

                                final int[] timesRolled = {0};
                                BukkitTask task = new BukkitRunnable() {
                                    public void run() {
                                        if (tempRandomNumber2 == timesRolled[0]) {
                                            cancel();
                                        }
                                        Silverfish fishyDude = (Silverfish) worldtoSpawnIn1.spawnEntity(locToSpawnAt1, EntityType.SILVERFISH);
                                        timesRolled[0]++;
                                    }

                                }.runTaskTimer(plugin, 0L, 0L);


                                p.sendMessage(Prefix + "You got unlucky and rolled a §c" + MineEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Now you are being hunted by a swarm of silverfish!");

                                break;
                            case 2:
                                Skeleton skeleton1 = (Skeleton) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.SKELETON);
                                Skeleton skeleton2 = (Skeleton) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.SKELETON);
                                Skeleton skeleton3 = (Skeleton) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.SKELETON);

                                Creeper creeper = (Creeper) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.CREEPER);

                                Zombie zombie1 = (Zombie) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ZOMBIE);
                                Zombie zombie2 = (Zombie) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ZOMBIE);
                                Zombie zombie3 = (Zombie) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ZOMBIE);

                                p.sendMessage(Prefix + "You got very unlucky and rolled a §4" + MineEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Good luck with these bad bois §o°_°");

                                break;
                            case 1:
                                Block targetBlock = event.getBlock();
                                Entity tnt = p.getWorld().spawn(targetBlock.getLocation().add(0, 0, 0), TNTPrimed.class);
                                Entity tnt1 = p.getWorld().spawn(targetBlock.getLocation().add(1, 0, 0), TNTPrimed.class);
                                Entity tnt2 = p.getWorld().spawn(targetBlock.getLocation().add(0, 0, 1), TNTPrimed.class);

                                ((TNTPrimed) tnt).setFuseTicks(10);
                                ((TNTPrimed) tnt1).setFuseTicks(10);
                                ((TNTPrimed) tnt2).setFuseTicks(10);

                                p.sendMessage(Prefix + "You got very very unlucky and rolled a §4" + MineEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "You better survive! :)");

                                break;
                            default:
                                break;
                        }
                    }
                }, 70);
            }
            default -> {
            }
        }
    }



}
