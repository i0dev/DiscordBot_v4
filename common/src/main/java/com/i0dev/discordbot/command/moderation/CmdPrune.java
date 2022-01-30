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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdPrune extends DiscordCommand {
    public CmdPrune(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("prune");
        setDescription("Delete a certain amount of past messages.");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.INTEGER, "amount", "The amount of messages to prune.", true)
                .setRequiredRange(0, 99));
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        long amount = e.getOption("amount").getAsLong();
        e.getChannel().purgeMessages(e.getChannel().getHistory().retrievePast(((int) amount)).complete());
        e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .user(e.getUser())
                .content("You pruned {amt} messages in this channel".replace("{amt}", amount + ""))
                .colorHexCode(heart.successColor())
                .build())).setEphemeral(true).queue();

        heart.logDiscord(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .authorImg(e.getUser().getEffectiveAvatarUrl())
                .authorName("Moderation Log")
                .content("{tag} pruned {amt} messages in {channel}".replace("{channel}", e.getChannel().getAsMention()).replace("{amt}", amount + ""))
                .build()
        );
    }
}
