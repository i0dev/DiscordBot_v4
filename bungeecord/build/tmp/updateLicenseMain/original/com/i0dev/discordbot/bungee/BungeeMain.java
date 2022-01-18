package com.i0dev.discordbot.bungee;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.bungee.command.CmdLink;
import com.i0dev.discordbot.object.StartupTag;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;

public class BungeeMain extends Plugin {

    Heart heart;

    @Override
    public void onEnable() {
        System.out.println("[DEBUG] BungeeMain.onEnable() - enabled");
        heart = new Heart(Arrays.asList(
                StartupTag.BUNGEE,
                StartupTag.PLUGIN
        ), getLogger(), this);

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CmdLink(heart, "Link"));
    }


    @Override
    public void onDisable() {
        heart.shutdown();
    }
}
