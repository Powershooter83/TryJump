package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import javax.inject.Inject;

public class WorldSecurityListener implements Listener {

    @Inject
    private GameImpl gameManager;
    @Inject
    private TryJump plugin;

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        e.getBlock().getDrops().clear();
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.STAINED_CLAY && gameManager.getGamePhase().equals(Phase.Game_running)) {
            gameManager.getTryPlayer(e.getPlayer()).setHelpBlockLocation(e.getBlock().getLocation());
            return;
        }
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp) && e.getBlock().getType() != Material.TNT) {
            e.setCancelled(true);
        }
        if (gameManager.getGamePhase().equals(Phase.Game_pvp) && e.getBlock().getType() == Material.TNT) {
            Block block = e.getBlock();
            block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
            block.setType(Material.AIR);
        }

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp) && !gameManager.getGamePhase().equals(Phase.Game_running) &&
                e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            Player player = (Player) e.getEntity();
            player.teleport((Location) plugin.getConfig().get("Lobby"));
        }

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
    public void onItemDrop(PlayerDropItemEvent e) {
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (gameManager.getGamePhase().equals(Phase.Game_running) && e.getCurrentItem().getType().equals(Material.STAINED_CLAY)) {
            return;
        }
        if (!gameManager.getGamePhase().equals(Phase.Game_pvp) && !gameManager.getGamePhase().equals(Phase.Game_shop)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler
    public void onItemPickUpEvent(PlayerPickupItemEvent e) {
        if (gameManager.getGamePhase().equals(Phase.Game_running)) {
            e.setCancelled(true);
        }
    }

}
