package me.prouge.tryjump.core.game;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface Game {

    void startCountdown();
    void stopCountdown();

    void startGame();
    void startGameCountdown();
    void stopGame();

    void addPlayer(final Player player);
    void sendPlayerJoinMessage(final Player player);
    void sendPlayerQuitMessage(final Player player);
    void removePlayer(final UUID uuid);
    Phase getGamePhase();

    void setGamePhase(final Phase gamePhase);



}
