package me.prouge.tryjump.core.commands;

import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.shop.Shop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class TestCMD implements CommandExecutor {
    @Inject
    private Shop shop;

    @Inject
    private TryJump plugin;

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
        Player player = (Player) commandSender;

        if (args.length == 0) {
            shop.openShop(player);
            return false;
        }


        if (args[0].equalsIgnoreCase("setlobby")) {
            plugin.getConfig().set("Lobby", player.getLocation());
            plugin.saveConfig();
        } else if (args[0].equalsIgnoreCase("setspawn")) {
            plugin.getConfig().set("Spawns." + args[1], player.getLocation());
            plugin.saveConfig();
            player.sendMessage("Der Spawn " + args[1] + " wurde gesetzt!");
        } else if (args[0].equalsIgnoreCase("setdm")) {
            plugin.getConfig().set("DeathMatchSpawns." + args[1], player.getLocation());
            plugin.saveConfig();
            player.sendMessage("Der DeathMatch Spawn " + args[1] + " wurde gesetzt!");
        }
        return false;
    }
}
