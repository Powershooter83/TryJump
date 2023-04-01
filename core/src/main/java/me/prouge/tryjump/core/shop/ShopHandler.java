package me.prouge.tryjump.core.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;

public class ShopHandler implements Listener {

    @Inject
    private Shop shop;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getInventory().getName().equals("§6>> §eTryJump Shop")){
            return;
        }

        final Player player = (Player) e.getWhoClicked();

        switch (e.getRawSlot()){
            case 0:
                shop.openWeapons(player);
            case 1:
                shop.openLeatherArmor(player);
            case 2:
                shop.openIronArmor(player);
            case 6:
                shop.openFood(player);
            case 8:
                shop.openSpecial(player);
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        shop.openShop(event.getPlayer());
    }
}
