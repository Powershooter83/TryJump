package me.prouge.tryjump.core.commands;

import me.prouge.tryjump.core.events.GameDeathEvent;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class HelpBlockResetCMD implements CommandExecutor {

    @Inject
    private GameImpl game;


    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (game.getGamePhase() != Phase.Game_running) {
            return false;
        }
        TryJumpPlayer tryPlayer = game.getTryPlayer((Player) sender);

        if (tryPlayer.getHelpBlockLocation() != null && tryPlayer.getUnitDeaths() >= 3) {
            Block block = tryPlayer.getHelpBlockLocation().getBlock();
            block.setType(Material.AIR);
            tryPlayer.setHelpBlockLocation(null);
            tryPlayer.addHelpBlock();
            Bukkit.getPluginManager().callEvent(new GameDeathEvent(tryPlayer.getPlayer(), true));
        }
        return false;
    }
}
