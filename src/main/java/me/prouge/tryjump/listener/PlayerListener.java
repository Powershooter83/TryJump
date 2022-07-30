package me.prouge.tryjump.listener;

import me.prouge.tryjump.util.MessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;

public class PlayerListener implements Listener {

    @Inject private MessageHandler messageHandler;


    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e){
        e.getPlayer().sendMessage(messageHandler.getMessage("de", "PLAYER_JOIN_MESSAGE"));
    }




}
