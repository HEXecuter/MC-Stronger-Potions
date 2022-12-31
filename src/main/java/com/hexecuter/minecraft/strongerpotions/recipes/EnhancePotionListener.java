package com.hexecuter.minecraft.strongerpotions.recipes;

import com.hexecuter.minecraft.strongerpotions.StrongerPotions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class EnhancePotionListener implements Listener {

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent event) {
        boolean validRecipeFlag = true;
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstAnvilItem = anvilInventory.getItem(0);
        ItemStack secondAnvilItem = anvilInventory.getItem(1);

        if (firstAnvilItem == null || secondAnvilItem == null) {
            return;
        }
        if (firstAnvilItem.getType() != Material.POTION &&
                firstAnvilItem.getType() != Material.LINGERING_POTION &&
                firstAnvilItem.getType() != Material.SPLASH_POTION) {
            return;
        }
        if (secondAnvilItem.getType() != Material.RAW_COPPER && secondAnvilItem.getType() != Material.COPPER_INGOT) {
            return;
        }

        ItemStack resultItem = firstAnvilItem.clone();
        PotionMeta firstItemPotionMeta = (PotionMeta) firstAnvilItem.getItemMeta();
        PotionMeta resultPotionMeta = (PotionMeta) resultItem.getItemMeta();
        // Make sure all existing custom effects are also enhanced
        if (firstItemPotionMeta.hasCustomEffects()) {
            resultPotionMeta.clearCustomEffects();
            if (secondAnvilItem.getType() == Material.RAW_COPPER) {
                for (PotionEffect currentEffect : firstItemPotionMeta.getCustomEffects()) {
                    resultPotionMeta.addCustomEffect(
                            new PotionEffect(
                                    currentEffect.getType(),
                                    currentEffect.getDuration() + 6000,
                                    currentEffect.getAmplifier()
                            ),
                            true
                    );
                }
            } else if (secondAnvilItem.getType() == Material.COPPER_INGOT) {
                for (PotionEffect currentEffect : firstItemPotionMeta.getCustomEffects()) {
                    resultPotionMeta.addCustomEffect(
                            new PotionEffect(
                                    currentEffect.getType(),
                                    currentEffect.getDuration(),
                                    currentEffect.getAmplifier() + 1
                            ),
                            true
                    );
                }
            }
        }


        PotionData firstItemPotionData = firstItemPotionMeta.getBasePotionData();
        PotionEffectType firstItemEffectType = firstItemPotionData.getType().getEffectType();
        if (firstItemEffectType == null && !firstItemPotionMeta.hasCustomEffects()) {
            validRecipeFlag = false;
        }
        // Short circuit checking if the effect type is null, so it is not passed to hasCustomEffect
        if (firstItemEffectType != null && !firstItemPotionMeta.hasCustomEffect(firstItemEffectType)) {
            if (secondAnvilItem.getType() == Material.RAW_COPPER) {
                resultPotionMeta.addCustomEffect(
                        new PotionEffect(
                                firstItemPotionData.getType().getEffectType(),
                                firstItemPotionData.isExtended() ? 15000 : 9000,
                                firstItemPotionData.isUpgraded() ? 2 : 1
                        )
                        , true);
            } else if (secondAnvilItem.getType() == Material.COPPER_INGOT) {
                resultPotionMeta.addCustomEffect(
                        new PotionEffect(
                                firstItemPotionData.getType().getEffectType(),
                                firstItemPotionData.isExtended() ? 9000 : 4500,
                                firstItemPotionData.isUpgraded() ? 3 : 2
                        )
                        , true);
            }
            resultPotionMeta.setBasePotionData(new PotionData(PotionType.WATER, false, false));
        }


        if (validRecipeFlag) {
            resultItem.setItemMeta(resultPotionMeta);
            event.setResult(resultItem);
            // Delay setting the repair cost because the client immediately sets it to 0
            // Without a repair cost, the client can not retrieve the item
            Bukkit.getScheduler().runTask(StrongerPotions.instance, () -> anvilInventory.setRepairCost(1));
        }

    }
}
