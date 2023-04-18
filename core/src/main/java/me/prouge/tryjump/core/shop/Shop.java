package me.prouge.tryjump.core.shop;

import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.ItemBuilder;
import me.prouge.tryjump.core.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;


public class Shop {

    @Inject
    private ChatWriter chatWriter;

    @Inject
    private GameImpl game;


    public void openShop(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 27, "§6>> §eTryJump Shop");
        initializeCategories(inventory, game.getTryPlayer(player));

        player.openInventory(inventory);
    }


    private void initializeCategories(final Inventory inventory, TryJumpPlayer tp) {
        inventory.setItem(0, new ItemBuilder(Material.GOLD_SWORD).setName(chatWriter.getItemStackName(tp, Message.SHOP_CATEGORY_WEAPONS)).toItemStack());
        inventory.setItem(1, new ItemBuilder(Material.LEATHER_CHESTPLATE).setName(chatWriter.getItemStackName(tp, Message.SHOP_CATEGORY_LEATHER_ARMOR)).toItemStack());
        inventory.setItem(2, new ItemBuilder(Material.CHAINMAIL_HELMET).setName(chatWriter.getItemStackName(tp, Message.SHOP_CATEGORY_CHAIN_ARMOR)).toItemStack());
        inventory.setItem(3, new ItemBuilder(Material.IRON_CHESTPLATE).setName(chatWriter.getItemStackName(tp, Message.SHOP_CATEGORY_IRON_ARMOR)).toItemStack());
        inventory.setItem(5, new ItemBuilder(Material.CAKE).setName(chatWriter.getItemStackName(tp, Message.SHOP_CATEGORY_FOOD)).toItemStack());
        inventory.setItem(6, new ItemBuilder(Material.POTION).setName(chatWriter.getItemStackName(tp, Message.SHOP_CATEGORY_POISON)).toItemStack());
        inventory.setItem(8, new ItemBuilder(Material.NETHER_STAR).setName(chatWriter.getItemStackName(tp, Message.SHOP_CATEGORY_SPECIAL)).toItemStack());
    }


    public void openWeapons(final Inventory inventory) {
        clearSecondRow(inventory);

        inventory.setItem(9, new ItemBuilder(Material.WOOD_SWORD).addLoreLine("40").toItemStack());
        inventory.setItem(10, new ItemBuilder(Material.STONE_SWORD).addLoreLine("200").toItemStack());
        inventory.setItem(11, new ItemBuilder(Material.IRON_SWORD).addLoreLine("400").toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.BOW).addLoreLine("150").toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.ARROW).addLoreLine("8").toItemStack());
        inventory.setItem(15, new ItemBuilder(Material.WEB).addLoreLine("30").toItemStack());
        inventory.setItem(16, new ItemBuilder(Material.FISHING_ROD).addLoreLine("100").toItemStack());
        inventory.setItem(17, new ItemBuilder(Material.TNT).addLoreLine("80").toItemStack());

    }

    public void openLeatherArmor(final Inventory inventory) {
        clearSecondRow(inventory);

        inventory.setItem(11, new ItemBuilder(Material.LEATHER_BOOTS).addLoreLine("40").toItemStack());
        inventory.setItem(12, new ItemBuilder(Material.LEATHER_LEGGINGS).addLoreLine("50").toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.LEATHER_CHESTPLATE).addLoreLine("60").toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.LEATHER_HELMET).addLoreLine("40").toItemStack());
    }

    public void openChainArmmor(final Inventory inventory) {
        clearSecondRow(inventory);

        inventory.setItem(11, new ItemBuilder(Material.CHAINMAIL_BOOTS).setLore("100").toItemStack());
        inventory.setItem(12, new ItemBuilder(Material.CHAINMAIL_LEGGINGS).setLore("120").toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setLore("150").toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.CHAINMAIL_HELMET).setLore("100").toItemStack());
    }


    public void openIronArmor(final Inventory inventory) {
        clearSecondRow(inventory);

        inventory.setItem(11, new ItemBuilder(Material.IRON_BOOTS).addLoreLine("200").toItemStack());
        inventory.setItem(12, new ItemBuilder(Material.IRON_LEGGINGS).addLoreLine("200").toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.IRON_CHESTPLATE).addLoreLine("300").toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.IRON_HELMET).addLoreLine("300").toItemStack());
    }

    public void openFood(final Inventory inventory) {
        clearSecondRow(inventory);

        inventory.setItem(11, new ItemBuilder(Material.APPLE, 2).addLoreLine("2").toItemStack());
        inventory.setItem(12, new ItemBuilder(Material.COOKED_BEEF, 2).addLoreLine("8").toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.CAKE).addLoreLine("8").toItemStack());
        inventory.setItem(15, new ItemBuilder(Material.GOLDEN_APPLE).addLoreLine("100").toItemStack());
    }

    public void openPotions(final Inventory inventory) {
        clearSecondRow(inventory);

        inventory.setItem(10, new ItemBuilder(new ItemStack(Material.POTION, 2, (short) 8197)).setLore("140").toItemStack());
        inventory.setItem(11, new ItemBuilder(new ItemStack(Material.POTION, 3, (short) 8229)).setLore("200").toItemStack());
        inventory.setItem(14, new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16396)).setLore("180").toItemStack());
        inventory.setItem(15, new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16428)).setLore("250").toItemStack());
    }


    public void openSpecial(final Inventory inventory) {
        clearSecondRow(inventory);

        inventory.setItem(13, new ItemBuilder(Material.RED_ROSE, 1).setName("Extra Leben").addLoreLine("200").toItemStack());
    }

    private void clearSecondRow(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, null);
        }
    }
}
