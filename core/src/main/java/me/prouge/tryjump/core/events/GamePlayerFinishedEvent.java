package me.prouge.tryjump.core.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class GamePlayerFinishedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Player finisher;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
