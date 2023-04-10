package me.prouge.tryjump.core.events;

import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.shop.Shop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.inject.Inject;

public class ShopHandler implements Listener {

    @Inject
    private Shop shop;

    @Inject
    private GameImpl gameImpl;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equals("§6>> §eTryJump Shop") ||
                e.getClick().isKeyboardClick()) {
            return;
        }

        switch (e.getRawSlot()) {
            case 0:
                shop.openWeapons(e.getClickedInventory());
                break;
            case 1:
                shop.openLeatherArmor(e.getClickedInventory());
                break;
            case 2:
                shop.openChainArmmor(e.getClickedInventory());
                break;
            case 3:
                shop.openIronArmor(e.getClickedInventory());
                break;
            case 5:
                shop.openFood(e.getClickedInventory());
                break;
            case 6:
                shop.openPotions(e.getClickedInventory());
                break;
            case 8:
                shop.openSpecial(e.getClickedInventory());
                break;
            default:
                Player player = (Player) e.getWhoClicked();
                TryJumpPlayer tryPlayer = gameImpl.getTryPlayer(player);

                int price = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(0));

                if (tryPlayer.getTokens() >= price) {
                    tryPlayer.setTokens(tryPlayer.getTokens() - price);

                    if (e.getCurrentItem().getType() == Material.RED_ROSE) {
                        player.setMaxHealth(player.getMaxHealth() + 2);
                        player.setHealth(player.getMaxHealth());
                        return;
                    }
                    player.getInventory().addItem(e.getCurrentItem());
                }
        }
    }
}
