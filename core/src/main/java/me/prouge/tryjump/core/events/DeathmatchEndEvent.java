package me.prouge.tryjump.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class DeathmatchEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Getter
    private final TryJumpPlayer winner;

}
