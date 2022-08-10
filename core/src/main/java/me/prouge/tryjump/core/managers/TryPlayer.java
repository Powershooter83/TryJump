package me.prouge.tryjump.core.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TryPlayer {

    private String language;
    private Player player;

    private Location spawnLocation;

    private int tokens = 100;

    private int moduleId = 1;
    private int unitDeaths = 0;

    public TryPlayer(String language, Player player, Location spawnLocation) {
        this.language = language;
        this.player = player;
        this.spawnLocation = spawnLocation;
    }


    public void updateModuleId() {
        this.moduleId++;
    }

    public int getModuleId() {
        return this.moduleId;
    }

    public Player toPlayer() {
        return this.player;
    }

    public String language() {
        return this.language;
    }

    public void teleportToSpawn() {
        this.player.teleport(spawnLocation);
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
    }

    public void updateUnitDeaths() {
        this.unitDeaths++;
    }

    public void addTokens(int tokens){
        this.tokens+= tokens;
    }

    public int getUnitDeaths(){
        return this.unitDeaths;
    }

    public void resetUnitDeaths(){
        this.unitDeaths = 0;
    }



}
