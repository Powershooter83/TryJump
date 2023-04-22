package me.prouge.tryjump.core.game.player;

import lombok.Getter;
import lombok.Setter;
import me.prouge.tryjump.core.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TryJumpPlayer {

    @Getter
    private final String language;
    @Getter
    private final UUID uniqueId;
    @Getter
    @Setter
    private int team;
    @Getter
    private Location spawnLocation;

    @Getter
    @Setter
    private float walkedDistance = 0;

    @Getter
    @Setter
    private Location helpBlockLocation;

    @Getter
    @Setter
    private boolean nextSwordPrice = false;

    @Getter
    @Setter
    private boolean nextFishingRodPrice = false;

    @Getter
    @Setter
    private boolean nextBowPrice = false;


    @Getter
    @Setter
    private int tokens = 100;
    @Getter
    private int moduleId = 1;
    private int unitDeaths = 0;

    @Getter
    private int totalUnitDeaths = 0;

    @Getter
    @Setter
    private byte deathMatchDeaths = 0;

    @Getter
    @Setter
    private float walkedDistanceUntilDeath = 0;

    @Getter
    @Setter
    private long timeStamp = 0;

    @Getter
    @Setter
    private boolean skipped = false;

    public TryJumpPlayer(String language, UUID uuid, Location spawnLocation) {
        this.language = language;
        this.uniqueId = uuid;
        this.spawnLocation = spawnLocation;
    }


    public void updateModuleId() {
        this.moduleId++;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }

    public void teleportToSpawn() {
        this.getPlayer().teleport(spawnLocation);
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
    }

    public void updateUnitDeaths() {
        this.unitDeaths++;
    }

    public void addTokens(int tokens) {
        this.tokens += tokens;
    }

    public int getUnitDeaths() {
        return this.unitDeaths;
    }

    public void resetUnitDeaths() {
        this.totalUnitDeaths += this.unitDeaths;
        this.unitDeaths = 0;
    }

    public void updateWalkedDistance(final double length) {
        this.walkedDistance += length;
    }

    public void addHelpBlock() {
        switch (this.moduleId) {
            case 1:
            case 2:
            case 3:
                getPlayer().getInventory().addItem(
                        new ItemBuilder(Material.STAINED_CLAY).
                                setDyeColor(DyeColor.LIME).
                                setName("§cHilfsblock")
                                .setLore("§8➥ §7Mit §c/reset §7kannst du den Block", "   §7zurückbekommen!",
                                        "§c§lACHTUNG" + "§8➥ §7Du wirst dabei an den Unit", "   §7Spawn zurück teleportiert!").
                                toItemStack());
                break;
            case 4:
            case 5:
            case 6:
                getPlayer().getInventory().addItem(
                        new ItemBuilder(Material.STAINED_CLAY).
                                setDyeColor(DyeColor.ORANGE).
                                setName("§cHilfsblock")
                                .setLore("§8➥ §7Mit §c/reset §7kannst du den Block", "   §7zurückbekommen!",
                                        "§c§lACHTUNG" + "§8➥ §7Du wirst dabei an den Unit", "   §7Spawn zurück teleportiert!").
                                toItemStack());
                break;
            case 7:
            case 8:
            case 9:
                getPlayer().getInventory().addItem(
                        new ItemBuilder(Material.STAINED_CLAY).
                                setDyeColor(DyeColor.RED).
                                setName("§cHilfsblock")
                                .setLore("§8➥ §7Mit §c/reset §7kannst du den Block", "   §7zurückbekommen!",
                                        "§c§lACHTUNG" + "§8➥ §7Du wirst dabei an den Unit", "   §7Spawn zurück teleportiert!").
                                toItemStack());
                break;
            case 10:
                getPlayer().getInventory().addItem(
                        new ItemBuilder(Material.STAINED_CLAY).
                                setDyeColor(DyeColor.MAGENTA).
                                setName("§cHilfsblock")
                                .setLore("§8➥ §7Mit §c/reset §7kannst du den Block", "   §7zurückbekommen!",
                                        "§c§lACHTUNG" + "§8➥ §7Du wirst dabei an den Unit", "   §7Spawn zurück teleportiert!").
                                toItemStack());
                break;
        }

        getPlayer().updateInventory();
    }


}
