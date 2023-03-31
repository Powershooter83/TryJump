package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.managers.GameManager;
import me.prouge.tryjump.core.managers.Phase;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import javax.inject.Inject;

public class WorldSecurityListener implements Listener {

    @Inject
    private GameManager gameManager;

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp) && e.getBlock().getType() != Material.TNT) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerMeeterChange(FoodLevelChangeEvent e) {
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp)) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp)) {
            event.setCancelled(true);
        }
    }


}
