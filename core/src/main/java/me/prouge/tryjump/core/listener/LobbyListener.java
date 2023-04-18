package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.events.GameStartEvent;
import me.prouge.tryjump.core.events.LobbyStartEvent;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.managers.ScoreboardManager;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;

public class LobbyListener implements Listener {

    @Inject
    private GameImpl game;

    @Inject
    private ChatWriter chatWriter;

    @Inject
    private TryJump plugin;

    @Inject
    private ScoreboardManager scoreboardManager;


    @EventHandler
    public void onLobbyStartEvent(final LobbyStartEvent event) {

        new BukkitRunnable() {
            int seconds = plugin.getConfig().getInt("lobbyDuration");

            @Override
            public void run() {
                if (event.isCancelled()) {
                    game.getPlayerArrayList().forEach(tp -> tp.getPlayer().sendMessage("Canceld"));
                    cancel();
                    return;
                }
                switch (seconds) {
                    case 60:
                    case 50:
                    case 40:
                    case 30:
                    case 20:
                    case 10:
                    case 9:
                    case 8:
                    case 7:
                    case 6:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        game.getPlayerArrayList().forEach(p -> chatWriter.print(p, Message.LOBBY_COUNTDOWN,
                                new String[][]{{"SECONDS", String.valueOf(seconds)}}));
                        break;
                    case 0:
                        for (int i = 0; i < 100; i++) {
                            game.getPlayerArrayList().forEach(p -> p.getPlayer().sendMessage(""));
                        }
                        game.getPlayerArrayList().forEach(p -> {
                            chatWriter.print(p, Message.LOBBY_TELEPORT, null);
                            p.teleportToSpawn();
                        });

                        Bukkit.getPluginManager().callEvent(new GameStartEvent(false));
                        scoreboardManager.createScoreboard(game.getPlayerArrayList(), game.getMapLength());
                        game.setTablist();
                        cancel();
                }
                seconds--;
            }
        }.runTaskTimer(plugin, 0, 20L);

    }

}
