package com.i0dev.discordbot.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMain extends JavaPlugin {


    @Override
    public void onEnable() {
        super.onEnable();
        System.out.println("ENABLED DISCORD BOT");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
