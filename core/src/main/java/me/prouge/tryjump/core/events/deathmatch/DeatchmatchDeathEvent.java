package me.prouge.tryjump.core.events.deathmatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class DeatchmatchDeathEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Player victim;
    @Getter
    private final Player attacker;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
