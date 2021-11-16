package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;

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
        event.replyEmbeds(heart.msgMgr().createMessageEmbed(embedMaker)).queue();
    }

    public Message replyComplete(EmbedMaker embedMaker) {
        return (Message) event.replyEmbeds(heart.msgMgr().createMessageEmbed(embedMaker)).complete().getInteraction();
    }


}
