package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.events.*;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.managers.ScoreboardManager;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.ItemBuilder;
import me.prouge.tryjump.core.utils.Message;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.time.LocalTime;


public class GameListener implements Listener {

    @Inject
    private GameImpl game;

    @Inject
    private ChatWriter chatWriter;

    @Inject
    private TryJump plugin;

    @Inject
    private ScoreboardManager scoreboardManager;
    private boolean hasFinished = false;

    @EventHandler
    public void onGameDeathEvent(final GameDeathEvent event) {
        Player victim = event.getVictim();

        TryJumpPlayer tj = game.getTryPlayer(victim);
        tj.setWalkedDistance(tj.getWalkedDistanceUntilDeath());
        if (!event.isCooldown() && System.currentTimeMillis() - tj.getTimeStamp() <= 3000) {
            chatWriter.print(tj, Message.ITEM_INSTANT_DEATH_COOLDOWN, null);
            return;
        }

        victim.playSound(victim.getLocation(), Sound.NOTE_PLING, 1, 1);

        tj.setTimeStamp(System.currentTimeMillis());
        tj.getSpawnLocation().setYaw(-90);
        victim.teleport(tj.getSpawnLocation());

        String text = "§c✖✖✖";

        switch (tj.getUnitDeaths()) {
            case 0:
                text = "§c✖§7✖✖";
                break;
            case 1:
                text = "§c✖✖§7✖";
                break;
            case 2:
                text = "§c✖✖✖";
                break;
        }
        tj.updateUnitDeaths();

        if (tj.getUnitDeaths() == 3) {
            tj.addHelpBlock();
        }


        IChatBaseComponent emptyTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}");
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, emptyTitle);

        PacketPlayOutTitle length = new PacketPlayOutTitle(1, 19, 11);

        ((CraftPlayer) victim).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) victim).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatTitle));
        ((CraftPlayer) victim).getHandle().playerConnection.sendPacket(length);
    }

    @EventHandler
    public void onGameStartEvent(final GameStartEvent event) {
        game.setGamePhase(Phase.Game_starting);
        ItemStack itemStack = new ItemBuilder(Material.INK_SACK, 1, (byte) 1)
                .setName("§c§lInstant-Tod(TM) §7§o<Rechtsklick>")
                .toItemStack();

        game.getPlayerArrayList().forEach(tj -> tj.getPlayer().getInventory().setItem(4, itemStack));

        new BukkitRunnable() {
            int seconds = 10;

            @Override
            public void run() {
                switch (seconds) {
                    case 10:
                    case 5:
                    case 3:
                    case 2:
                    case 1:
                        game.getPlayerArrayList().forEach(p -> {
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.NOTE_BASS_GUITAR, 1, 1);
                            chatWriter.print(p, Message.GAME_COUNTDOWN, new String[][]{{"SECONDS", String.valueOf(seconds)}});

                        });
                        break;
                    case 0:
                        game.setGamePhase(Phase.Game_running);
                        game.getPlayerArrayList().forEach(p -> {
                            chatWriter.print(p, Message.GAME_GO, null);
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.LEVEL_UP, 1, 1);
                            Bukkit.getPluginManager().callEvent(new WorldSpawnModuleEvent(p.getPlayer(), p.getSpawnLocation().add(6, 0, 0)));
                        });
                        Bukkit.getScheduler().runTaskTimer(plugin,
                                () -> {
                                    if (game.getGamePhase() == Phase.Game_running) {
                                        scoreboardManager.updateScoreboard(game.getPlayerArrayList(), game.getMapLength());
                                        game.setTablist();
                                    } else {
                                        cancel();
                                    }

                                }, 0, 20);

                        cancel();
                        break;
                }
                seconds--;
            }
        }.runTaskTimer(plugin, 0, 20L);

    }

    @EventHandler
    public void onGamePlayerFinished(GamePlayerFinishedEvent event) {
        if (event.getFinisher() == null && !hasFinished) {
            playerFinish(true);
            return;
        }

        TryJumpPlayer tryp = game.getTryPlayer(event.getFinisher());
        tryp.getPlayer().getInventory().clear();

        if (tryp.getTotalUnitDeaths() == 0) {
            chatWriter.print(tryp, Message.MEGA_TOKEN_BOOST, null);
            tryp.addTokens(2000);
        }
        game.getPlayerArrayList().forEach(tp -> tp.getPlayer().playSound(tp.getPlayer().getLocation(), Sound.WITHER_DEATH, 1, 1));
        if (hasFinished) {
            return;
        }

        IChatBaseComponent emptyTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§6" + event.getFinisher().getName() + "\"}");
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§7hat das Ziel erreicht!" + "\"}");
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, emptyTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(1, 19, 11);

        game.getPlayerArrayList().forEach(tp -> {
            Player player = tp.getPlayer();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatTitle));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
        });
        hasFinished = true;
        playerFinish(false);

    }

    private void playerFinish(boolean force) {
        if (force) {
            shopPhase();
            return;
        }

        scoreboardManager.setTime(LocalTime.of(0, 0, 10));
        scoreboardManager.setFinished(true);
        Bukkit.getScheduler().runTaskLater(plugin, this::shopPhase, 10 * 20);
    }

    private void shopPhase() {
        System.out.println("SHOP_PHASE");
        game.setGamePhase(Phase.Game_shop);

        game.getPlayerArrayList().forEach(tp -> {
            Player player = tp.getPlayer();
            game.teleportPlayerToSpawn(player);
            player.getInventory().clear();
            player.getInventory().setItem(4, new ItemBuilder(Material.CHEST).setName("Shop").toItemStack());
            player.setLevel(tp.getTokens());
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            tp.setTimeStamp(0);
        });

        game.setTablist();
        scoreboardManager.createLobbyScoreboard(game.getPlayerArrayList());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getGamePhase() == Phase.Game_shop) {
                    scoreboardManager.updateLobbyScoreboard(game.getPlayerArrayList());
                    if (scoreboardManager.getTime().getSecond() == 0 && scoreboardManager.getTime().getMinute() == 0) {
                        Bukkit.getPluginManager().callEvent(new DeathmatchStartEvent());
                        cancel();
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

}
