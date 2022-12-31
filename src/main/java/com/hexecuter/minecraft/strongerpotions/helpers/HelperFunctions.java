package com.hexecuter.minecraft.strongerpotions.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class HelperFunctions {
    public static boolean isPotion(ItemStack item) {
        return item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION;
    }

    public static boolean hasEffects(ItemStack item) {
        PotionMeta itemPotionMeta = (PotionMeta) item.getItemMeta();
        return itemPotionMeta.hasCustomEffects() || itemPotionMeta.getBasePotionData().getType().getEffectType() != null;
    }

    public static ItemStack upgradePotion(ItemStack firstPotion, ItemStack upgradeMaterial) {
        ItemStack returnItem = firstPotion.clone();
        int materialAmount = upgradeMaterial.getAmount();
        PotionMeta firstPotionMeta = (PotionMeta) firstPotion.getItemMeta();
        PotionMeta returnPotionMeta = (PotionMeta) returnItem.getItemMeta();

        // Upgrade each custom effect first
        if (firstPotionMeta.hasCustomEffects()) {
            returnPotionMeta.clearCustomEffects();
            if (upgradeMaterial.getType() == Material.RAW_COPPER) {
                for (PotionEffect currentEffect : firstPotionMeta.getCustomEffects()) {
                    returnPotionMeta.addCustomEffect(
                            new PotionEffect(
                                    currentEffect.getType(),
                                    currentEffect.getDuration() + (6000 * materialAmount),
                                    currentEffect.getAmplifier()
                            ),
                            true
                    );
                }
            } else if (upgradeMaterial.getType() == Material.COPPER_INGOT) {
                for (PotionEffect currentEffect : firstPotionMeta.getCustomEffects()) {
                    returnPotionMeta.addCustomEffect(
                            new PotionEffect(
                                    currentEffect.getType(),
                                    currentEffect.getDuration(),
                                    currentEffect.getAmplifier() + materialAmount
                            ),
                            true
                    );
                }
            }
        }

        PotionData firstPotionData = firstPotionMeta.getBasePotionData();
        PotionEffectType firstEffectType = firstPotionData.getType().getEffectType();
        // If it has a base effect that is not already included in the custom effects
        if (firstEffectType != null && !firstPotionMeta.hasCustomEffect(firstEffectType)) {
            if (upgradeMaterial.getType() == Material.RAW_COPPER) {
                returnPotionMeta.addCustomEffect(
                        new PotionEffect(
                                firstEffectType,
                                firstPotionData.isExtended() ? 15000 + (6000 * materialAmount) : 9000 + (6000 * materialAmount),
                                firstPotionData.isUpgraded() ? 2 : 1
                        )
                        , true);
            } else if (upgradeMaterial.getType() == Material.COPPER_INGOT) {
                returnPotionMeta.addCustomEffect(
                        new PotionEffect(
                                firstEffectType,
                                firstPotionData.isExtended() ? 9000 : 4500,
                                firstPotionData.isUpgraded() ? 2 + materialAmount : 1 + materialAmount
                        )
                        , true);
            }
            returnPotionMeta.setBasePotionData(new PotionData(PotionType.WATER, false, false));
        }
        returnItem.setItemMeta(returnPotionMeta);
        return returnItem;
    }
}
