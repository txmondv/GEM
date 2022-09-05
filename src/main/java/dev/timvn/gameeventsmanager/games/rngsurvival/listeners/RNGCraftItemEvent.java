package dev.timvn.gameeventsmanager.games.rngsurvival.listeners;

import dev.timvn.gameeventsmanager.GameEventsManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.BlockManager;

import dev.timvn.gameeventsmanager.games.rngsurvival.manager.CraftManager;
import dev.timvn.gameeventsmanager.games.rngsurvival.manager.RNGGameManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static dev.timvn.gameeventsmanager.games.rngsurvival.RNGSurvival.Prefix;


public class RNGCraftItemEvent implements Listener {

    GameEventsManager plugin = GameEventsManager.getPlugin(GameEventsManager.class);

    @EventHandler
    private void onCraft(CraftItemEvent e) {

        // event is 1/5 to be triggered
        if ((int) (Math.random() * 3) + 1 != 1) {
            return;
        }

        final ItemStack itemStack = Objects.requireNonNull(e.getInventory().getResult()).clone();
        if (itemStack.getItemMeta() instanceof Damageable) {
            if (itemStack.getType().getMaxDurability() == 0) {
                return;
            }

            e.setCancelled(true);

            final Player p = (Player)e.getViewers().get(0);
            final int result = RNGSurvival.roll(p);
            if (result == -1) {
                return;
            }

            e.getInventory().clear();
            p.closeInventory();
            (new BukkitRunnable() {
                public void run() {
                    Damageable meta = (Damageable)itemStack.getItemMeta();
                    double maxDurability = (double)itemStack.getType().getMaxDurability();
                    double damage = maxDurability - (maxDurability / 16.0 + maxDurability / 20.0 * (double)result);
                    if (damage < 0.0) {
                        damage = 0.0;
                    }

                    meta.setDamage((int)damage);
                    itemStack.setItemMeta((ItemMeta)meta);

                    switch (result) {
                        case 20:
                            // concept: add enchantments to the sword
                            if (Enchantment.DIG_SPEED.canEnchantItem(itemStack)) {
                                itemStack.addEnchantment(Enchantment.DIG_SPEED, 1);
                            }

                            if (Enchantment.DURABILITY.canEnchantItem(itemStack)) {
                                itemStack.addEnchantment(Enchantment.DURABILITY, 1);
                            }

                            if (Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(itemStack)) {
                                itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                            }

                            if (Enchantment.DAMAGE_ALL.canEnchantItem(itemStack)) {
                                itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                            }
                        case 19:
                            // concept:
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
                            break;
                        case 2:
                            // concept:
                        case 1:
                            // concept:
                        default:
                            break;
                    }

                    p.getWorld().dropItem(p.getLocation(), itemStack).setPickupDelay(0);
                }
            }).runTaskLater(plugin, 70L);
        }
    }



}
