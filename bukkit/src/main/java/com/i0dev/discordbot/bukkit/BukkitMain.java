package com.i0dev.discordbot.bukkit;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.StartupTag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class BukkitMain extends JavaPlugin {
    Heart heart;

    @Override
    public void onEnable() {
        heart = new Heart(Arrays.asList(
                StartupTag.BUKKIT,
                StartupTag.PLUGIN
        ), Bukkit.getLogger(), this);
    }

    @Override
    public void onDisable() {
        heart.shutdown();
    }
}
