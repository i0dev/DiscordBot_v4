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
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdBotInfo extends DiscordCommand {

    public CmdBotInfo(Heart heart) {
        super(heart);
    }

    private EmbedMaker message;

    @Override
    public void initialize() {
        String msg = "Bot Author: " + "`{botAuthor}`" + "\n" +
                "Bot Version: `" + "{version}" + "`\n" +
                "Plugin Mode: `" + "{pluginMode}" + "`\n";

        message = EmbedMaker.builder()
                .authorName("DiscordBot Information")
                .authorURL("https://i0dev.com/")
                .authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl())
                .footer("Bot created by i0dev.com")
                .footerImg("https://cdn.discordapp.com/attachments/687663938443542552/908087180306575432/2fd97023f121975bc18c967b3bf5418f.png")
                .content(msg)
                .build();
    }

    @Override
    protected void setupCommand() {
        setCommand("bot_info");
        setDescription("Get the bots information");
        setRegisterListener(true);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().startsWith("<@!" + heart.getJda().getSelfUser().getId() + ">"))
            e.getMessage().replyEmbeds(heart.msgMgr().createMessageEmbed(message)).mentionRepliedUser(false).queue();
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        data.reply(message);
    }

}
