package me.prouge.tryjump.core.listener;

import lombok.RequiredArgsConstructor;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.shop.Shop;
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

import static me.prouge.tryjump.core.game.Phase.Game_running;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerListener implements Listener {
    private final Shop shop;
    private final GameImpl gameImpl;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        gameImpl.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        e.setQuitMessage("");
        gameImpl.removePlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        final TryJumpPlayer tryPlayer = gameImpl.getTryPlayer(player);
        switch (gameImpl.getGamePhase()) {
            case Game_starting:
                player.teleport(e.getFrom());
            case Game_running:
                if (player.getLocation().getY() < 55) {
                    this.gameImpl.teleportPlayer(player);
                }
        }
        tryPlayer.updateWalkedDistance(- (e.getFrom().getX() - e.getTo().getX()));
    }

    @EventHandler
    public void pressurePlatePress(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            Player player = e.getPlayer();
            if (e.getClickedBlock().getType().equals(Material.GOLD_PLATE)
                    && gameImpl.getGamePhase().equals(Game_running)
                    && e.getClickedBlock().getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
                this.gameImpl.updateModule(player, e.getClickedBlock().getLocation());
                this.gameImpl.spawnModule(player, e.getClickedBlock().getLocation());
                e.getClickedBlock().setType(Material.AIR);
                e.getClickedBlock().getState().update();
            }
        } else if (e.getPlayer().getItemInHand().getType().equals(Material.INK_SACK)) {
            gameImpl.instantDeath(e.getPlayer());
        } else if (e.getPlayer().getItemInHand().getType().equals(Material.CHEST)) {
            this.shop.openShop(e.getPlayer());
        }
    }


}
