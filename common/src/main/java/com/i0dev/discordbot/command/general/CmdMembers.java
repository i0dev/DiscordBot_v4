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

package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;

public class CmdMembers extends DiscordCommand {
    public CmdMembers(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("members");
        setDescription("Sends guild member information.");
        addRequirement(Requirement.IN_GUILD);
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        List<Member> members = e.getGuild().getMembers();
        String info = "Members: " + "`" + members.size() + "`" + "\n" +
                "Humans: " + "`" + members.stream().filter(member -> !member.getUser().isBot()).count() + "`" + "\n" +
                "Bots: " + "`" + members.stream().filter(member -> member.getUser().isBot()).count() + "`" + "\n";
        String status = "Online: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count() + "`" + "\n" +
                "DND: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count() + "`" + "\n" +
                "Idle: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count() + "`" + "\n" +
                "Offline: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count() + "`" + "\n";
        data.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__Member Information__", info, true),
                        new MessageEmbed.Field("__Member Status__", status, true)
                })
                .build());
    }

}
