package me.prouge.tryjump.creator.module;

//import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.prouge.tryjump.creator.TryJump;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.material.FlowerPot;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MSaver {

    @Inject
    private TryJump plugin;

    public void saveModule(Location position1,
                           Location position2,
                           Location playerLocation,
                           String name,
                           MDifficulty difficulty) {
        final List<MBlock> blockList = new ArrayList<>();
        int minX = Math.min(position1.getBlockX(), position2.getBlockX());
        int maxX = Math.max(position1.getBlockX(), position2.getBlockX());

        int minY = Math.min(position1.getBlockY(), position2.getBlockY());
        int maxY = Math.max(position1.getBlockY(), position2.getBlockY());

        int minZ = Math.min(position1.getBlockZ(), position2.getBlockZ());
        int maxZ = Math.max(position1.getBlockZ(), position2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block selected = position1.getWorld().getBlockAt(x, y, z);
                    if (selected.getType() != Material.AIR) {
                        if (selected.getType() == Material.SKULL) {
                            Skull skull = (Skull) selected.getState();
                            if (skull.getSkullType() == SkullType.PLAYER) {
                                blockList.add(new MBlock(x, y, z, selected.getState().getData(), skull.getOwner(), skull.getRotation()));
                                continue;
                            }
                        }
                        if (selected.getType() == Material.STANDING_BANNER || selected.getType() == Material.WALL_BANNER) {
                            Banner banner = (Banner) selected.getState();
                            blockList.add(new MBlock(x, y, z, selected.getState().getData(),
                                    banner.getBaseColor(),
                                    banner.getPatterns()));
                            continue;
                        }

                        if (selected.getType() == Material.SIGN_POST || selected.getType() == Material.WALL_SIGN) {
                            Sign sign = (Sign) selected.getState();
                            blockList.add(new MBlock(x, y, z, selected.getState().getData(),
                                    sign.getLines()));
                            continue;
                        }
                        blockList.add(new MBlock(x, y, z, selected.getState().getData()));
                    }
                }
            }
        }
        blockList.forEach(block -> {
            Location relative = block.getRelativeLocation(playerLocation);

            block.setPositionX(relative.getBlockX());
            block.setPositionY(relative.getBlockY());
            block.setPositionZ(relative.getBlockZ());
        });

        try {
            saveToFile(blockList, playerLocation, name, difficulty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToFile(List<MBlock> blockList, Location playerLocation, String name, MDifficulty difficulty) throws IOException {

        final File folder = new File("plugins" + File.separator + "TryJump-Modules" + File.separator + difficulty.toString().toLowerCase());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        final File file = new File(folder.getPath() + File.separator + name + ".module");

        if (!file.exists()) {
            file.createNewFile();
        }

        final FileWriter writer = new FileWriter(file);

        writer.write(getDirection(playerLocation) + ";" + name + ";" + difficulty + "000000000000000000");
        writer.write(new ObjectMapper().writeValueAsString(blockList));
        writer.flush();
        writer.close();
    }


    private String getDirection(Location location) {
        double rotation = (location.getYaw() - 90.0F) % 360.0F;
        if (rotation < 0.0D) {
            rotation += 360.0D;
        }
        if ((0.0D <= rotation) && (rotation < 45.0D))
            return "W";
        if ((45.0D <= rotation) && (rotation < 135.0D))
            return "N";
        if ((135.0D <= rotation) && (rotation < 225.0D))
            return "E";
        if ((225.0D <= rotation) && (rotation < 315.0D))
            return "S";
        if ((315.0D <= rotation) && (rotation < 360.0D)) {
            return "W";
        }
        return null;
    }


}
