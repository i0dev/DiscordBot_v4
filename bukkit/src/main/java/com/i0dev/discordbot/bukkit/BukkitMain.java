/*
 * MIT License
 *
 * Copyright (c) i0dev
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.i0dev.discordbot.bukkit;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.bukkit.command.CmdLink;
import com.i0dev.discordbot.bukkit.object.AbstractCommand;
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

        registerCmd("Link", new CmdLink(heart, "Link"));
    }

    public void registerCmd(String cmd, AbstractCommand abstractCommand) {
        getCommand(cmd).setExecutor(abstractCommand);
        getCommand(cmd).setTabCompleter(abstractCommand);
    }


    @Override
    public void onDisable() {
        if (heart != null)
            heart.shutdown();
    }
}
