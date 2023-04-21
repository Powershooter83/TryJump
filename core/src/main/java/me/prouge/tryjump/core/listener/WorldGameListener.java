package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.events.WorldSpawnModuleEvent;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.module.MDifficulty;
import me.prouge.tryjump.core.module.MLoader;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.List;


public class WorldGameListener implements Listener {

    @Inject
    private GameImpl game;

    @Inject
    private MLoader loader;

    @EventHandler
    public void onModuleSpawnEvent(WorldSpawnModuleEvent event) {
        TryJumpPlayer player = null;
        for (TryJumpPlayer tp : game.getPlayerArrayList()) {
            if (tp.getPlayer() == event.getPlayer()) {
                player = tp;
            }
        }
        assert player != null;
        player.setWalkedDistanceUntilDeath(player.getWalkedDistance());
        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_STICKS, 1, 1);
        player.resetUnitDeaths();
        clearLag();

        for (ItemStack item : player.getPlayer().getInventory().getContents()) {
            if (item != null && item.getType() == Material.STAINED_CLAY) {
                player.getPlayer().getInventory().remove(item);
                player.getPlayer().updateInventory();
                break;
            }
        }

        if (player.getModuleId() <= 3) {
            loader.getModules().get(MDifficulty.EASY).get(player.getModuleId() - 1).paste("E", event.getPressurePlateLocation());
        }

        if (player.getModuleId() > 3 && player.getModuleId() <= 6) {
            loader.getModules().get(MDifficulty.MEDIUM).get(player.getModuleId() - 4).paste("E", event.getPressurePlateLocation());

        }
        if (player.getModuleId() > 6 && player.getModuleId() <= 9) {
            loader.getModules().get(MDifficulty.HARD).get(player.getModuleId() - 7).paste("E", event.getPressurePlateLocation());

        }
        if (player.getModuleId() == 10) {
            loader.getModules().get(MDifficulty.EXTREME).get(0).paste("E", event.getPressurePlateLocation());
        }
    }


    private void clearLag() {
        World world = Bukkit.getServer().getWorld("TryJump");
        List<org.bukkit.entity.Entity> entList = world.getEntities();

        for (Entity current : entList) {
            if (current instanceof Item) {
                current.remove();
            }
        }
    }

}
