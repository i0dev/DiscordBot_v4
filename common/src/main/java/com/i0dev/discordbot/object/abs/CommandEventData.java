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

package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Getter
public class CommandEventData {

    // Custom
    DiscordUser discordUser;
    Heart heart;
    SlashCommandInteractionEvent event;

    public CommandEventData(Heart heart, SlashCommandInteractionEvent event) {
        this.heart = heart;
        this.event = event;
        this.discordUser = heart.genMgr().getDiscordUser(event.getUser().getIdLong());
    }

    public void reply(EmbedMaker embedMaker) {
        if (event.isAcknowledged()) return;
        event.replyEmbeds(heart.msgMgr().createMessageEmbed(embedMaker)).queue();
    }

    public void replyFailure(String message) {
        if (event.isAcknowledged()) return;
        event.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder().content(message).colorHexCode(heart.failureColor()).build())).queue();
    }

    public void replySuccess(String message) {
        if (event.isAcknowledged()) return;
        event.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder().content(message).colorHexCode(heart.successColor()).build())).queue();
    }


    public Message replyComplete(EmbedMaker embedMaker) {
        if (event.isAcknowledged()) return null;
        return (Message) event.replyEmbeds(heart.msgMgr().createMessageEmbed(embedMaker)).complete().getInteraction();
    }


}
