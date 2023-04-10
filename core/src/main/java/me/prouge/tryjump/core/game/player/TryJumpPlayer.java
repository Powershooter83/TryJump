package me.prouge.tryjump.core.game.player;

import lombok.Getter;
import lombok.Setter;
import me.prouge.tryjump.core.utils.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private float walkedDistance = 0;

    @Getter
    @Setter
    private int tokens = 100;
    @Getter
    private int moduleId = 1;
    private int unitDeaths = 0;

    @Getter
    @Setter

    private boolean skipped = false;

    private Direction facingDirection;

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
        getFacing();
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
        this.unitDeaths = 0;
    }

    private void getFacing() {
        this.facingDirection = Direction.fromYaw(spawnLocation.getYaw());
    }


    public void updateWalkedDistance(final double length) {
        this.walkedDistance += length;
    }


}
