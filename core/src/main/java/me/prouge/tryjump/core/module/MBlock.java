package me.prouge.tryjump.core.module;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.material.MaterialData;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MBlock {

    @JsonProperty("X")
    private int positionX;

    @JsonProperty("Y")
    private int positionY;

    @JsonProperty("Z")
    private int positionZ;

    @JsonProperty
    private Material material;

    @JsonProperty
    private byte data;

    @JsonProperty(required = false)
    private String playerName;

    @JsonProperty(required = false)
    private BlockFace blockFace;

    @JsonProperty(required = false)
    private DyeColor baseColor;

    @JsonProperty(required = false)
    private List<Pattern> patterns;

    @JsonProperty(required = false)
    private String[] lines;

    @JsonProperty(required = false)
    private MaterialData flowerPotContent;

    public MBlock(int positionX, int positionY, int positionZ, MaterialData data) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.material = data.getItemType();
        this.data = data.getData();
    }

    public MBlock(int positionX, int positionY, int positionZ, MaterialData data,
                  MaterialData flowerPotContent) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.material = data.getItemType();
        this.data = data.getData();
        this.flowerPotContent = flowerPotContent;
    }

    public MBlock(int positionX, int positionY, int positionZ, MaterialData data, String[] lines) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.material = data.getItemType();
        this.data = data.getData();
        this.lines = lines;
    }

    public MBlock(int positionX, int positionY, int positionZ, MaterialData data, DyeColor baseColor,
                  List<Pattern> patterns) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.material = data.getItemType();
        this.data = data.getData();
        this.baseColor = baseColor;
        this.patterns = patterns;
    }

    public MBlock(int positionX, int positionY, int positionZ, MaterialData data, String playerName,
                  BlockFace blockFace) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.material = data.getItemType();
        this.data = data.getData();
        this.playerName = playerName;
        this.blockFace = blockFace;
    }


    public Location getRelativeLocation(Location playerPosition) {
        int relativeX = positionX - playerPosition.getBlockX();
        int relativeY = positionY - playerPosition.getBlockY();
        int relativeZ = positionZ - playerPosition.getBlockZ();
        return new Location(playerPosition.getWorld(), relativeX, relativeY, relativeZ);
    }

    public void rotateBlock(int x, int z) {
        this.positionX = x;
        this.positionZ = z;
    }

    public boolean isNeighbor(MBlock otherBlock) {
        int dx = Math.abs(this.positionX - otherBlock.positionX);
        int dy = Math.abs(this.positionY - otherBlock.positionY);
        int dz = Math.abs(this.positionZ - otherBlock.positionZ);

        // Prüfen, ob die Differenz der Koordinaten genau 1 beträgt
        return (dx == 1 && dy == 0 && dz == 0) ||
                (dx == 0 && dy == 1 && dz == 0) ||
                (dx == 0 && dy == 0 && dz == 1);
    }

}
