package me.prouge.tryjump.creator.module;

import org.bukkit.Location;
import org.bukkit.SkullType;
import org.bukkit.block.*;
import org.bukkit.material.MaterialData;

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
        this.moduleBlockList.forEach(mBlock -> blocks.add(new MBlock(mBlock.getPositionX(), mBlock.getPositionY(), mBlock.getPositionZ(), new MaterialData(mBlock.getMaterial(), mBlock.getData()))));
        if (facing.equals("N") && direction.equals("E")) {
            blocks.forEach(mBlock -> mBlock.rotateBlock(-mBlock.getPositionZ(), mBlock.getPositionX()));
        }
        if (facing.equals("N") && direction.equals("S") ||
                facing.equals("E") && direction.equals("W") ||
                facing.equals("S") && direction.equals("N") ||
                facing.equals("W") && direction.equals("E")) {
            blocks.forEach(mBlock -> mBlock.rotateBlock(-mBlock.getPositionX(), -mBlock.getPositionZ()));
        }
        if (facing.equals("N") && direction.equals("W") ||
                facing.equals("E") && direction.equals("N") ||
                facing.equals("S") && direction.equals("E") ||
                facing.equals("W") && direction.equals("S")) {
            blocks.forEach(mBlock -> mBlock.rotateBlock(mBlock.getPositionZ(), -mBlock.getPositionX()));
        }
        if (facing.equals("E") && direction.equals("S") ||
                facing.equals("S") && direction.equals("W") ||
                facing.equals("W") && direction.equals("N")) {
            blocks.forEach(mBlock -> mBlock.rotateBlock(-mBlock.getPositionZ(), mBlock.getPositionX()));

        }
        pasteTheObject(blocks, location);
    }


    @SuppressWarnings("deprecation")
    private void pasteTheObject(List<MBlock> blocks, Location location) {
        blocks.forEach(mBlock -> {
            final Location relativeLoc = new Location(location.getWorld(),
                    mBlock.getPositionX(),
                    mBlock.getPositionY(),
                    mBlock.getPositionZ());
            final Block block1 = relativeLoc.add(location).getBlock();


            block1.setType(mBlock.getMaterial());
            block1.setData(mBlock.getData());

            if (mBlock.getPlayerName() != null) {
                block1.setType(mBlock.getMaterial());
                block1.setData(mBlock.getData());
                BlockState state = block1.getState();
                Skull skull = (Skull) state;
                skull.setSkullType(SkullType.PLAYER);
                skull.setOwner(mBlock.getPlayerName());
                skull.setRotation(mBlock.getBlockFace());
                skull.update(true);
                block1.getState().update(true);
            }

            if (mBlock.getBaseColor() != null) {
                BlockState state = block1.getState();
                Banner banner = (Banner) state;
                banner.setPatterns(mBlock.getPatterns());
                banner.setBaseColor(mBlock.getBaseColor());
                banner.update(true);
                block1.getState().update(true);

            }

            if (mBlock.getLines() != null) {
                BlockState state = block1.getState();
                Sign sign = (Sign) state;

                for (int i = 0; i < mBlock.getLines().length; i++) {
                    sign.setLine(i, mBlock.getLines()[i]);
                }
                sign.update(true);
                block1.getState().update(true);
            }
        });

    }

    public int getMaxLength() {
        int maxLength = 0;
        int minPosition = 0;
        int maxPosition = 0;
        switch (facing) {
            case "N":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionZ));
                minPosition = moduleBlockList.get(0).getPositionZ();
                maxPosition = moduleBlockList.get(moduleBlockList.size() - 1).getPositionZ();
                break;
            case "W":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionX));
                minPosition = moduleBlockList.get(moduleBlockList.size() - 1).getPositionX();
                maxPosition = moduleBlockList.get(0).getPositionX();
                break;
            case "E":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionX));
                minPosition = moduleBlockList.get(0).getPositionX();
                maxPosition = moduleBlockList.get(moduleBlockList.size() - 1).getPositionX();
                break;
            case "S":
                moduleBlockList.sort(Comparator.comparingInt(MBlock::getPositionZ));
                minPosition = moduleBlockList.get(moduleBlockList.size() - 1).getPositionZ();
                maxPosition = moduleBlockList.get(0).getPositionZ();
                break;
        }
        maxLength = Math.abs(maxPosition - minPosition);
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
