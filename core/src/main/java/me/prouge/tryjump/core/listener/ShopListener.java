package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.shop.Shop;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.Message;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scoreboard.Scoreboard;

import javax.inject.Inject;

public class ShopListener implements Listener {

    @Inject
    private Shop shop;

    @Inject
    private ChatWriter chatWriter;

    @Inject
    private GameImpl gameImpl;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equals("§6>> §eTryJump Shop") ||
                e.getClick().isKeyboardClick()) {
            return;
        }
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();

        switch (e.getRawSlot()) {
            case 0:
                shop.openWeapons(e.getClickedInventory());
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1 ,1);
                break;
            case 1:
                shop.openLeatherArmor(e.getClickedInventory());
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1 ,1);
                break;
            case 2:
                shop.openChainArmmor(e.getClickedInventory());
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1 ,1);
                break;
            case 3:
                shop.openIronArmor(e.getClickedInventory());
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1 ,1);
                break;
            case 5:
                shop.openFood(e.getClickedInventory());
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1 ,1);
                break;
            case 6:
                shop.openPotions(e.getClickedInventory());
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1 ,1);
                break;
            case 8:
                shop.openSpecial(e.getClickedInventory());
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1 ,1);
                break;
            default:
                if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR){
                    return;
                }
                TryJumpPlayer tryPlayer = gameImpl.getTryPlayer(player);

                int price = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(0));

                if (tryPlayer.getTokens() >= price) {
                    tryPlayer.getPlayer().playSound(tryPlayer.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1 ,1);

                    tryPlayer.setTokens(tryPlayer.getTokens() - price);
                    updateScore(player);

                    if (e.getCurrentItem().getType() == Material.RED_ROSE) {
                        player.setMaxHealth(player.getMaxHealth() + 2);
                        player.setHealth(player.getMaxHealth());
                        return;
                    }
                    player.getInventory().addItem(e.getCurrentItem());
                } else {
                    chatWriter.print(tryPlayer, Message.LOBBY_SHOP_MONEY, null);
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
                }
        }
    }

    private void updateScore(Player player) {
        Scoreboard board = player.getScoreboard();
        int tokens = gameImpl.getTryPlayer(player).getTokens();

        player.setLevel(tokens);
        board.getTeam(player.getName()).setSuffix(String.valueOf(tokens));

        gameImpl.setTablist();
    }

}