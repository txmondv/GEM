package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.BlockManager;

import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
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
        Material BlockType = event.getBlock().getType();
        switch (eventType) {
            case "wood_event" -> {
                // make wood breaking a 1/3 chance to trigger the roll
                if ((int) (Math.random() * 3) + 1 != 1) {
                    break;
                }

                int woodEventRolledNumber = RNGSurvival.roll(p);

                // simply delay task by the time the roll should last
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {

                        Location spawnlocation = event.getBlock().getLocation();
                        spawnlocation.setY(spawnlocation.getY() + 1);
                        while(spawnlocation.getBlock().getType() != Material.AIR) {
                            spawnlocation.setY(spawnlocation.getY() + 1);
                        }

                        switch (woodEventRolledNumber) {
                            case 20:
                                // concept: player enters god mode
                                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400, 4, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 2400, 4, false, false, false));

                                p.sendMessage(Prefix + "You're blessed! You rolled a §c" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "The gods hear your call and grant you their powers for 2 minutes!");
                                break;
                            case 19:
                                // concept: tree drops a block of diamond
                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND_BLOCK,1 ));
                                event.setDropItems(false);

                                p.sendMessage(Prefix + "You got incredibly lucky and rolled a §c" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Your log turned into a §bdiamond block§7!");
                                break;
                            case 18:
                                // concept: drop DURABLE, super efficient iron axe
                                ItemStack SuperIronAxe = new ItemStack(Material.IRON_AXE, 1);
                                SuperIronAxe.addEnchantment(Enchantment.DURABILITY, 3);
                                SuperIronAxe.addEnchantment(Enchantment.DIG_SPEED, 3);
                                SuperIronAxe.addEnchantment(Enchantment.MENDING, 1);

                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), SuperIronAxe);

                                p.sendMessage(Prefix + "You got unbelievably lucky and rolled a §a" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "As a reward, your tree dropped a super iron axe!");
                                break;
                            case 17:
                                // concept: Drop 20 to 40 Oak Logs
                                int newOakLogsAmount = (int) ((Math.random() * (40 - 20)) + 20);
                                ItemStack newLogs = new ItemStack(BlockType, newOakLogsAmount);
                                p.sendMessage(String.valueOf(BlockType));
                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), newLogs);
                                event.setDropItems(false);

                                p.sendMessage(Prefix + "You got amazingly lucky and rolled a §a" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "As luck should be rewarded, you get §a" + newOakLogsAmount + " §7instead of 1 log!");
                                break;
                            case 16:
                                // concept: 3 tree allays

                                Allay treeAllay = (Allay) p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ALLAY);
                                treeAllay.setTarget(p);
                                treeAllay.setCustomName("§aThe Allay of the tree");
                                treeAllay.setCustomNameVisible(true);

                                p.sendMessage(Prefix + "You rolled a §a" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Therefore, the Allay of the tree companion you!");
                                break;
                            case 15:
                                // concept: Give Haste for a while
                                int AmountOfTicksThatHasteIsGranted = (int) ((Math.random() * (4500 - 1500)) + 1500);
                                // 20 Ticks = 1 Second :)
                                p.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(AmountOfTicksThatHasteIsGranted, 3));

                                p.sendMessage(Prefix + "Somehow you got lucky and rolled a §a" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Because your tree likes this number, you get " + AmountOfTicksThatHasteIsGranted / 20 + " seconds of Haste III!");
                                break;
                            case 14:
                                // concept: spawn iron golem


                                IronGolem theguard = (IronGolem) p.getWorld().spawnEntity(spawnlocation, EntityType.IRON_GOLEM);
                                theguard.setGlowing(true);
                                theguard.setCustomName("§aGuardian of the woods");
                                theguard.setCustomNameVisible(true);


                                p.sendMessage(Prefix + "You got kind of lucky and rolled a §6" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Be guarded during your further farming!");
                                break;
                            case 13:
                                // concept: drop extra apples
                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE, (int) ((Math.random() * (8 - 3)) + 3)));

                                p.sendMessage(Prefix + "You rolled a §6" + woodEventRolledNumber + "§7.");
                                p.sendMessage(Prefix + "This log contained an apple to be dropped, take what you can carry!");
                                break;
                            case 12:
                                // concept: drop coal
                                event.setDropItems(false);
                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.COAL, (int) ((Math.random() * (8 - 3)) + 3)));

                                p.sendMessage(Prefix + "That's a §6" + woodEventRolledNumber + "§7.");
                                p.sendMessage(Prefix + "Due to industrialization the log got turned into coal!");
                                break;
                            case 11:
                                // concept: block respawns AND drops
                                event.setCancelled(true);
                                p.getInventory().addItem(new ItemStack(event.getBlock().getType(), 1));

                                p.sendMessage(Prefix + "We take that, you rolled a §6" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Just keep going, here is another block.");
                                break;
                            case 10:
                                // concept: block simply respawns
                                p.getWorld().getBlockAt(event.getBlock().getLocation()).setType(BlockType);

                                p.sendMessage(Prefix + "Hm, can't really say much about a §6" + woodEventRolledNumber + "§7...");
                                p.sendMessage(Prefix + "Let's just forget it.");
                                break;
                            case 9:
                                // concept: drop paper
                                event.setDropItems(false);
                                p.getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.PAPER, (int) ((Math.random() * (8 - 3)) + 3)));

                                p.sendMessage(Prefix + "You rolled a §6" + woodEventRolledNumber + "§7.");
                                p.sendMessage(Prefix + "Due to industrialization the log got turned into paper!");
                                break;
                            case 8:
                                // concept:
                            case 7:
                                // concept:
                            case 6:
                                // concept:
                            case 5:
                                // concept: set the tree on fire
                            case 4:
                                // concept: bad effects (many)
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) ((Math.random() * (2400 - 600)) + 600), 2, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) ((Math.random() * (2400 - 600)) + 600), 2, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (int) ((Math.random() * (2400 - 600)) + 600), 2, false, false, false));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, (int) ((Math.random() * (2400 - 600)) + 600), 1, false, false, false));

                                p.sendMessage(Prefix + "You got very unlucky and rolled a §c" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Seemingly, the tree master didn't want you to break that block and cussed a little...");
                                break;
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
                                // concept: spawn warden
                            case 1:
                                // concept: players health is set to 3 hearts (6hp), monster army spawns, and he gets slowness (?)
                                p.setHealth(6);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 69, false, false, false));

                                p.getWorld().spawnEntity(spawnlocation, EntityType.SKELETON);
                                p.getWorld().spawnEntity(spawnlocation, EntityType.SKELETON);
                                p.getWorld().spawnEntity(spawnlocation, EntityType.SKELETON);

                                p.getWorld().spawnEntity(spawnlocation, EntityType.CREEPER);

                                p.getWorld().spawnEntity(spawnlocation, EntityType.ZOMBIE);
                                p.getWorld().spawnEntity(spawnlocation, EntityType.ZOMBIE);
                                p.getWorld().spawnEntity(spawnlocation, EntityType.ZOMBIE);

                                p.sendMessage(Prefix + "Your luck is (not so) incredible, you rolled a §c" + woodEventRolledNumber + "§7!");
                                p.sendMessage(Prefix + "Deal with your destiny!");
                                break;
                            default:
                                break;
                        }
                    }
                }, timeRollShouldLast);

            }
            case "mine_event" -> {
                // make mining event 1/10
                if ((int) (Math.random() * 10) + 1 != 1) {
                    break;
                }
                int MineEventRolledNumber = RNGSurvival.roll(p);


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
                                int tempRandomNumber3 = (int) ((Math.random() * (400 - 200)) + 200);
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
                                p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.SKELETON);
                                p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.SKELETON);
                                p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.SKELETON);

                                p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.CREEPER);

                                p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ZOMBIE);
                                p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ZOMBIE);
                                p.getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.ZOMBIE);

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
                }, timeRollShouldLast);
            }
            default -> {
            }
        }
    }



}
