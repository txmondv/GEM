package dev.timvn.gameeventsmanager.games.rngsurvival.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.HashSet;
import java.util.Set;

public class BlockManager {

    private RNGGameManager gameManager;

    public BlockManager(RNGGameManager gameManager) {
        this.gameManager = gameManager;

        // configure the blocks that should trigger the woodBreak event
        woodBreak.add(Material.OAK_LOG);
        woodBreak.add(Material.BIRCH_LOG);
        woodBreak.add(Material.SPRUCE_LOG);
        woodBreak.add(Material.ACACIA_LOG);
        woodBreak.add(Material.JUNGLE_LOG);
        woodBreak.add(Material.DARK_OAK_LOG);
        woodBreak.add(Material.MANGROVE_LOG);
        woodBreak.add(Material.CRIMSON_STEM);
        woodBreak.add(Material.WARPED_STEM);

        mineBreak.add(Material.STONE);
        mineBreak.add(Material.DEEPSLATE);

    }

    // create HasSets to store the blocks in
    private static Set<Material> woodBreak = new HashSet<>();
    private static Set<Material> mineBreak = new HashSet<>();

    // method to tell the user if a block is part of set, if so, display the event key
    public static String checkEvent(Block block) {

        if(woodBreak.contains(block.getType())) {
            return "wood_event";
        } else if(mineBreak.contains(block.getType())) {
            return "mine_event";
        }


        else {
            return "None";
        }
    }

}
