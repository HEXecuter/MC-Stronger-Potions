package com.hexecuter.minecraft.strongerpotions;

import com.hexecuter.minecraft.strongerpotions.recipes.EnhancePotionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class StrongerPotions extends JavaPlugin {
    public static StrongerPotions instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new EnhancePotionListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
