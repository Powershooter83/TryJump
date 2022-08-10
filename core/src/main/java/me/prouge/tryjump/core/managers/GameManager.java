package me.prouge.tryjump.core.managers;

import com.sun.org.apache.xpath.internal.operations.Mod;
import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.module.MDifficulty;
import me.prouge.tryjump.core.module.MLoader;
import me.prouge.tryjump.core.module.Module;
import me.prouge.tryjump.core.util.ChatWriter;
import me.prouge.tryjump.core.util.Message;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.io.File;
import java.util.*;

public class GameManager {

    @Inject
    private TryJump plugin;
    @Inject
    private ChatWriter chatWriter;

    @Inject
    private MLoader mLoader;
    private Phase gamePhase = Phase.Lobby_without_countdown;

    private HashMap<MDifficulty, ArrayList<Module>> modules = new HashMap<>();


    private final ArrayList<TryPlayer> playerArrayList = new ArrayList<>();

    private final ArrayList<Location> spawnLocations = new ArrayList<Location>();

    private boolean hasRunningActionbar = false;

    public void updateModule(Player player, Location location) {
        this.playerArrayList.forEach(tp -> {
            if (tp.toPlayer() == player) {

                switch (tp.getModuleId()){
                    case 1:
                    case 2:
                    case 3:
                        tp.addTokens(200);
                    case 4:
                    case 5:
                    case 6:
                        tp.addTokens(300);
                    case 7:
                    case 8:
                    case 9:
                        tp.addTokens(400);
                    case 10:
                        tp.addTokens(500 );
                }


                tp.updateModuleId();
                tp.setSpawnLocation(location);
                chatWriter.print(tp, Message.PLAYER_JOIN_MESSAGE, null);




            }
        });
    }

    public void teleportPlayer(Player player) {
        this.playerArrayList.forEach(tp -> {
            if (tp.toPlayer() == player) {
                player.teleport(tp.getSpawnLocation());
            }
        });
    }


    public void addPlayer(Player p) {
        if (spawnLocations.size() == 0) {
            for (String spawn : plugin.getConfig().getConfigurationSection("Spawns").getKeys(false)) {
                this.spawnLocations.add((Location) plugin.getConfig().get("Spawns." + spawn));
            }
        }

        String language = p.spigot().getLocale().substring(0, p.spigot().getLocale().lastIndexOf('_'));
        List<String> languageFiles = Arrays.asList(Objects.requireNonNull(new File(plugin.getDataFolder().getPath() + "/languages").list()));
        if (!languageFiles.contains(language + ".yml")) {
            language = "de";
        }
        TryPlayer tryPlayer = new TryPlayer(language, p, spawnLocations.get(0));
        spawnLocations.remove(0);

        this.playerArrayList.add(tryPlayer);
        sendPlayerJoinMessage(p);
        checkGamePhase();
        sendActionbar(tryPlayer);
        getModules();

        p.teleport((Location) plugin.getConfig().get("Lobby"));

    }

    private void getModules() {
        if (this.modules.size() == 0) {
            HashMap<MDifficulty, ArrayList<Module>> allModules = mLoader.getAllModules();

            ArrayList<Module> easy = allModules.get(MDifficulty.EASY);
            Collections.shuffle(easy);
            ArrayList<Module> medium = allModules.get(MDifficulty.MEDIUM);
            Collections.shuffle(medium);
            ArrayList<Module> hard = allModules.get(MDifficulty.HARD);
            Collections.shuffle(hard);
            ArrayList<Module> extreme = allModules.get(MDifficulty.EXTREME);
            Collections.shuffle(extreme);

            modules.computeIfAbsent(MDifficulty.EASY, k -> new ArrayList<>());
            modules.get(MDifficulty.EASY).add(easy.get(0));
            modules.get(MDifficulty.EASY).add(easy.get(1));
            modules.get(MDifficulty.EASY).add(easy.get(2));

            modules.computeIfAbsent(MDifficulty.MEDIUM, k -> new ArrayList<>());
            modules.get(MDifficulty.MEDIUM).add(medium.get(0));
            modules.get(MDifficulty.MEDIUM).add(medium.get(1));
            modules.get(MDifficulty.MEDIUM).add(medium.get(2));

            modules.computeIfAbsent(MDifficulty.HARD, k -> new ArrayList<>());
            modules.get(MDifficulty.HARD).add(hard.get(0));
            modules.get(MDifficulty.HARD).add(hard.get(1));
            modules.get(MDifficulty.HARD).add(hard.get(2));

            modules.computeIfAbsent(MDifficulty.EXTREME, k -> new ArrayList<>());
            modules.get(MDifficulty.EXTREME).add(extreme.get(0));
        }
    }


    public void removePlayer(Player p) {
        TryPlayer toRemove = null;

        for (TryPlayer tryPlayer : this.playerArrayList) {
            if (tryPlayer.toPlayer() == p) {
                this.spawnLocations.add(tryPlayer.getSpawnLocation());
                toRemove = tryPlayer;
            }
        }
        this.playerArrayList.remove(toRemove);
        sendPlayerQuitMessage(p);
        checkGamePhase();
    }

    private void sendPlayerJoinMessage(Player player) {
        playerArrayList.forEach(p -> chatWriter.print(p, Message.PLAYER_JOIN_MESSAGE,
                new String[][]{{"PLAYER", player.getName()}}));
    }

    private void sendPlayerQuitMessage(Player player) {
        playerArrayList.forEach(p -> chatWriter.print(p, Message.PLAYER_QUIT_MESSAGE,
                new String[][]{{"PLAYER", player.getName()}}));
    }

    private void checkGamePhase() {
        if (playerArrayList.size() >= 2 && this.gamePhase == Phase.Lobby_without_countdown) {
            this.gamePhase = Phase.Lobby_with_countdown;
            startCountdown();
        }
        if (playerArrayList.size() < 2 && this.gamePhase == Phase.Lobby_with_countdown) {
            this.gamePhase = Phase.Lobby_without_countdown;
        }
    }

    private void sendActionbar(TryPlayer p) {
        chatWriter.sendActionbar(p, Message.LOBBY_ACTIONBAR,
                new String[][]{{"CURRENT_PLAYERS", String.valueOf(playerArrayList.size())},
                        {"MAX_PLAYERS", "10"}, {"MIN_PLAYERS", "2"}});
        if (hasRunningActionbar) {
            return;
        }
        hasRunningActionbar = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gamePhase.equals(Phase.Lobby_with_countdown) || gamePhase.equals(Phase.Lobby_without_countdown)) {
                    playerArrayList.forEach(p -> {
                        chatWriter.sendActionbar(p, Message.LOBBY_ACTIONBAR,
                                new String[][]{{"CURRENT_PLAYERS", String.valueOf(playerArrayList.size())},
                                        {"MAX_PLAYERS", "10"}, {"MIN_PLAYERS", "2"}});
                    });
                }
                if (gamePhase.equals(Phase.Game_starting) || gamePhase.equals(Phase.Game_running)) {
                    playerArrayList.forEach(p -> {
                        if (p.getModuleId() <= 3) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", modules.get(MDifficulty.EASY).get(p.getModuleId() - 1).getName()},
                                            {"DIFFICULTY", "EASY"}});
                        }

                        if (p.getModuleId() > 3 && p.getModuleId() <= 6) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", modules.get(MDifficulty.MEDIUM).get(p.getModuleId() - 4).getName()},
                                            {"DIFFICULTY", "MEDIUM"}});
                        }
                        if (p.getModuleId() > 6 && p.getModuleId() <= 9) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", modules.get(MDifficulty.HARD).get(p.getModuleId() - 7).getName()},
                                            {"DIFFICULTY", "HARD"}});
                        }
                        if (p.getModuleId() == 10) {
                            chatWriter.sendActionbar(p, Message.GAME_ACTIONBAR,
                                    new String[][]{{"UNIT", String.valueOf(p.getModuleId())},
                                            {"UNITNAME", modules.get(MDifficulty.EXTREME).get(0).getName()},
                                            {"DIFFICULTY", "EXTREME"}});
                        }
                    });


                }

            }
        }.runTaskTimer(plugin, 0, 40L);
    }

    public void spawnModule(Player p, Location pressurePlate) {
        TryPlayer player = null;
        for (TryPlayer tp : this.playerArrayList) {
            if (tp.toPlayer() == p) {
                player = tp;
            }
        }
        assert player != null;
        player.resetUnitDeaths();
        if (player.getModuleId() <= 3) {
            this.modules.get(MDifficulty.EASY).get(player.getModuleId() - 1).paste("E", pressurePlate);
        }

        if (player.getModuleId() > 3 && player.getModuleId() <= 6) {
            this.modules.get(MDifficulty.MEDIUM).get(player.getModuleId() - 4).paste("E", pressurePlate);

        }
        if (player.getModuleId() > 6 && player.getModuleId() <= 9) {
            this.modules.get(MDifficulty.HARD).get(player.getModuleId() - 7).paste("E", pressurePlate);

        }
        if (player.getModuleId() == 10) {
            this.modules.get(MDifficulty.EXTREME).get(0).paste("E", pressurePlate);
        }

    }


    public Phase getGamePhase() {
        return this.gamePhase;
    }


    private void startCountdown() {
        new BukkitRunnable() {
            int seconds = 60;

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
                            playerArrayList.forEach(p -> p.toPlayer().sendMessage(""));
                        }
                        playerArrayList.forEach(p -> {
                            chatWriter.print(p, Message.LOBBY_TELEPORT, null);
                            p.teleportToSpawn();
                        });
                        startFinalCountdown();
                        cancel();
                }
                seconds--;
            }
        }.runTaskTimer(plugin, 0, 20L);

    }

    private void startFinalCountdown() {
        this.gamePhase = Phase.Game_starting;

        ItemStack itemStack = new ItemStack(Material.INK_SACK, 1, (byte) 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§c§lInstant-Tod(TM) §7§o<Rechtsklick>");
        itemStack.setItemMeta(itemMeta);


        playerArrayList.forEach(tj -> {

            tj.toPlayer().getInventory().setItem(4, itemStack);


        });

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
                        gamePhase = Phase.Game_running;
                        playerArrayList.forEach(p -> chatWriter.print(p, Message.GAME_GO, null));
                        playerArrayList.forEach(p -> {
                            spawnModule(p.toPlayer(), p.getSpawnLocation().add(6, 0, 0));
                        });
                        cancel();
                        break;
                }
                seconds--;
            }
        }.runTaskTimer(plugin, 0, 20L);

    }

    public void instantDeath(Player player) {
        this.playerArrayList.forEach(tj -> {
            if (tj.toPlayer().equals(player)) {
                tj.getSpawnLocation().setYaw(-90);
                player.teleport(tj.getSpawnLocation());

                String text = "§c✖✖✖";

                switch (tj.getUnitDeaths()){
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


}
