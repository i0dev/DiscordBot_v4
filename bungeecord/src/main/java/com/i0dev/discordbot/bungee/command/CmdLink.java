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

package com.i0dev.discordbot.bungee.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.bungee.object.AbstractCommand;
import com.i0dev.discordbot.bungee.util.MsgUtil;
import com.i0dev.discordbot.manager.LinkManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class CmdLink extends AbstractCommand {

    public CmdLink(Heart heart, String command) {
        super(heart, command);
    }

    @SneakyThrows
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            MsgUtil.msg(sender, "&7Usage: &c/link generate");
            return;
        }
        if (!(sender instanceof ProxiedPlayer)) {
            MsgUtil.msg(sender, "&cYou must be a player to use this command.");
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("generate")) {
            LinkManager lm = heart.getManager(LinkManager.class);

            ResultSet resultSet = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE minecraftUUID = '" + ((ProxiedPlayer) sender).getUniqueId() + "'");

            if (resultSet.next()) {
                User user = heart.genMgr().retrieveUser(Long.parseLong(resultSet.getString("id")));
                MsgUtil.msg(sender, "&cYou are already linked to the Discord account: &7" + user.getAsTag() + "&c.");
                return;
            }

            if (lm.isOnLinkList(((ProxiedPlayer) sender).getUniqueId())) {
                MsgUtil.msg(sender, "&cYou have already generated a link code.");

                AtomicReference<String> pastcode = new AtomicReference<>("");
                AtomicBoolean end = new AtomicBoolean(false);
                lm.pendingLinkMap.forEach((s, uuid) -> {
                    if (end.get()) return;
                    if (uuid.equals(((ProxiedPlayer) sender).getUniqueId())) {
                        pastcode.set(s);
                        end.set(true);
                    }
                });
                MsgUtil.msg(sender, "&cYour code is: &7" + pastcode.get());
                return;
            }

            String code = lm.generateCode((int) heart.cnf().getLinkCodeLength());

            lm.addToLinkList(code, ((ProxiedPlayer) sender).getUniqueId());
            MsgUtil.msg(sender, "&cYou have generated a link code!");
            MsgUtil.msg(sender, "&cYour link code is: &7" + code);
            MsgUtil.msg(sender, "&cType the command &7/link code " + code + " &cin discord.");
            return;
        }
        MsgUtil.msg(sender, "&7Usage: &c/link generate");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return tabCompleteHelper(args[0], Collections.singletonList("generate"));
        return blank;
    }
}
