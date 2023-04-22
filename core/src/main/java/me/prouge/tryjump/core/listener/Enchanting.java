package me.prouge.tryjump.core.listener;

import me.prouge.tryjump.core.game.GameImpl;
import me.prouge.tryjump.core.game.Phase;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import me.prouge.tryjump.core.utils.ChatWriter;
import me.prouge.tryjump.core.utils.Message;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.ItemBow;
import net.minecraft.server.v1_8_R3.ItemFishingRod;
import net.minecraft.server.v1_8_R3.ItemSword;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Enchanting implements Listener {

    @Inject
    private GameImpl gameManager;

    @Inject
    private ChatWriter chatWriter;

    private List<Enchantment> protections = Arrays.asList(
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.PROTECTION_PROJECTILE,
            Enchantment.PROTECTION_FIRE
    );


    @EventHandler
    public void onRightClickWithEnchantingTable(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.ENCHANTMENT_TABLE ||
                gameManager.getGamePhase() != Phase.Game_shop) {
            return;
        }
        event.setCancelled(true);

        if (event.getItem() == null) {
            return;
        }

        enchantItem(event.getItem(), event.getPlayer());
    }


    private void enchantItem(ItemStack item, Player player) {
        TryJumpPlayer tryPlayer = gameManager.getTryPlayer(player);

        if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemSword) {
            if (tryPlayer.isNextSwordPrice() && tryPlayer.getTokens() < 400
                    || !tryPlayer.isNextSwordPrice() && tryPlayer.getTokens() < 250) {
                chatWriter.print(tryPlayer, Message.SHOP_NOT_ENOUGH_MONEY_ENCHANTMENT, null);
                return;
            }

            if (tryPlayer.isNextSwordPrice()) {
                tryPlayer.setTokens(tryPlayer.getTokens() - 400);
            }

            if (!tryPlayer.isNextSwordPrice()) {
                tryPlayer.setTokens(tryPlayer.getTokens() - 250);
                tryPlayer.setNextSwordPrice(true);
            }
            chatWriter.print(tryPlayer, Message.SHOP_NEXT_ENCHANTMENT, new String[][]{{"ITEM", "Schwert"}});

            ItemMeta meta = item.getItemMeta();

            int sharpnessLevel = Math.min(meta.getEnchantLevel(Enchantment.DAMAGE_ALL) + 1, Enchantment.DAMAGE_ALL.getMaxLevel());
            meta.addEnchant(Enchantment.DAMAGE_ALL, sharpnessLevel, true);


            if (Math.random() < 0.5) {
                int knockbackLevel = Math.min(meta.getEnchantLevel(Enchantment.KNOCKBACK) + 1, Enchantment.KNOCKBACK.getMaxLevel());
                meta.addEnchant(Enchantment.KNOCKBACK, knockbackLevel, true);
            }

            if (Math.random() < 0.05) {
                int fireAspectLevel = Math.min(meta.getEnchantLevel(Enchantment.FIRE_ASPECT) + 1, Enchantment.FIRE_ASPECT.getMaxLevel());
                meta.addEnchant(Enchantment.FIRE_ASPECT, fireAspectLevel, true);
            }
            item.setItemMeta(meta);
        }
        if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemBow) {
            if (tryPlayer.isNextBowPrice() && tryPlayer.getTokens() < 400
                    || !tryPlayer.isNextBowPrice() && tryPlayer.getTokens() < 250) {
                chatWriter.print(tryPlayer, Message.SHOP_NOT_ENOUGH_MONEY_ENCHANTMENT, null);
                return;
            }

            if (tryPlayer.isNextBowPrice()) {
                tryPlayer.setTokens(tryPlayer.getTokens() - 400);
            }

            if (!tryPlayer.isNextBowPrice()) {
                tryPlayer.setTokens(tryPlayer.getTokens() - 250);
                tryPlayer.setNextBowPrice(true);
            }
            chatWriter.print(tryPlayer, Message.SHOP_NEXT_ENCHANTMENT, new String[][]{{"ITEM", "Bogen"}});


            ItemMeta meta = item.getItemMeta();

            int powerLevel = Math.min(meta.getEnchantLevel(Enchantment.ARROW_DAMAGE) + 1, Enchantment.ARROW_DAMAGE.getMaxLevel());
            meta.addEnchant(Enchantment.ARROW_DAMAGE, powerLevel, true);

            if (Math.random() < 0.5) {
                int punchLevel = Math.min(meta.getEnchantLevel(Enchantment.ARROW_KNOCKBACK) + 1, Enchantment.ARROW_KNOCKBACK.getMaxLevel());
                meta.addEnchant(Enchantment.ARROW_KNOCKBACK, punchLevel, true);
            }

            if (Math.random() < 0.1) {
                int infinityLevel = Math.min(meta.getEnchantLevel(Enchantment.ARROW_INFINITE) + 1, Enchantment.ARROW_INFINITE.getMaxLevel());
                meta.addEnchant(Enchantment.ARROW_INFINITE, infinityLevel, true);
            }

            if (Math.random() < 0.05) {
                int flameLevel = Math.min(meta.getEnchantLevel(Enchantment.ARROW_FIRE) + 1, Enchantment.ARROW_FIRE.getMaxLevel());
                meta.addEnchant(Enchantment.ARROW_FIRE, flameLevel, true);
            }
            item.setItemMeta(meta);
        }
        if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemFishingRod) {
            if (tryPlayer.isNextFishingRodPrice() && tryPlayer.getTokens() < 400
                    || !tryPlayer.isNextFishingRodPrice() && tryPlayer.getTokens() < 250) {
                chatWriter.print(tryPlayer, Message.SHOP_NOT_ENOUGH_MONEY_ENCHANTMENT, null);
                return;
            }

            if (tryPlayer.isNextFishingRodPrice()) {
                tryPlayer.setTokens(tryPlayer.getTokens() - 400);
            }

            if (!tryPlayer.isNextFishingRodPrice()) {
                tryPlayer.setTokens(tryPlayer.getTokens() - 250);
                tryPlayer.setNextFishingRodPrice(true);
            }
            chatWriter.print(tryPlayer, Message.SHOP_NEXT_ENCHANTMENT, new String[][]{{"ITEM", "Angel"}});


            ItemMeta meta = item.getItemMeta();

            int knockbackLevel = Math.min(meta.getEnchantLevel(Enchantment.KNOCKBACK) + 1, 3);
            meta.addEnchant(Enchantment.KNOCKBACK, knockbackLevel, true);
            item.setItemMeta(meta);
        }
        if (CraftItemStack.asNMSCopy(item).getItem() instanceof ItemArmor) {
            if (tryPlayer.getTokens() < 250) {
                chatWriter.print(tryPlayer, Message.SHOP_NOT_ENOUGH_MONEY_ENCHANTMENT, null);
                return;
            }
            tryPlayer.setTokens(tryPlayer.getTokens() - 250);
            ItemMeta meta = item.getItemMeta();
            Collections.shuffle(protections);

            for (Enchantment e : protections) {
                if (meta.hasEnchant(e)) {
                    int level = Math.min(meta.getEnchantLevel(e) + 1, e.getMaxLevel());
                    meta.addEnchant(e, level, true);
                }
            }

            for (int i = 0; i < new Random().nextInt(protections.size()); i++) {
                Enchantment protection = protections.get(i);

                if (meta.hasEnchant(protection)) {
                    continue;
                }
                meta.addEnchant(protection, i + 1, true);
            }

            if (Math.random() < 0.15) {
                int level = Math.min(meta.getEnchantLevel(Enchantment.THORNS) + 1, Enchantment.THORNS.getMaxLevel());
                meta.addEnchant(Enchantment.THORNS, level, true);
            }

            if (item.getType().name().endsWith("_BOOTS")) {
                if (Math.random() < 0.3) {
                    int level = Math.min(meta.getEnchantLevel(Enchantment.PROTECTION_FALL) + 1, Enchantment.PROTECTION_FALL.getMaxLevel());
                    meta.addEnchant(Enchantment.PROTECTION_FALL, level, true);
                }
            }
            item.setItemMeta(meta);
        }
        updateScore(player);
    }

    private void updateScore(Player player) {
        Scoreboard board = player.getScoreboard();
        int tokens = gameManager.getTryPlayer(player).getTokens();

        player.setLevel(tokens);
        board.getTeam(player.getName()).setSuffix(String.valueOf(tokens));

        gameManager.setTablist();
    }

}
