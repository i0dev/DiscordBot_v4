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
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdChangelog extends DiscordCommand {
    public CmdChangelog(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("changelog");
        setDescription("Post a changelog update.");
        addOption(new OptionData(OptionType.STRING, "message", "The changelog post to post.", true));
        addOption(new OptionData(OptionType.CHANNEL, "channel", "A channel override if you don't want to use the default.", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        String message = e.getOption("message").getAsString();
        GuildMessageChannel channel;
        channel = e.getOption("channel") == null ? heart.getJda().getTextChannelById(heart.cnf().getChangelogChannel()) : (GuildMessageChannel) e.getOption("channel").getAsGuildChannel();

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .user(e.getUser())
                .authorName("Changelog post from {tag}")
                .authorImg(e.getUser().getEffectiveAvatarUrl())
                .content(message)
                .colorHexCode(heart.normalColor())
                .build())).queue();

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content("You have successfully sent a changelog post.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
