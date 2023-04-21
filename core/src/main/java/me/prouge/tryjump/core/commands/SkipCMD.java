package me.prouge.tryjump.core.commands;

import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.listener.LobbyListener;
import me.prouge.tryjump.core.managers.ScoreboardManager;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.time.LocalTime;

public class SkipCMD implements CommandExecutor {

    @Inject
    private GameImpl game;

    @Inject
    private ChatWriter chatWriter;

    @Inject
    private LobbyListener lobbyListener;

    @Inject
    private ScoreboardManager scoreboardManager;

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (game.getGamePhase().equals(Phase.Lobby_with_countdown)) {
            lobbyListener.setSeconds(10);
        }


        if (game.getGamePhase() != Phase.Game_shop) {
            return false;
        }

        TryJumpPlayer tryPlayer = game.getTryPlayer((Player) sender);

        if (tryPlayer.isSkipped()) {
            chatWriter.print(tryPlayer, Message.LOBBY_SHOP_SKIPPED_ALREADY, null);
            return false;
        }
        tryPlayer.setSkipped(true);
        game.getPlayerArrayList().forEach(tp -> chatWriter.print(tp, Message.LOBBY_SHOP_SKIP, new String[][]{{
                "PLAYER", Bukkit.getPlayer(tryPlayer.getUniqueId()).getName()},
                {"PLAYER_PRO", String.valueOf(game.getPlayerArrayList().stream().filter(TryJumpPlayer::isSkipped).count())},
                {"MAX_PLAYERS", String.valueOf(game.getPlayerArrayList().size())}}));
        game.skipShop();
        return false;
    }
}
