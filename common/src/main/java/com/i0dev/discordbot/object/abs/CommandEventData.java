package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Getter
public class CommandEventData {

    // Custom
    DiscordUser discordUser;
    Heart heart;
    SlashCommandEvent event;

    public CommandEventData(Heart heart, SlashCommandEvent event) {
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
