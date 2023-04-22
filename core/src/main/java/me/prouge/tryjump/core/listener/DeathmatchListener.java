package me.prouge.tryjump.core.listener;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.provider.service.SpecificCloudServiceProvider;
import de.dytanic.cloudnet.driver.service.ServiceId;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.executor.ServerSelectorType;
import me.prouge.tryjump.core.TryJump;
import me.prouge.tryjump.core.events.DeatchmatchDeathEvent;
import me.prouge.tryjump.core.events.DeathmatchEndEvent;
import me.prouge.tryjump.core.events.DeathmatchStartEvent;
import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.managers.ScoreboardManager;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.Message;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

public class DeathmatchListener implements Listener {

    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry()
            .getFirstService(IPlayerManager.class);

    @Inject
    private GameImpl game;
    @Inject
    private TryJump plugin;
    @Inject
    private ChatWriter chatWriter;
    @Inject
    private ScoreboardManager scoreboardManager;

    @EventHandler
    public void onDeathmatchEndEvent(final DeathmatchEndEvent event) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§7hat das Spiel gewonnen!" + "\"}");
        PacketPlayOutTitle length = new PacketPlayOutTitle(1, 19, 11);

        game.getPlayerArrayList().forEach(tp -> {
            IChatBaseComponent emptyTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§6" + event.getWinner().getPlayer().getName() + "\"}");
            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, emptyTitle);
            chatWriter.print(tp, Message.DEATHMATCH_WINNER, new String[][]{{"PLAYER", event.getWinner().getPlayer().getName()}});
            chatWriter.print(tp, Message.DEATHMATCH_RESTART, null);
            Player player = tp.getPlayer();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatTitle));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
        });

        Bukkit.getScheduler().runTaskLater(plugin, Bukkit::shutdown, 10 * 20);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ServiceId serviceId = null;

            for (TryJumpPlayer tp : game.getPlayerArrayList()) {
                serviceId = playerManager.getOnlinePlayer(tp.getUniqueId()).getConnectedService().getServiceId();
                playerManager.getPlayerExecutor(tp.getUniqueId()).connectToTask("Lobby", ServerSelectorType.HIGHEST_PLAYERS);
            }
            SpecificCloudServiceProvider service = CloudNetDriver.getInstance().getCloudServiceProvider(serviceId.getUniqueId());
            service.stop();
        }, 10 * 20);

    }

    @EventHandler
    public void onDeathmatchStartEvent(final DeathmatchStartEvent event) {
        scoreboardManager.createDeathMatchScoreboard(game.getPlayerArrayList());

        new BukkitRunnable() {
            @Override
            public void run() {
                scoreboardManager.updateDeathMatchScoreboard(game.getPlayerArrayList());
            }
        }.runTaskTimer(plugin, 0, 20L);


        game.setGamePhase(Phase.Game_pvp);
        Collections.shuffle(game.getDeathMatchSpawnLocations());
        int index = 0;
        for (TryJumpPlayer tryJumpPlayer : game.getPlayerArrayList()) {
            Player player = tryJumpPlayer.getPlayer();
            tryJumpPlayer.getPlayer().teleport(game.getDeathMatchSpawnLocations().get(index));

            player.setLevel(0);
            player.setGameMode(GameMode.SURVIVAL);

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.CHEST) {
                    player.getInventory().remove(item);
                    player.updateInventory();
                    break;
                }
            }
            index++;
        }
    }


    @EventHandler
    public void onDeathmatchDeatchEvent(final DeatchmatchDeathEvent event) {
        Player victim = event.getVictim();
        Player attacker = event.getAttacker();
        victim.setHealth(victim.getMaxHealth());

        TryJumpPlayer tryp = game.getTryPlayer(victim);
        victim.setFoodLevel(20);
        tryp.setTimeStamp(System.currentTimeMillis());

        tryp.setDeathMatchDeaths((byte) (tryp.getDeathMatchDeaths() + 1));

        if (attacker != null) {
            attacker.playSound(attacker.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
            this.game.getPlayerArrayList().forEach(tp -> chatWriter.print(tp, Message.DEATHMATCH_KILL, new String[][]{
                    {"VICTIM", victim.getName()}, {"KILLER", attacker.getName()}}));

            attacker.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0, false, false));
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) this, () -> attacker.removePotionEffect(PotionEffectType.REGENERATION), 5 * 20);

            double hearts = attacker.getHealth();
            double maxHearts = attacker.getMaxHealth();
            StringBuilder health = new StringBuilder();
            health.append("§c");

            if (hearts % 2 != 0) {
                health.append("❥");
                hearts = hearts - 2;
            }

            for (int i = 0; i < hearts / 2; i++) {
                health.append("❤");
            }
            health.append("§7");
            for (int i = 0; i < (maxHearts - hearts) / 2; i++) {
                health.append("❤");
            }

            chatWriter.print(tryp, Message.DEATHMATCH_ATTACKER_LIVE, new String[][]{{
                    "KILLER", attacker.getName()},
                    {"HEARTS", health.toString()}});
        } else {
            this.game.getPlayerArrayList().forEach(tp -> chatWriter.print(tp, Message.DEATHMATCH_KILL, new String[][]{
                    {"VICTIM", victim.getName()}, {"KILLER", "TNT"}}));
        }


        if (tryp.getDeathMatchDeaths() == 3) {
            this.game.getPlayerArrayList().forEach(tp -> chatWriter.print(tp, Message.PLAYER_QUIT_MESSAGE, new String[][]{{"PLAYER", victim.getName()}}));
            victim.setGameMode(GameMode.ADVENTURE);
            victim.getInventory().clear();
            victim.setAllowFlight(true);
            victim.setFlying(true);
            victim.teleport(victim.getLocation().add(0, 5, 0));

            victim.getInventory().forEach(item -> {
                if (item != null && item.getType() != Material.AIR) {
                    victim.getWorld().dropItemNaturally(victim.getLocation(), item);
                }
            });

            Bukkit.getOnlinePlayers().forEach(op -> op.hidePlayer(victim));

            if (this.game.getPlayerArrayList().stream().filter(tp -> tp.getDeathMatchDeaths() != 3).count() == 1) {
                Optional<TryJumpPlayer> winner = this.game.getPlayerArrayList().stream().filter(tp -> tp.getDeathMatchDeaths() != 3).findFirst();
                winner.ifPresent(tryjumpPlayer -> Bukkit.getPluginManager().callEvent(new DeathmatchEndEvent(tryjumpPlayer)));
            }
        } else {
            chatWriter.print(tryp, Message.DEATHMATCH_SPAWN_PROTECTION, null);
            victim.teleport(game.calculateSpawn());
        }
        victim.playSound(victim.getLocation(), Sound.NOTE_PLING, 1, 1);
    }

}
