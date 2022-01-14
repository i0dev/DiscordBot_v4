package com.i0dev.discordbot.object.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.time.temporal.TemporalAccessor;

@Builder
@Getter
@Setter
public class EmbedMaker {

    String title, content, footer, footerImg, image, thumbnail, authorName, authorURL, authorImg;

    User author, user;

    Guild guild;

    MessageEmbed.Field[] fields;

    TemporalAccessor timestamp;

    MessageEmbed.Field field;

    String colorHexCode;

}


