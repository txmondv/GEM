package dev.timvn.gameeventsmanager.games.rngsurvival.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class CraftManager {

    private RNGGameManager gameManager;

    public CraftManager(RNGGameManager gameManager) {
        this.gameManager = gameManager;

        // configure the crafting recipes that should trigger the toolCraftEvent event
        toolSet.add(Material.WOODEN_SHOVEL);
        toolSet.add(Material.WOODEN_PICKAXE);
        toolSet.add(Material.WOODEN_AXE);
        toolSet.add(Material.WOODEN_HOE);

        toolSet.add(Material.STONE_SHOVEL);
        toolSet.add(Material.STONE_PICKAXE);
        toolSet.add(Material.STONE_AXE);
        toolSet.add(Material.STONE_HOE);

        toolSet.add(Material.GOLDEN_SHOVEL);
        toolSet.add(Material.GOLDEN_PICKAXE);
        toolSet.add(Material.GOLDEN_AXE);
        toolSet.add(Material.GOLDEN_HOE);

        toolSet.add(Material.IRON_SHOVEL);
        toolSet.add(Material.IRON_PICKAXE);
        toolSet.add(Material.IRON_AXE);
        toolSet.add(Material.IRON_HOE);

        toolSet.add(Material.DIAMOND_SHOVEL);
        toolSet.add(Material.DIAMOND_PICKAXE);
        toolSet.add(Material.DIAMOND_AXE);
        toolSet.add(Material.DIAMOND_HOE);

        toolSet.add(Material.FISHING_ROD);
        toolSet.add(Material.FLINT_AND_STEEL);
        toolSet.add(Material.SHEARS);
    }

    // create HasSets to store the blocks in
    private static Set<Material> toolSet = new HashSet<>();

    // method to tell the user if a block is part of set, if so, display the event key
    public static String checkCraftingEvent(ItemStack item) {

        if(toolSet.contains(item.getType())) {
            return "tool_craft_event";
        }


        else {
            return "None";
        }
    }

}
