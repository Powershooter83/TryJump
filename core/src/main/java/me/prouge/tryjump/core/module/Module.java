package me.prouge.tryjump.core.module;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Module {

    private String name;
    private String facing;
    private MDifficulty difficulty;

    private List<MBlock> moduleBlockList;

    public Module(String name, String facing, MDifficulty difficulty, List<MBlock> moduleBlockList) {
        this.name = name;
        this.facing = facing;
        this.difficulty = difficulty;
        this.moduleBlockList = moduleBlockList;
    }


    public void paste(String direction, Location location) {
        if (direction.equals(facing)) {
            pasteTheObject(this.moduleBlockList, location);
            return;
        }
        List<MBlock> blocks = new ArrayList<>();
        this.moduleBlockList.forEach(mBlock -> {
            blocks.add(new MBlock(mBlock.getPositionX(), mBlock.getPositionY(), mBlock.getPositionZ(), mBlock.getData(), mBlock.getType()));
        });
        if (facing.equals("N") && direction.equals("E")) {
            blocks.forEach(mBlock -> {
                int blockX = mBlock.getPositionX();
                int blockZ = mBlock.getPositionZ();
                mBlock.setPositionX(-blockZ);
                mBlock.setPositionZ(blockX);
            });
        }
        if (facing.equals("N") && direction.equals("S") ||
                facing.equals("E") && direction.equals("W") ||
                facing.equals("S") && direction.equals("N") ||
                facing.equals("W") && direction.equals("E")) {
            blocks.forEach(mBlock -> {
                int blockX = mBlock.getPositionX();
                int blockZ = mBlock.getPositionZ();
                mBlock.setPositionX(-blockX);
                mBlock.setPositionZ(-blockZ);
            });
        }
        if (facing.equals("N") && direction.equals("W") ||
                facing.equals("E") && direction.equals("N") ||
                facing.equals("S") && direction.equals("E") ||
                facing.equals("W") && direction.equals("S")) {
            blocks.forEach(mBlock -> {
                int blockX = mBlock.getPositionX();
                int blockZ = mBlock.getPositionZ();
                mBlock.setPositionX(blockZ);
                mBlock.setPositionZ(-blockX);
            });
        }
        if (facing.equals("E") && direction.equals("S") ||
                facing.equals("S") && direction.equals("W") ||
                facing.equals("W") && direction.equals("N")) {
            blocks.forEach(mBlock -> {
                int blockX = mBlock.getPositionX();
                int blockZ = mBlock.getPositionZ();
                mBlock.setPositionX(-blockZ);
                mBlock.setPositionZ(blockX);
            });

        }
        pasteTheObject(blocks, location);
    }


    private void pasteTheObject(List<MBlock> blocks, Location location) {
        blocks.forEach(mBlock -> {
            final Location block2 = new Location(location.getWorld(),
                    mBlock.getPositionX(),
                    mBlock.getPositionY(),
                    mBlock.getPositionZ());

            final Location officalLocation = block2.add(location);
            final org.bukkit.block.Block block1 = officalLocation.getBlock();

            block1.setType(mBlock.getType());
            block1.setData(mBlock.getData());
        });

    }

    public int getMaxLength() {
        int maxLength = 0;
        switch(facing) {
            case "N":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionZ));
                maxLength = moduleBlockList.get(moduleBlockList.size() - 1).getPositionZ() - moduleBlockList.get(0).getPositionZ();
                break;
            case "W":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionX));
                maxLength = moduleBlockList.get(0).getPositionX() - moduleBlockList.get(moduleBlockList.size() - 1).getPositionX();
                break;
            case "E":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionX));
                maxLength = moduleBlockList.get(moduleBlockList.size() - 1).getPositionX() - moduleBlockList.get(0).getPositionX();
                break;
            case "S":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionZ));
                maxLength = moduleBlockList.get(0).getPositionZ() - moduleBlockList.get(moduleBlockList.size() - 1).getPositionZ();
                break;
        }
        return maxLength;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacing() {
        return facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public MDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(MDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<MBlock> getModuleBlockList() {
        return moduleBlockList;
    }

    public void setModuleBlockList(List<MBlock> moduleBlockList) {
        this.moduleBlockList = moduleBlockList;
    }
}
