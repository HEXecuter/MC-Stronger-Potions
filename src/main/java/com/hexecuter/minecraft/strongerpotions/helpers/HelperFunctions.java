package com.hexecuter.minecraft.strongerpotions.helpers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.List;

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

    private static int calculateAmplifier(PotionEffect firstPotionEffect, PotionEffect secondPotionEffect) {
        return calculateAmplifier(firstPotionEffect.getAmplifier(), secondPotionEffect.getAmplifier());
    }

    private static int calculateAmplifier(PotionData firstPotionData, PotionData secondPotionData) {
        return calculateAmplifier(firstPotionData.isUpgraded() ? 2 : 1, secondPotionData.isUpgraded() ? 2 : 1);
    }

    private static int calculateAmplifier(int firstNum, int secondNum) {
        if (firstNum == secondNum) {
            return secondNum + 1;
        } else {
            return Math.max(firstNum, secondNum);
        }
    }

    private static int calculateDuration(PotionEffect firstPotionEffect, PotionEffect secondPotionEffect) {
        return calculateDuration(firstPotionEffect.getDuration(), secondPotionEffect.getDuration());
    }

    private static int calculateDuration(PotionData firstPotionData, PotionData secondPotionData) {
        return calculateDuration(
                firstPotionData.isExtended() ? 12000 : 6000,
                secondPotionData.isExtended() ? 12000 : 6000
        );
    }

    private static int calculateDuration(int firstDur, int secondDur) {
        if (firstDur == secondDur) {
            return firstDur + 6000;
        } else {
            return Math.max(firstDur, secondDur);
        }
    }

    private static PotionEffect transferPotionDataToCustomEffect(PotionData potionData) {
        return new PotionEffect(
                potionData.getType().getEffectType(),
                potionData.isExtended() ? 15000 : 9000,
                potionData.isUpgraded() ? 2 : 1
        );
    }

    private static HashMap<PotionEffectType, PotionEffect> effectsListToMap(List<PotionEffect> customEffectsList) {
        HashMap<PotionEffectType, PotionEffect> returnMap = new HashMap<>();
        for (PotionEffect currentEffect : customEffectsList) {
            returnMap.put(currentEffect.getType(), currentEffect);
        }
        return returnMap;
    }

    public static ItemStack mergePotions(ItemStack firstPotion, ItemStack secondPotion) {
        PotionMeta firstPotionMeta = (PotionMeta) firstPotion.getItemMeta();
        PotionMeta secondPotionMeta = (PotionMeta) secondPotion.getItemMeta();
        ItemStack returnPotion = firstPotion.clone();
        PotionMeta returnPotionMeta = (PotionMeta) returnPotion.getItemMeta();
        returnPotionMeta.clearCustomEffects();
        // Transfer base effects from both potions into the custom effect of the resulting potion
        if (firstPotionMeta.getBasePotionData().getType().getEffectType() != null && firstPotionMeta.getBasePotionData().getType().getEffectType() == secondPotionMeta.getBasePotionData().getType().getEffectType()) {
            returnPotionMeta.addCustomEffect(
                    new PotionEffect(
                            firstPotionMeta.getBasePotionData().getType().getEffectType(),
                            calculateDuration(firstPotionMeta.getBasePotionData(), secondPotionMeta.getBasePotionData()),
                            calculateAmplifier(firstPotionMeta.getBasePotionData(), secondPotionMeta.getBasePotionData())
                    ),
                    true
            );
        } else {
            if (firstPotionMeta.getBasePotionData().getType().getEffectType() != null) {
                returnPotionMeta.addCustomEffect(transferPotionDataToCustomEffect(firstPotionMeta.getBasePotionData()), true);
            }
            if (secondPotionMeta.getBasePotionData().getType().getEffectType() != null) {
                returnPotionMeta.addCustomEffect(transferPotionDataToCustomEffect(secondPotionMeta.getBasePotionData()), true);
            }
        }

        // Transfer each custom effect from both potions into the resulting potions
        HashMap<PotionEffectType, PotionEffect> secondPotionEffectsMap = effectsListToMap(secondPotionMeta.getCustomEffects());
        for (PotionEffect currentEffect : firstPotionMeta.getCustomEffects()) {
            // If the effect already exists in the resulting potion, transfer the strongest duration and amplifier
            if (secondPotionEffectsMap.containsKey(currentEffect.getType())) {
                int amplifier = calculateAmplifier(currentEffect, secondPotionEffectsMap.get(currentEffect.getType()));
                int duration = calculateDuration(currentEffect, secondPotionEffectsMap.get(currentEffect.getType()));
                returnPotionMeta.addCustomEffect(new PotionEffect(currentEffect.getType(), duration, amplifier), true);
                secondPotionEffectsMap.remove(currentEffect.getType());
            } else {
                returnPotionMeta.addCustomEffect(currentEffect, true);
            }
        }
        for (PotionEffect currentEffect : secondPotionEffectsMap.values()) {
            returnPotionMeta.addCustomEffect(currentEffect, true);
        }
        returnPotionMeta.setBasePotionData(new PotionData(PotionType.WATER, false, false));
        returnPotion.setItemMeta(returnPotionMeta);
        returnPotion.setAmount(Math.min(firstPotion.getAmount() + secondPotion.getAmount() - 1, 64));
        return returnPotion;
    }
}
