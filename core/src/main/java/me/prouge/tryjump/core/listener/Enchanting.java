package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Inject;


public class Enchanting implements Listener {

    @Inject
    private GameImpl gameManager;


    @EventHandler
    public void onRightClickWithEnchantingTable(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.ENCHANTMENT_TABLE ||
                gameManager.getGamePhase() != Phase.Game_running) {
            return;
        }

        //TODO: Enchanting system
        event.setCancelled(true);
    }


}
