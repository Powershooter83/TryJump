package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.events.WorldSpawnModuleEvent;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.module.MDifficulty;
import me.prouge.tryjump.core.module.MLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.inject.Inject;

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
        player.resetUnitDeaths();
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


}
