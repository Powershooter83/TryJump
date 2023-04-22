package me.prouge.tryjump.core.game;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.events.LobbyStartEvent;
import me.prouge.tryjump.core.events.game.GamePlayerFinishedEvent;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.managers.ScoreboardManager;
import me.prouge.tryjump.core.module.MDifficulty;
import me.prouge.tryjump.core.module.MLoader;
import me.prouge.tryjump.core.module.Module;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.ItemBuilder;
import me.prouge.tryjump.core.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static me.prouge.tryjump.core.utils.Message.GAME_COINS_ADD;

@Singleton
public class GameImpl implements Game {


    private final LobbyStartEvent lobbyStartEvent = new LobbyStartEvent(false);
    private final ChatWriter chatWriter;
    private final ScoreboardManager scoreboardManager;
    @Getter
    private final ArrayList<TryJumpPlayer> playerArrayList = new ArrayList<>();
    @Getter
    private final ArrayList<Location> spawnLocations = new ArrayList<>();
    @Getter
    private final ArrayList<Location> deathMatchSpawnLocations = new ArrayList<>();
    private final TryJump plugin;
    private final MLoader loader;
    private final int MIN_PLAYERS;
    private final int MAX_PLAYERS;
    @Getter
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

        File locationsFile = new File(plugin.getDataFolder().getPath() + "/" + "locations.yml");
        try {
            Reader defConfigStream = new InputStreamReader(new FileInputStream(locationsFile), StandardCharsets.UTF_8);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(defConfigStream);
            this.spawnLocations.addAll(config.getConfigurationSection("Spawns").getValues(false).values().stream()
                    .map(Location.class::cast)
                    .collect(Collectors.toList()));
            this.deathMatchSpawnLocations.addAll(config.getConfigurationSection("DeathMatchSpawns").getValues(false).values().stream()
                    .map(Location.class::cast)
                    .collect(Collectors.toList()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.MIN_PLAYERS = (int) plugin.getConfig().get("minPlayers");
        this.MAX_PLAYERS = (int) plugin.getConfig().get("maxPlayers");
    }


    @Override
    public void sendPlayerJoinMessage(final Player player) {
        this.playerArrayList.forEach(p -> chatWriter.print(p, Message.GAME_JOIN_MESSAGE,
                new String[][]{{"PLAYER", player.getName()}}));
    }

    @Override
    public void sendPlayerQuitMessage(final Player player) {
        this.playerArrayList.forEach(p -> chatWriter.print(p, Message.GAME_QUIT_MESSAGE,
                new String[][]{{"PLAYER", player.getName()}}));
        lobbyStartEvent.setCancelled(true);
    }

    @Override
    public TryJumpPlayer getTryPlayer(final Player player) {
        return this.playerArrayList.stream()
                .filter(tp -> tp.getPlayer().getUniqueId().equals(player.getUniqueId()))
                .findFirst()
                .orElse(null);
    }

    private void sendActionbar(TryJumpPlayer p) {
        chatWriter.sendActionbar(p, Message.LOBBY_ACTIONBAR,
                new String[][]{{"CURRENT_PLAYERS", String.valueOf(this.playerArrayList.size())},
                        {"MAX_PLAYERS", String.valueOf(MAX_PLAYERS)}, {"MIN_PLAYERS", String.valueOf(MIN_PLAYERS)}});
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
                                        {"MAX_PLAYERS", String.valueOf(MAX_PLAYERS)}, {"MIN_PLAYERS", String.valueOf(MIN_PLAYERS)}});
                    });
                }
                if (getGamePhase().equals(Phase.Game_starting) || getGamePhase().equals(Phase.Game_running)) {
                    playerArrayList.forEach(p -> {
                        if (p.getModuleId() <= 3) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.EASY, p.getModuleId() - 1)},
                                            {"DIFFICULTY", String.valueOf(MDifficulty.EASY)}});
                        }

                        if (p.getModuleId() > 3 && p.getModuleId() <= 6) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.MEDIUM, p.getModuleId() - 4)},
                                            {"DIFFICULTY", "§6§l" + MDifficulty.MEDIUM}});
                        }
                        if (p.getModuleId() > 6 && p.getModuleId() <= 9) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.HARD, p.getModuleId() - 7)},
                                            {"DIFFICULTY", "§c§l" + MDifficulty.HARD}});
                        }
                        if (p.getModuleId() == 10) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", getModuleName(MDifficulty.EXTREME, 0)},
                                            {"DIFFICULTY", "§5§l" + MDifficulty.EXTREME}});
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
        if (this.playerArrayList.size() >= MIN_PLAYERS && this.getGamePhase() == Phase.Lobby_without_countdown) {
            this.setGamePhase(Phase.Lobby_with_countdown);
            startCountdown();
        }
        if (this.playerArrayList.size() < MIN_PLAYERS && this.getGamePhase() == Phase.Lobby_with_countdown) {
            this.setGamePhase(Phase.Lobby_without_countdown);
        }
    }

    public void updateModule(Player player, Location location) {
        int addedTokens = 0;
        TryJumpPlayer tryPlayer = this.getTryPlayer(player);
        if (tryPlayer == null) {
            return;
        }
        int moduleId = tryPlayer.getModuleId();
        switch (moduleId) {
            case 1:
            case 2:
            case 3:
                addedTokens = tryPlayer.getUnitDeaths() >= 3 ? 100 : 200;
                break;
            case 4:
            case 5:
            case 6:
                addedTokens = tryPlayer.getUnitDeaths() >= 3 ? 150 : 300;
                break;
            case 7:
            case 8:
            case 9:
                addedTokens = tryPlayer.getUnitDeaths() >= 3 ? 200 : 400;
                break;
            case 10:
                addedTokens = tryPlayer.getUnitDeaths() >= 3 ? 250 : 500;
                break;
        }
        for (TryJumpPlayer tp : this.getPlayerArrayList()) {
            if (tp.getPlayer() == player) {
                tp.addTokens(addedTokens);
                tp.setSpawnLocation(location);
                chatWriter.print(tp, GAME_COINS_ADD, new String[][]{{"UNITID", String.valueOf(moduleId)}, {"tokens", String.valueOf(addedTokens)}});
                tp.updateModuleId();
            }
        }

        if (moduleId == 11) {
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerFinishedEvent(player));
        }
    }


    private String getModuleName(MDifficulty difficulty, int number) {
        return loader.getModules().get(difficulty).get(number).getName();
    }


    public void setTablist() {
        this.playerArrayList.forEach(tp -> {
            Player player = Bukkit.getPlayer(tp.getUniqueId());

            player.setPlayerListName("§7[§6" + tp.getTokens() + "§7] §a" + player.getName());
        });
    }

    public void skipShop() {
        if (this.playerArrayList.stream().filter(TryJumpPlayer::isSkipped).count() == this.playerArrayList.size()) {
            scoreboardManager.setTime(LocalTime.of(0, 0, 5));
            playerArrayList.forEach(p -> chatWriter.print(p, Message.SHOP_SKIPPED, null));
        }
    }

    public Location calculateSpawn() {
        double maxMinDistance = 0;
        Location farthestSpawn = null;

        for (Location spawn : deathMatchSpawnLocations) {
            double minDistance = playerArrayList.stream()
                    .filter(tp -> tp.getDeathMatchDeaths() != 3)
                    .mapToDouble(tp -> tp.getPlayer().getLocation().distance(spawn))
                    .min()
                    .orElse(Double.MAX_VALUE);

            if (minDistance > maxMinDistance) {
                maxMinDistance = minDistance;
                farthestSpawn = spawn;
            }
        }

        return farthestSpawn;
    }

    @Override
    public void addPlayer(final Player player) {
        String language = player.spigot().getLocale().substring(0, player.spigot().getLocale().lastIndexOf('_'));
        List<String> languageFiles = Arrays.asList(Objects.requireNonNull(new File(plugin.getDataFolder().getPath() + "/languages").list()));
        language = languageFiles.contains(language + ".yml") ? language : "de";

        TryJumpPlayer tryPlayer = new TryJumpPlayer(language, player.getUniqueId(), spawnLocations.remove(0));
        playerArrayList.add(tryPlayer);
        sendPlayerJoinMessage(player);
        sendActionbar(tryPlayer);
        checkGamePhase();

        player.teleport((Location) plugin.getConfig().get("Lobby"));

        ItemStack itemStack = new ItemBuilder(Material.INK_SACK, 1, (byte) 1)
                .setName(chatWriter.getItemStackName(tryPlayer, Message.LOBBY_ITEM_LEAVE))
                .toItemStack();
        player.getInventory().setItem(8, itemStack);
        player.updateInventory();
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


    @Override
    public void startCountdown() {
        Bukkit.getPluginManager().callEvent(lobbyStartEvent);
    }


}
