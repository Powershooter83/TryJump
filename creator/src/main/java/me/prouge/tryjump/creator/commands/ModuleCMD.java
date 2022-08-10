package me.prouge.tryjump.creator.commands;

import me.prouge.tryjump.creator.module.MDifficulty;
import me.prouge.tryjump.creator.module.MSaver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModuleCMD implements CommandExecutor {

    @Inject
    private MSaver mSaver;

    private HashMap<Player, List<Location>> playerListHashMap = new HashMap<>();
    private HashMap<Player, String> nameToChat = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage("");
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("tryjump.setup")) {
            return false;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("setup")) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }

            player.sendMessage("§8§m--------------------------------------------");
            player.sendMessage("§8» §6Module Creator");
            player.sendMessage("§7Nutze folgende Befehle, um dein Module zu erstellen:");
            player.sendMessage("");
            player.sendMessage("§6➀ §7/module pos1 §8[§6Setzt die erste Position deines Modules!§8]");
            player.sendMessage("§6➁ §7/module pos2 §8[§6Setzt die zweite Position deines Modules!§8]");
            player.sendMessage("§6➂ §7/module save §8[§6Speichert das Module relativ zu deiner Position!§8]");
            player.sendMessage("§6➃ §7/module name §8<name> §8[§6Speichert den Module namen!§8]");
            player.sendMessage("§6➄ §7/module difficulty §8<Easy, Medium, Hard, Extreme>");
            player.sendMessage("§8§m--------------------------------------------");
            return false;
        }
        if (args[0].equalsIgnoreCase("pos1")) {
            player.sendMessage("§8» §6Module Creator §7| Die Position §6Pos1 §7wurde erfolgreich gespeichert!");
            this.playerListHashMap.computeIfAbsent(player, k -> new ArrayList<>());
            this.playerListHashMap.get(player).add(player.getLocation());
        }
        if (args[0].equalsIgnoreCase("pos2")) {
            player.sendMessage("§8» §6Module Creator §7| Die Position §6Pos2 §7wurde erfolgreich gespeichert!");
            this.playerListHashMap.computeIfAbsent(player, k -> new ArrayList<>());
            this.playerListHashMap.get(player).add(player.getLocation());
        }
        if (args[0].equalsIgnoreCase("save")) {
            player.sendMessage("§8» §6Module Creator §7| Das Module wurde relativ zu deiner Position gespeichert!");
            this.playerListHashMap.get(player).add(player.getLocation());
        }
        if (args[0].equalsIgnoreCase("name")) {
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (args.length - 1 != i) {
                    name.append(args[i]).append(" ");
                } else {
                    name.append(args[i]);
                }
            }
            player.sendMessage("§8» §6Module Creator §7| Das Module wurde " + name + " §7benannt.");
            this.nameToChat.put(player, name.toString());
        }
        if (args[0].equalsIgnoreCase("difficulty")) {
            if (args[1].equalsIgnoreCase("easy") ||
                    args[1].equalsIgnoreCase("medium") ||
                    args[1].equalsIgnoreCase("hard") ||
                    args[1].equalsIgnoreCase("extreme")) {
                player.sendMessage("§8» §6Module Creator §7| Das Module wurde fertiggestellt!");

                mSaver.saveModule(this.playerListHashMap.get(player).get(0),
                        this.playerListHashMap.get(player).get(1)
                        , this.playerListHashMap.get(player).get(2),
                        this.nameToChat.get(player),
                        Enum.valueOf(MDifficulty.class, args[1].toUpperCase()));
            } else {
                player.sendMessage("§8» §6Module Creator §7| Folgende Schwierigkeiten gibt es: §8<Easy, Medium, Hard, Extreme>");
            }
        }
        return false;
    }
}
