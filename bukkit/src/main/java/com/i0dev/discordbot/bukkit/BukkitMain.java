package com.i0dev.discordbot.bukkit;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.StartupTag;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.Arrays;

public class BukkitMain extends JavaPlugin {

    Heart heart;

    @Override
    public void onEnable() {
        heart = new Heart(Arrays.asList(
                StartupTag.BUKKIT,
                StartupTag.PLUGIN
        ), Bukkit.getLogger(), this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            OSProcess process = new SystemInfo().getOperatingSystem().getProcess(Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]));
            heart.logDebug("Used Memory: " + "" + NumberFormat.getIntegerInstance().format(process.getResidentSetSize() / 1024 / 1024) + " MB" + "");
        }, 20 * 5, 20 * 15);
    }

    @Override
    public void onDisable() {
        heart.shutdown();
    }
}
