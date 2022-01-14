package com.i0dev.discordbot.bungee;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.StartupTag;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;

public class BungeeMain extends Plugin {

    Heart heart;

    @Override
    public void onEnable() {
        heart = new Heart(Arrays.asList(
                StartupTag.BUNGEE,
                StartupTag.PLUGIN
        ), getLogger(), this);
        heart.startup();
    }

    @Override
    public void onDisable() {
        heart.shutdown();
    }
}
