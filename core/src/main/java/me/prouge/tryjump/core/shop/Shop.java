package me.prouge.tryjump.core.shop;

import me.prouge.tryjump.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class Shop {

    public void openShop(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 27, "§6>> §eTryJump Shop");
        initializeCategories(inventory);

        player.openInventory(inventory);
    }


    private void initializeCategories(final Inventory inventory) {
        inventory.setItem(0, new ItemBuilder(Material.GOLD_SWORD, 1).setName("§bWaffen").toItemStack());
        inventory.setItem(1, new ItemBuilder(Material.GOLD_SWORD, 1).setName("§bLederrüstung").toItemStack());
        inventory.setItem(2, new ItemBuilder(Material.GOLD_SWORD, 1).setName("§bEisenrüstung").toItemStack());
        inventory.setItem(6, new ItemBuilder(Material.GOLD_SWORD, 1).setName("§bNahrung").toItemStack());
        inventory.setItem(8, new ItemBuilder(Material.GOLD_SWORD, 1).setName("§Spezial").toItemStack());
    }


    public void openWeapons(final Player p) {
        Inventory inventory = p.getInventory();
        clearSecondRow(inventory);

        inventory.setItem(9, new ItemBuilder(Material.WOOD_SWORD, 1).toItemStack());
        inventory.setItem(10, new ItemBuilder(Material.STONE_SWORD, 1).toItemStack());
        inventory.setItem(11, new ItemBuilder(Material.IRON_SWORD, 1).toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.BOW, 1).toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.ARROW, 1).toItemStack());
        inventory.setItem(16, new ItemBuilder(Material.FISHING_ROD, 1).toItemStack());
        inventory.setItem(17, new ItemBuilder(Material.TNT, 1).toItemStack());
    }

    public void openLeatherArmor(final Player p) {
        Inventory inventory = p.getInventory();
        clearSecondRow(inventory);

        inventory.setItem(11, new ItemBuilder(Material.LEATHER_HELMET, 1).toItemStack());
        inventory.setItem(12, new ItemBuilder(Material.LEATHER_LEGGINGS, 1).toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.LEATHER_BOOTS, 1).toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.LEATHER_CHESTPLATE, 1).toItemStack());
    }

    public void openIronArmor(final Player p) {
        Inventory inventory = p.getInventory();
        clearSecondRow(inventory);

        inventory.setItem(11, new ItemBuilder(Material.IRON_HELMET, 1).toItemStack());
        inventory.setItem(12, new ItemBuilder(Material.IRON_LEGGINGS, 1).toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.IRON_BOOTS, 1).toItemStack());
        inventory.setItem(14, new ItemBuilder(Material.IRON_CHESTPLATE, 1).toItemStack());
    }

    public void openFood(final Player p) {
        Inventory inventory = p.getInventory();
        clearSecondRow(inventory);

        inventory.setItem(11, new ItemBuilder(Material.APPLE, 1).toItemStack());
        inventory.setItem(12, new ItemBuilder(Material.COOKED_BEEF, 1).toItemStack());
        inventory.setItem(13, new ItemBuilder(Material.CAKE, 1).toItemStack());
        inventory.setItem(15, new ItemBuilder(Material.GOLDEN_APPLE, 1).toItemStack());
    }

    public void openSpecial(final Player p) {
        Inventory inventory = p.getInventory();
        clearSecondRow(inventory);

        inventory.setItem(13, new ItemBuilder(Material.RED_ROSE, 1).toItemStack());
    }

    private void clearSecondRow(Inventory inventory) {
        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, null);
        }
    }

}
