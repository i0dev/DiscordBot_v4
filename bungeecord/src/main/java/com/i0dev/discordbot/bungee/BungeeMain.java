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

package com.i0dev.discordbot.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.bungee.command.CmdLink;
import com.i0dev.discordbot.object.StartupTag;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;


import java.util.Arrays;

public class BungeeMain extends Plugin {

    Heart heart;

    @SneakyThrows
    public void sendCommand(String command, String server) {
        if (server.equalsIgnoreCase("bungee")) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
        } else {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF("ALL");
            out.writeUTF(command);
            ProxyServer.getInstance().getServerInfo(server).sendData("Message", out.toByteArray());
        }
    }

    @Override
    public void onEnable() {
        heart = new Heart(Arrays.asList(
                StartupTag.BUNGEE,
                StartupTag.PLUGIN
        ), getLogger(), this);

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CmdLink(heart, "Link"));
    }


    @Override
    public void onDisable() {
        if (heart != null)
            heart.shutdown();
    }
}
