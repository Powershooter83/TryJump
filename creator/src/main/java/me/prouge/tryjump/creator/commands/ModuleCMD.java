package me.prouge.tryjump.creator.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Charsets;
import me.prouge.tryjump.creator.module.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ModuleCMD implements CommandExecutor {

    public static final byte NORTH = 2;
    public static final byte EAST = 5;
    public static final byte SOUTH = 3;
    public static final byte WEST = 4;
    @Inject
    private MSaver mSaver;
    private HashMap<Player, List<Location>> playerListHashMap = new HashMap<>();
    private HashMap<Player, String> nameToChat = new HashMap<>();

    public byte rotateFour(byte blockData, String facing, String direction) {
        byte newData = blockData;

        switch (facing) {
            case "N":
                switch (direction) {
                    case "E":
                        newData = rotateBlockClockwise(newData);
                        break;
                    case "S":
                        newData = rotateBlockClockwise(rotateBlockClockwise(newData));
                        break;
                    case "W":
                        newData = rotateBlockCounterClockwise(newData);
                        break;
                }
                break;
            case "E":
                switch (direction) {
                    case "N":
                        newData = rotateBlockCounterClockwise(newData);
                        break;
                    case "S":
                        newData = rotateBlockClockwise(newData);
                        break;
                    case "W":
                        newData = rotateBlockClockwise(rotateBlockClockwise(newData));
                        break;
                }
                break;
            case "S":
                switch (direction) {
                    case "N":
                        newData = rotateBlockClockwise(rotateBlockClockwise(newData));
                        break;
                    case "E":
                        newData = rotateBlockCounterClockwise(newData);
                        break;
                    case "W":
                        newData = rotateBlockClockwise(newData);
                        break;
                }
                break;
            case "W":
                switch (direction) {
                    case "N":
                        newData = rotateBlockClockwise(newData);
                        break;
                    case "E":
                        newData = rotateBlockClockwise(rotateBlockClockwise(newData));
                        break;
                    case "S":
                        newData = rotateBlockCounterClockwise(newData);
                        break;
                }
                break;
        }
        return newData;
    }

    private byte rotateBlockClockwise(byte blockData) {
        byte newData = blockData;

        switch (blockData) {
            case NORTH:
                newData = EAST;
                break;
            case EAST:
                newData = SOUTH;
                break;
            case SOUTH:
                newData = WEST;
                break;
            case WEST:
                newData = NORTH;
                break;
        }

        return newData;
    }

    private byte rotateBlockCounterClockwise(byte blockData) {
        byte newData = blockData;

        switch (blockData) {
            case NORTH:
                newData = WEST;
                break;
            case EAST:
                newData = NORTH;
                break;
            case SOUTH:
                newData = EAST;
                break;
            case WEST:
                newData = SOUTH;
                break;
        }

        return newData;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage("");
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("tryjump.setup")) {
            return false;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("setup")) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }

            player.sendMessage("§8§m--------------------------------------------");
            player.sendMessage("§8» §6Module Creator");
            player.sendMessage("§7Nutze folgende Befehle, um dein Module zu erstellen:");
            player.sendMessage("");
            player.sendMessage("§6➀ §7/module pos1 §8[§6Setzt die erste Position deines Modules!§8]");
            player.sendMessage("§6➁ §7/module pos2 §8[§6Setzt die zweite Position deines Modules!§8]");
            player.sendMessage("§6➂ §7/module save §8[§6Speichert das Module relativ zu deiner Position!§8]");
            player.sendMessage("§6➃ §7/module name §8<name> §8[§6Speichert den Module namen!§8]");
            player.sendMessage("§6➄ §7/module difficulty §8<Easy, Medium, Hard, Extreme>");
            player.sendMessage("§6➅ §7/module load §8<difficulty> §8<name>");
            player.sendMessage("§8§m--------------------------------------------");
            return false;
        }
        if (args[0].equalsIgnoreCase("pos1")) {
            player.sendMessage("§8» §6Module Creator §7| Die Position §6Pos1 §7wurde erfolgreich gespeichert!");
            this.playerListHashMap.computeIfAbsent(player, k -> new ArrayList<>());
            this.playerListHashMap.get(player).add(player.getLocation());
        }
        if (args[0].equalsIgnoreCase("pos2")) {
            player.sendMessage("§8» §6Module Creator §7| Die Position §6Pos2 §7wurde erfolgreich gespeichert!");
            this.playerListHashMap.computeIfAbsent(player, k -> new ArrayList<>());
            this.playerListHashMap.get(player).add(player.getLocation());
        }
        if (args[0].equalsIgnoreCase("save")) {
            player.sendMessage("§8» §6Module Creator §7| Das Module wurde relativ zu deiner Position gespeichert!");
            this.playerListHashMap.get(player).add(player.getLocation());
        }
        if (args[0].equalsIgnoreCase("name")) {
            StringBuilder name = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (args.length - 1 != i) {
                    name.append(args[i]).append(" ");
                } else {
                    name.append(args[i]);
                }
            }
            player.sendMessage("§8» §6Module Creator §7| Das Module wurde " + name + " §7benannt.");
            this.nameToChat.put(player, name.toString());
        }
        if (args[0].equalsIgnoreCase("difficulty")) {
            if (args[1].equalsIgnoreCase("easy") ||
                    args[1].equalsIgnoreCase("medium") ||
                    args[1].equalsIgnoreCase("hard") ||
                    args[1].equalsIgnoreCase("extreme")) {
                player.sendMessage("§8» §6Module Creator §7| Das Module wurde fertiggestellt!");

                mSaver.saveModule(this.playerListHashMap.get(player).get(0),
                        this.playerListHashMap.get(player).get(1)
                        , this.playerListHashMap.get(player).get(2),
                        this.nameToChat.get(player),
                        Enum.valueOf(MDifficulty.class, args[1].toUpperCase()));
            } else {
                player.sendMessage("§8» §6Module Creator §7| Folgende Schwierigkeiten gibt es: §8<Easy, Medium, Hard, Extreme>");
            }
        }
        if (args[0].equalsIgnoreCase("load")) {
            try {

                Module module = loadModule(args[2], args[1]);
                paste(module.getModuleBlockList(), args[3], player.getLocation(), module.getFacing());
                this.playerListHashMap.clear();
                this.nameToChat.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @SuppressWarnings("UnstableApiUsage")
    private Module loadModule(String name, String difficulty) throws IOException {
        File module = new File("plugins" +
                File.separator +
                "TryJump-Modules" +
                File.separator +
                difficulty + File.separator +
                name + ".module");
        String content = com.google.common.io.Files.asCharSource(module, Charsets.UTF_8).read();
        String[] contentSplit = content.split("000000000000000000");
        String[] splitInformation = contentSplit[0].split(";");

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Pattern.class, new PatternDeserializer());
        objectMapper.registerModule(simpleModule);

        return new Module(splitInformation[1],
                splitInformation[0],
                Enum.valueOf(MDifficulty.class, splitInformation[2].toUpperCase()),
                objectMapper.readValue(contentSplit[1], new TypeReference<List<MBlock>>() {
                }));
    }

    public void paste(List<MBlock> moduleBlockList, String direction, Location location, String facing) {
        if (direction.equals(facing)) {
            pasteTheObject(moduleBlockList, location);
            return;
        }
        List<MBlock> blocks = new ArrayList<>();
        moduleBlockList.forEach(mBlock -> blocks.add(new MBlock(mBlock.getPositionX(), mBlock.getPositionY(), mBlock.getPositionZ(), new MaterialData(mBlock.getMaterial(), mBlock.getData()))));
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
        blocks.sort((block1, block2) -> {
            Material mat1 = block1.getMaterial();
            Material mat2 = block2.getMaterial();
            boolean isSolid1 = mat1.isSolid();
            boolean isSolid2 = mat2.isSolid();
            if (isSolid1 && !isSolid2) {
                return -1;
            } else if (!isSolid1 && isSolid2) {
                return 1;
            } else {
                return 0;
            }
        });


        for (MBlock mBlock : blocks) {
            final Location relativeLoc = new Location(location.getWorld(),
                    mBlock.getPositionX(),
                    mBlock.getPositionY(),
                    mBlock.getPositionZ());
            final Block block1 = relativeLoc.add(location).getBlock();


            block1.setType(mBlock.getMaterial());
            block1.setData(mBlock.getData(), false);

            if (mBlock.getPlayerName() != null) {
                block1.setType(mBlock.getMaterial());
                block1.setData(mBlock.getData(), false);
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
        }

    }
}
