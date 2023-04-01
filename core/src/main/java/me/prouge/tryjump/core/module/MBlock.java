package me.prouge.tryjump.core.module;

//import com.fasterxml.jackson.annotation.JsonProperty;
import org.bukkit.Location;
import org.bukkit.Material;

public class MBlock {

   // @JsonProperty("positionX")
    private int positionX;
  //  @JsonProperty("positionY")
    private int positionY;
   // @JsonProperty("positionZ")
    private int positionZ;
  //  @JsonProperty("data")
    private byte data;
  //  @JsonProperty("type")
    private Material type;

    public MBlock() {
    }

    public MBlock(int positionX, int positionY, int positionZ, byte data, Material type) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.data = data;
        this.type = type;
    }

    public Location getRelativeLocation(Location playerPosition) {
        int relativeX = 0;
        int relativeY = 0;
        int relativeZ = 0;

        if (playerPosition.getBlockX() < positionX && playerPosition.getBlockX() > 0) {
            relativeX = positionX - playerPosition.getBlockX();
        }
        if (playerPosition.getBlockX() < positionX && playerPosition.getBlockX() < 0) {
            relativeX = positionX + playerPosition.getBlockX();
        }
        if (playerPosition.getBlockX() > positionX && positionX > 0) {
            relativeX = -(playerPosition.getBlockX() - positionX);
        }
        if (playerPosition.getBlockX() > positionX && positionX < 0) {
            relativeX = playerPosition.getBlockX() + positionX;
        }

        if (playerPosition.getBlockY() < positionY && playerPosition.getBlockY() > 0) {
            relativeY = positionY - playerPosition.getBlockY();
        }
        if (playerPosition.getBlockY() < positionY && playerPosition.getBlockY() < 0) {
            relativeY = positionY + playerPosition.getBlockY();
        }
        if (playerPosition.getBlockY() > positionY && positionY > 0) {
            relativeY = -(playerPosition.getBlockY() - positionY);
        }
        if (playerPosition.getBlockY() > positionY && positionY < 0) {
            relativeY = playerPosition.getBlockY() + positionY;
        }

        if (playerPosition.getBlockZ() < positionZ && playerPosition.getBlockZ() > 0) {
            relativeZ = positionZ - playerPosition.getBlockZ();
        }
        if (playerPosition.getBlockZ() < positionZ && playerPosition.getBlockZ() < 0) {
            relativeZ = positionZ + playerPosition.getBlockZ();
        }
        if (playerPosition.getBlockZ() > positionZ && positionZ > 0) {
            relativeZ = -(playerPosition.getBlockZ() - positionZ);
        }
        if (playerPosition.getBlockZ() > positionZ && positionZ < 0) {
            relativeZ = playerPosition.getBlockZ() + positionZ;
        }
        return new Location(playerPosition.getWorld(), relativeX, relativeY, relativeZ);
    }


    @Override
    public String toString() {
        return "MBlock{" +
                "positionX=" + positionX +
                ", positionY=" + positionY +
                ", positionZ=" + positionZ +
                ", data=" + data +
                ", type=" + type +
                '}';
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(int positionZ) {
        this.positionZ = positionZ;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }


}
