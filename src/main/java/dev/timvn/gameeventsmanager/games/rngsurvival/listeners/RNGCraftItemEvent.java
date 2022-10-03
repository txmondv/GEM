package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival.timeRollShouldLast;


public class RNGCraftItemEvent implements Listener {

    GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);

    @EventHandler
    private void onCraft(CraftItemEvent e) {

        // event is 1/3 to be triggered
        if ((int) (Math.random() * 3) + 1 != 1) {
            return;
        }

        // FIRST copy item data, then remove it (otherwise amount would be 0, you also couldn't just set it to 1 afterwards because the player can craft up to 64 at a time!)
        final ItemStack itemStack = Objects.requireNonNull(e.getInventory().getResult()).clone();

        // check if the item has durability
        if (itemStack.getItemMeta() instanceof Damageable) {

            // remove the crafted item from the inventory
            e.getInventory().getResult().setAmount(0);

            if (itemStack.getType().getMaxDurability() == 0) {
                return;
            }


            final Player p = (Player)e.getViewers().get(0);
            final int result = RNGSurvival.roll(p);
            if (result == -1) {
                return;
            }

            p.closeInventory();
            (new BukkitRunnable() {
                public void run() {
                    Damageable meta = (Damageable) itemStack.getItemMeta();
                    double maxDurability = itemStack.getType().getMaxDurability();
                    double damage = maxDurability - (maxDurability / 16.0 + maxDurability / 20.0 * (double)result);
                    if (damage < 0.0) {
                        damage = 0.0;
                    }

                    meta.setDamage((int)damage);
                    itemStack.setItemMeta(meta);

                    switch (result) {
                        case 20:
                            // concept: insane enchantments
                            if (Enchantment.DIG_SPEED.canEnchantItem(itemStack)) {
                                itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                            }

                            itemStack.getItemMeta().setUnbreakable(true);

                            if (Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(itemStack)) {
                                itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                            }
                            if (Enchantment.DAMAGE_ALL.canEnchantItem(itemStack)) {
                                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                            }

                            p.sendMessage(RNGSurvival.Prefix + "Amazing! You have rolled a §a" + result + "§7!");
                            p.sendMessage(RNGSurvival.Prefix + "Your item is unbreakable and super op!");
                            break;
                        case 19:
                            // concept: insane enchantments but one level lower
                            if (Enchantment.DIG_SPEED.canEnchantItem(itemStack)) {
                                itemStack.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
                            }
                            if (Enchantment.DURABILITY.canEnchantItem(itemStack)) {
                                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                            }
                            if (Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(itemStack)) {
                                itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                            }
                            if (Enchantment.DAMAGE_ALL.canEnchantItem(itemStack)) {
                                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
                            }

                            p.sendMessage(RNGSurvival.Prefix + "Very nice, you rolled a §a" + result + "§7!");
                            p.sendMessage(RNGSurvival.Prefix + "Your item got some pretty decent buffs.");
                            break;
                        case 18:
                            // concept:
                        case 17:
                            // concept:
                        case 16:
                            // concept:
                        case 15:
                            // concept:
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
                            // concept:
                        case 2:
                            // concept:
                        case 1:
                            // concept: take a random item from inventory

                            ItemStack itemInRandomSlot = Objects.requireNonNull(p.getInventory().getItem((int) ((Math.random() * (20 - 1)) + 1)));
                            itemInRandomSlot.setAmount(0);

                        default:
                            break;
                    }

                    p.getWorld().dropItem(p.getLocation(), itemStack).setPickupDelay(0);
                }
            }).runTaskLater(plugin, timeRollShouldLast);
        }
    }



}
