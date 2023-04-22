package me.prouge.tryjump.core.game;

import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Game {

    void startCountdown();

    void addPlayer(final Player player);
    void sendPlayerJoinMessage(final Player player);
    void sendPlayerQuitMessage(final Player player);
    void removePlayer(final UUID uuid);
    Phase getGamePhase();

    TryJumpPlayer getTryPlayer(final Player player);

    void setGamePhase(final Phase gamePhase);



}
