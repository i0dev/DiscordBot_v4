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

package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.NewsChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdAnnounce extends DiscordCommand {
    public CmdAnnounce(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("announce");
        setDescription("Announce a message to a channel");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.CHANNEL, "channel", "The channel to send the announcement in.", true));
        addOption(new OptionData(OptionType.STRING, "announcement", "The channel to send the announcement in.", true));
        addOption(new OptionData(OptionType.BOOLEAN, "embed", "Set to disabled to disable the embed. It is enabled as default.", false));
        addOption(new OptionData(OptionType.STRING, "color", "A custom special color hex code for the announcement embed.", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        GuildChannel channel = e.getOption("channel").getAsGuildChannel();
        String color = e.getOption("color") == null ? heart.normalColor() : e.getOption("color").getAsString();
        boolean embed = e.getOption("embed") == null || e.getOption("embed").getAsBoolean();
        String announcement = e.getOption("announcement").getAsString();
        if (embed) {
            ((GuildMessageChannel) channel).sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                    .user(e.getUser())
                    .content(announcement)
                    .colorHexCode(color)
                    .build())).queue();
        } else ((GuildMessageChannel) channel).sendMessage(announcement).queue();


        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content("You have successfully send an announcement in {channel}.".replace("{channel}", channel.getAsMention()))
                .colorHexCode(heart.successColor())
                .build());
    }
}
