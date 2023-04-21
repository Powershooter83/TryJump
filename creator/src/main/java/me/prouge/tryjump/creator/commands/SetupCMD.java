package me.prouge.tryjump.creator.commands;

import me.prouge.tryjump.creator.TryJump;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class SetupCMD implements CommandExecutor {
    @Inject
    private TryJump plugin;

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
        File customYml = new File(plugin.getDataFolder() + "/locations.yml");
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);

        Player player = (Player) commandSender;
        if (args[0].equalsIgnoreCase("setlobby")) {
            customConfig.set("Lobby", player.getLocation());
            saveCustomYml(customConfig, customYml);
            player.sendMessage("Der Lobbyspawn wurde gesetzt!");
        } else if (args[0].equalsIgnoreCase("setspawn")) {
            customConfig.set("Spawns." + args[1], player.getLocation());
            saveCustomYml(customConfig, customYml);
            player.sendMessage("Der Spawn " + args[1] + " wurde gesetzt!");
        } else if (args[0].equalsIgnoreCase("setdm")) {
            customConfig.set("DeathMatchSpawns." + args[1], player.getLocation());
            saveCustomYml(customConfig, customYml);
            player.sendMessage("Der DeathMatch Spawn " + args[1] + " wurde gesetzt!");
        }
        return false;
    }

    private void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
        try {
            ymlConfig.save(ymlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
