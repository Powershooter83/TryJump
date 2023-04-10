package me.prouge.tryjump.core.game;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.managers.ScoreboardManager;
import me.prouge.tryjump.core.module.MDifficulty;
import me.prouge.tryjump.core.module.MLoader;
import me.prouge.tryjump.core.module.Module;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.ItemBuilder;
import me.prouge.tryjump.core.utils.Message;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static me.prouge.tryjump.core.utils.Message.GAME_COINS_ADD;

@Singleton
public class GameImpl implements Game {
    private final ChatWriter chatWriter;
    private final ScoreboardManager scoreboardManager;
    @Getter
    private final ArrayList<TryJumpPlayer> playerArrayList = new ArrayList<>();
    @Getter
    private final ArrayList<Location> spawnLocations = new ArrayList<>();

    private final TryJump plugin;
    private final MLoader loader;
    private int mapLength = 0;
    private boolean hasRunningActionbar = false;
    @Getter
    @Setter
    private Phase gamePhase = Phase.Lobby_without_countdown;


    @Inject
    public GameImpl(final ChatWriter chatWriter,
                    final ScoreboardManager scoreboardManager, final MLoader loader, final TryJump plugin) {
        this.chatWriter = chatWriter;
        this.scoreboardManager = scoreboardManager;
        this.loader = loader;
        this.plugin = plugin;

        this.mapLength = loader.getModules().values().stream()
                .flatMap(List::stream)
                .mapToInt(Module::getMaxLength)
                .sum();
    }


    @Override
    public void startCountdown() {
        new BukkitRunnable() {
            int seconds = 5;

            @Override
            public void run() {
                switch (seconds) {
                    case 60:
                    case 50:
                    case 40:
                    case 30:
                    case 20:
                    case 10:
                    case 9:
                    case 8:
                    case 7:
                    case 6:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        playerArrayList.forEach(p -> chatWriter.print(p, Message.LOBBY_COUNTDOWN,
                                new String[][]{{"SECONDS", String.valueOf(seconds)}}));
                        break;
                    case 0:
                        for (int i = 0; i < 100; i++) {
                            playerArrayList.forEach(p -> p.getPlayer().sendMessage(""));
                        }
                        playerArrayList.forEach(p -> {
                            chatWriter.print(p, Message.LOBBY_TELEPORT, null);
                            p.teleportToSpawn();
                        });
                        startGameCountdown();
                        scoreboardManager.createScoreboard(playerArrayList, mapLength);
                        setTablist();
                        cancel();
                }
                seconds--;
            }
        }.runTaskTimer(plugin, 0, 20L);


    }

    @Override
    public void stopCountdown() {

    }

    @Override
    public void startGame() {

    }

    @Override
    public void startGameCountdown() {
        setGamePhase(Phase.Game_starting);
        ItemStack itemStack = new ItemBuilder(Material.INK_SACK, 1)
                .setName("§c§lInstant-Tod(TM) §7§o<Rechtsklick>")
                .toItemStack();

        playerArrayList.forEach(tj -> tj.getPlayer().getInventory().setItem(4, itemStack));

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
                        playerArrayList.forEach(p -> chatWriter.print(p, Message.GAME_COUNTDOWN,
                                new String[][]{{"SECONDS", String.valueOf(seconds)}}));
                        break;
                    case 0:
                        setGamePhase(Phase.Game_running);
                        playerArrayList.forEach(p -> {
                            chatWriter.print(p, Message.GAME_GO, null);
                            spawnModule(p.getPlayer(), p.getSpawnLocation().add(6, 0, 0));
                        });
                        Bukkit.getScheduler().runTaskTimer(plugin,
                                () -> {
                                    scoreboardManager.updateScoreboard(playerArrayList, mapLength);
                                    setTablist();
                                }, 0, 20);

                        cancel();
                        break;
                }
                seconds--;
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    @Override
    public void stopGame() {

    }

    @Override
    public void addPlayer(final Player player) {
        if (spawnLocations.size() == 0) {
            this.spawnLocations.addAll(plugin.getConfig().getConfigurationSection("Spawns").getValues(false).values().stream()
                    .map(Location.class::cast)
                    .collect(Collectors.toList()));
        }

        String language = player.spigot().getLocale().substring(0, player.spigot().getLocale().lastIndexOf('_'));
        List<String> languageFiles = Arrays.asList(Objects.requireNonNull(new File(plugin.getDataFolder().getPath() + "/languages").list()));
        if (!languageFiles.contains(language + ".yml")) {
            language = "de";
        }
        TryJumpPlayer tryPlayer = new TryJumpPlayer(language, player.getUniqueId(), spawnLocations.remove(0));

        this.playerArrayList.add(tryPlayer);
        sendPlayerJoinMessage(player);
        sendActionbar(tryPlayer);
        checkGamePhase();

        player.teleport((Location) plugin.getConfig().get("Lobby"));
    }

    @Override
    public void sendPlayerJoinMessage(final Player player) {
        this.playerArrayList.forEach(p -> chatWriter.print(p, Message.PLAYER_JOIN_MESSAGE,
                new String[][]{{"PLAYER", player.getName()}}));
    }

    @Override
    public void sendPlayerQuitMessage(final Player player) {
        this.playerArrayList.forEach(p -> chatWriter.print(p, Message.PLAYER_QUIT_MESSAGE,
                new String[][]{{"PLAYER", player.getName()}}));
    }

    @Override
    public void removePlayer(final UUID uuid) {
        this.playerArrayList.stream()
                .filter(tryPlayer -> tryPlayer.getUniqueId() == uuid)
                .findFirst()
                .ifPresent(toRemove -> {
                    spawnLocations.add(toRemove.getSpawnLocation());
                    this.playerArrayList.remove(toRemove);
                    sendPlayerQuitMessage(Bukkit.getPlayer(uuid));
                    checkGamePhase();
                });
    }

    public TryJumpPlayer getTryPlayer(Player player) {
        return this.playerArrayList.stream()
                .filter(tp -> tp.getPlayer().getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }


    public void teleportPlayer(final Player player) {
        this.playerArrayList.stream()
                .filter(tp -> tp.getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .ifPresent(tp -> {
                    int walkedDistance = player.getLocation().getBlockX() - tp.getSpawnLocation().getBlockX();
                    tp.updateWalkedDistance(-walkedDistance);
                    tp.teleportToSpawn();
                });
    }


    private void sendActionbar(TryJumpPlayer p) {
        chatWriter.sendActionbar(p, Message.LOBBY_ACTIONBAR,
                new String[][]{{"CURRENT_PLAYERS", String.valueOf(this.playerArrayList.size())},
                        {"MAX_PLAYERS", "10"}, {"MIN_PLAYERS", "2"}});
        if (hasRunningActionbar) {
            return;
        }
        hasRunningActionbar = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getGamePhase().equals(Phase.Lobby_with_countdown) || getGamePhase().equals(Phase.Lobby_without_countdown)) {
                    playerArrayList.forEach(p -> {
                        chatWriter.sendActionbar(p, Message.LOBBY_ACTIONBAR,
                                new String[][]{{"CURRENT_PLAYERS", String.valueOf(playerArrayList.size())},
                                        {"MAX_PLAYERS", "10"}, {"MIN_PLAYERS", "2"}});
                    });
                }
                if (getGamePhase().equals(Phase.Game_starting) || getGamePhase().equals(Phase.Game_running)) {
                    playerArrayList.forEach(p -> {
                        if (p.getModuleId() <= 3) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.EASY, p.getModuleId() - 1)},
                                            {"DIFFICULTY", "EASY"}});
                        }

                        if (p.getModuleId() > 3 && p.getModuleId() <= 6) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.MEDIUM, p.getModuleId() - 4)},
                                            {"DIFFICULTY", "MEDIUM"}});
                        }
                        if (p.getModuleId() > 6 && p.getModuleId() <= 9) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.HARD, p.getModuleId() - 7)},
                                            {"DIFFICULTY", "HARD"}});
                        }
                        if (p.getModuleId() == 10) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.EXTREME, 0)},
                                            {"DIFFICULTY", "EXTREME"}});
                        }
                    });


                }

            }
        }.runTaskTimer(plugin, 0, 40L);
    }


    public void teleportPlayerToSpawn(Player player) {
        player.teleport((Location) plugin.getConfig().get("Lobby"));
    }

    public void checkGamePhase() {
        if (this.playerArrayList.size() >= 2 && this.getGamePhase() == Phase.Lobby_without_countdown) {
            this.setGamePhase(Phase.Lobby_with_countdown);
            startCountdown();
        }
        if (this.playerArrayList.size() < 2 && this.getGamePhase() == Phase.Lobby_with_countdown) {
            this.setGamePhase(Phase.Lobby_without_countdown);
        }
    }

    public void instantDeath(Player player) {
        this.playerArrayList.forEach(tj -> {
            if (tj.getPlayer().equals(player)) {
                tj.getSpawnLocation().setYaw(-90);
                player.teleport(tj.getSpawnLocation());

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

                IChatBaseComponent emptyTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"\"}");
                IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");

                PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, emptyTitle);

                PacketPlayOutTitle length = new PacketPlayOutTitle(1, 19, 11);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatTitle));
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
            }
        });
    }


    private void playerFinished(Player p) {
        IChatBaseComponent emptyTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§6" + p.getName() + "\"}");
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§7hat das Ziel erreicht!" + "\"}");

        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, emptyTitle);

        PacketPlayOutTitle length = new PacketPlayOutTitle(1, 19, 11);

        this.playerArrayList.forEach(tp -> {
            Player player = tp.getPlayer();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatTitle));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);

            teleportPlayerToSpawn(player);
            player.getInventory().clear();
            player.getInventory().setItem(4, new ItemBuilder(Material.CHEST).setName("Shop").toItemStack());
            player.setLevel(tp.getTokens());
        });

        this.setGamePhase(Phase.Game_shop);
    }


    public void spawnModule(Player p, Location pressurePlate) {
        TryJumpPlayer player = null;
        for (TryJumpPlayer tp : this.getPlayerArrayList()) {
            if (tp.getPlayer() == p) {
                player = tp;
            }
        }
        assert player != null;
        player.resetUnitDeaths();
        if (player.getModuleId() <= 3) {
            loader.getModules().get(MDifficulty.EASY).get(player.getModuleId() - 1).paste("E", pressurePlate);
        }

        if (player.getModuleId() > 3 && player.getModuleId() <= 6) {
            loader.getModules().get(MDifficulty.MEDIUM).get(player.getModuleId() - 4).paste("E", pressurePlate);

        }
        if (player.getModuleId() > 6 && player.getModuleId() <= 9) {
            loader.getModules().get(MDifficulty.HARD).get(player.getModuleId() - 7).paste("E", pressurePlate);

        }
        if (player.getModuleId() == 10) {
            loader.getModules().get(MDifficulty.EXTREME).get(0).paste("E", pressurePlate);
        }

    }

    public void updateModule(Player player, Location location) {
        this.getPlayerArrayList().forEach(tp -> {
            if (tp.getPlayer() == player) {
                switch (tp.getModuleId()) {
                    case 1:
                    case 2:
                    case 3:
                        tp.addTokens(200);
                        break;
                    case 4:
                    case 5:
                    case 6:
                        tp.addTokens(300);
                        break;
                    case 7:
                    case 8:
                    case 9:
                        tp.addTokens(400);
                        break;
                    case 10:
                        tp.addTokens(500);
                }
                tp.setSpawnLocation(location);
                chatWriter.print(tp, GAME_COINS_ADD, new String[][]{{"UNITID", String.valueOf(tp.getModuleId())}, {"tokens", "200"}});
                tp.updateModuleId();

            }
        });
        if (this.getTryPlayer(player).getModuleId() == 11) {
            playerFinished(player);
        }
    }


    public String getModuleName(MDifficulty difficulty, int number) {
        return loader.getModules().get(difficulty).get(number).getName();
    }


    private void setTablist() {
        this.playerArrayList.forEach(tp -> {
            Player player = Bukkit.getPlayer(tp.getUniqueId());

            player.setPlayerListName("§7[§6" + tp.getTokens() + "§7] §a" + player.getName());
        });
    }

}
