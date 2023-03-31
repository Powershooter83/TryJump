package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.managers.GameManager;
import me.prouge.tryjump.core.managers.Phase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

;


public class PlayerListener implements Listener {
    @Inject
    private GameManager gameManager;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        gameManager.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage("");
        gameManager.removePlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        switch (gameManager.getGamePhase()) {
            case Game_starting:
                player.teleport(e.getFrom());
            case Game_running:
                if (player.getLocation().getY() < 55) {
                    this.gameManager.teleportPlayer(player);
                }
        }
    }

    @EventHandler
    public void pressurePlatePress(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            Player player = e.getPlayer();
            if (e.getClickedBlock().getType().equals(Material.GOLD_PLATE)
                    && gameManager.getGamePhase().equals(Phase.Game_running)
                    && e.getClickedBlock().getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
                this.gameManager.updateModule(player, e.getClickedBlock().getLocation());
                this.gameManager.spawnModule(player, e.getClickedBlock().getLocation());
                e.getClickedBlock().setType(Material.AIR);
                e.getClickedBlock().getState().update();
            }
        } else if (e.getPlayer().getItemInHand().getType().equals(Material.INK_SACK)) {
            this.gameManager.instantDeath(e.getPlayer());
        }
    }


}
