package me.prouge.tryjump.core.commands;

import me.prouge.tryjump.core.TryJump;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class TestCMD implements CommandExecutor {

    @Inject
    private TryJump plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        if (args[0].equalsIgnoreCase("setspawn")) {
            plugin.getConfig().set("Lobby", player.getLocation());
            plugin.saveConfig();
        } else {
            plugin.getConfig().set("Spawns." + args[0], player.getLocation());
            plugin.saveConfig();
            player.sendMessage("Der Spawn " + args[0] + " wurde gesetzt!");
        }


        return false;
    }
}
