package com.hexecuter.minecraft.strongerpotions.listeners;

import com.hexecuter.minecraft.strongerpotions.StrongerPotions;
import com.hexecuter.minecraft.strongerpotions.helpers.HelperFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;


public class EnhancePotionListener implements Listener {

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstAnvilItem = anvilInventory.getItem(0);
        ItemStack secondAnvilItem = anvilInventory.getItem(1);

        if (firstAnvilItem == null || secondAnvilItem == null) {
            return;
        }

        ItemStack resultItem;
        int xpCost;
        if (HelperFunctions.isPotion(firstAnvilItem) && HelperFunctions.hasEffects(firstAnvilItem)) {
            if (secondAnvilItem.getType() == Material.RAW_COPPER || secondAnvilItem.getType() == Material.COPPER_INGOT) {
                resultItem = HelperFunctions.upgradePotion(firstAnvilItem, secondAnvilItem);
                xpCost = Math.max((secondAnvilItem.getAmount() / 2), 1);
            } else if (HelperFunctions.isPotion(secondAnvilItem) && HelperFunctions.hasEffects(secondAnvilItem)) {
                resultItem = HelperFunctions.mergePotions(firstAnvilItem, secondAnvilItem);
                xpCost = Math.max((resultItem.getAmount() / 2), 1);
            } else if (secondAnvilItem.getType() == Material.DIAMOND_BLOCK) {
                resultItem = HelperFunctions.addToStack(firstAnvilItem);
                xpCost = 10;
            } else {
                return;
            }
        } else {
            return;
        }

        if (resultItem != null) {
            event.setResult(resultItem);
            // Delay setting the repair cost because the client immediately sets it to 0
            // Without a repair cost, the client can not retrieve the item
            Bukkit.getScheduler().runTask(StrongerPotions.instance, () -> anvilInventory.setRepairCost(xpCost));
        }
    }
}
