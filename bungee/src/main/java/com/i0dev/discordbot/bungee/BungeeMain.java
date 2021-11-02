package com.i0dev.discordbot.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {


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
