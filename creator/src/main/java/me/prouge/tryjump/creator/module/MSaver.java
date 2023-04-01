package me.prouge.tryjump.creator.module;

//import com.fasterxml.jackson.databind.ObjectMapper;
import me.prouge.tryjump.creator.TryJump;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
                        blockList.add(new MBlock(x, y, z, selected.getData(), selected.getType()));
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

        writer.write(getDirection(playerLocation) + ";" + name + ";" + difficulty + "01001023010000140141024023415433543");
     //   writer.write(new ObjectMapper().writeValueAsString(blockList));
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
