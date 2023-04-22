package me.prouge.tryjump.core.listener;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.executor.ServerSelectorType;
import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.events.deathmatch.DeatchmatchDeathEvent;
import me.prouge.tryjump.core.events.game.GameDeathEvent;
import me.prouge.tryjump.core.events.WorldSpawnModuleEvent;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.shop.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

import javax.inject.Inject;

import static me.prouge.tryjump.core.game.Phase.Game_running;

//@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerListener implements Listener {
    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry()
            .getFirstService(IPlayerManager.class);
    @Inject
    private Shop shop;
    @Inject
    private GameImpl gameImpl;
    @Inject
    private TryJump plugin;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        e.getPlayer().getInventory().clear();
        e.getPlayer().setLevel(0);
        e.getPlayer().setMaxHealth(20);
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
                    Bukkit.getPluginManager().callEvent(new GameDeathEvent(player, true));
                }
        }
        if (gameImpl.getGamePhase() == Game_running) {
            tryPlayer.updateWalkedDistance(-(e.getFrom().getX() - e.getTo().getX()));
        }

    }

    @EventHandler
    public void pressurePlatePress(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.PHYSICAL)) {
            Player player = e.getPlayer();
            if (e.getClickedBlock().getType().equals(Material.GOLD_PLATE)
                    && gameImpl.getGamePhase().equals(Game_running)
                    && e.getClickedBlock().getLocation().subtract(0, 1, 0).getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
                this.gameImpl.updateModule(player, e.getClickedBlock().getLocation());
                Bukkit.getPluginManager().callEvent(new WorldSpawnModuleEvent(player, e.getClickedBlock().getLocation()));
                e.getClickedBlock().setType(Material.AIR);
                e.getClickedBlock().getState().update();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    e.getClickedBlock().setType(Material.AIR);
                    e.getClickedBlock().getState().update();
                }, 1L);
            }
        } else if (e.getPlayer().getItemInHand().getType().equals(Material.INK_SACK)) {
            if (gameImpl.getGamePhase().equals(Phase.Lobby_without_countdown) || gameImpl.getGamePhase().equals(Phase.Lobby_with_countdown)) {
                playerManager.getPlayerExecutor(e.getPlayer().getUniqueId()).connectToTask("Lobby", ServerSelectorType.HIGHEST_PLAYERS);
                return;
            }

            Bukkit.getPluginManager().callEvent(new GameDeathEvent(e.getPlayer(), false));
        } else if (e.getPlayer().getItemInHand().getType().equals(Material.CHEST)) {
            this.shop.openShop(e.getPlayer());
        }
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        TryJumpPlayer tryJumpPlayer = gameImpl.getTryPlayer((Player) e.getEntity());
        Player player = (Player) e.getEntity();

        if (gameImpl.getGamePhase() == Phase.Game_pvp) {
            if (System.currentTimeMillis() - tryJumpPlayer.getTimeStamp() <= 3000) {
                e.setCancelled(true);
            }

            if (player.getHealth() <= e.getFinalDamage()) {
                e.setCancelled(true);
                if (e.getDamager() instanceof Player) {
                    Bukkit.getPluginManager().callEvent(new DeatchmatchDeathEvent(player,
                            (Player) e.getDamager()));
                } else {
                    Bukkit.getPluginManager().callEvent(new DeatchmatchDeathEvent(player, null));
                }

            }
        }

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Bukkit.broadcastMessage("§a" + event.getPlayer().getName() + " §8» §f" + event.getMessage());
    }
}
