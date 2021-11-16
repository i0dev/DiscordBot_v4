package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class MessageManager extends AbstractManager {

    public MessageManager(Heart heart) {
        super(heart);
    }

    public MessageEmbed createMessageEmbed(EmbedMaker builder) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);

        if (builder.getContent() != null)
            embedBuilder.setDescription(heart.msgMgr().replacePlaceholders(builder.getContent(), builder));

        if (builder.getFooter() != null && builder.getFooterImg() == null)
            embedBuilder.setFooter(heart.msgMgr().replacePlaceholders(builder.getFooter(), builder));

        if (builder.getFooter() != null && builder.getFooterImg() != null)
            embedBuilder.setFooter(heart.msgMgr().replacePlaceholders(builder.getFooter(), builder), builder.getFooterImg());

        if (builder.getTitle() != null)
            embedBuilder.setTitle(heart.msgMgr().replacePlaceholders(builder.getTitle(), builder));

        if (builder.getImage() != null)
            embedBuilder.setImage(builder.getImage());

        if (builder.getThumbnail() != null)
            embedBuilder.setThumbnail(builder.getThumbnail());

        embedBuilder.setAuthor(heart.msgMgr().replacePlaceholders(builder.getAuthorName(), builder), builder.getAuthorURL(), builder.getAuthorImg());

        if (builder.getTimestamp() != null)
            embedBuilder.setTimestamp(builder.getTimestamp());

        if (builder.getFields() != null) {
            for (MessageEmbed.Field field : builder.getFields()) {
                MessageEmbed.Field newField = new MessageEmbed.Field(heart.msgMgr().replacePlaceholders(field.getName(), builder), heart.msgMgr().replacePlaceholders(field.getValue(), builder), field.isInline());
                embedBuilder.addField(newField);
            }
        }

        if (builder.getField() != null) {
            MessageEmbed.Field newField = new MessageEmbed.Field(heart.msgMgr().replacePlaceholders(builder.getField().getName(), builder), heart.msgMgr().replacePlaceholders(builder.getField().getValue(), builder), builder.getField().isInline());
            embedBuilder.addField(newField);

        }
        String colorHex = heart.normalColor();
        if (builder.getColorHexCode() != null) colorHex = builder.getColorHexCode();
        embedBuilder.setColor(Color.decode(colorHex));
        return embedBuilder.build();
    }

    public String replacePlaceholders(String message, User user, User author, Guild guild) {
        if (message == null) return null;
        if (user != null) {
            DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
            message = message
                    .replace("{tag}", user.getAsTag())
                    .replace("{mention}", user.getAsMention())
                    .replace("{id}", user.getId())
                    .replace("{muted}", discordUser.isMuted() ? "Yes" : "No")
                    .replace("{muteExpiry}", discordUser.getUnmuteAtTime() == 0 ? "`Not Muted`" : discordUser.getUnmuteAtTime() == -1 ? "`Never`" : "<t:" + discordUser.getUnmuteAtTime() / 1000 + ":R>")
                    .replace("{blacklisted}", discordUser.isBlacklisted() ? "Yes" : "No")
                    .replace("{boosts}", discordUser.getTotalBoostCount() + "")
                    .replace("{ticketsClosed}", discordUser.getTicketsClosed() + "")
                    .replace("{warnings}", discordUser.getWarnings() + "")
                    .replace("{ign}", discordUser.getMinecraftIGN().equals("") || discordUser.getMinecraftIGN().equalsIgnoreCase("0") ? "Not Linked" : discordUser.getMinecraftIGN())
                    .replace("{uuid}", discordUser.getMinecraftUUID())
                    .replace("{invites}", discordUser.getDiscordInvites() + "")
                    .replace("{isBot}", user.isBot() ? "Yes" : "No")
                    .replace("{timeCreated}", "<t:" + (user.getTimeCreated().toInstant().toEpochMilli() / 1000L) + ":R>")
                    .replace("{linkTime}", "<t:" + (discordUser.getLinkedTime() / 1000L) + ":R>")
                    .replace("{name}", user.getName());
            if (guild != null && guild.getMember(user) != null) {
                Member member = guild.getMember(user);
                message = message
                        .replace("{isBoosting}", guild.getBoosters().contains(member) ? "Yes" : "No");
            }
        }

        if (author != null) {
            message = message
                    .replace("{authorName}", author.getName())
                    .replace("{authorTag}", author.getAsTag())
                    .replace("{authorMention}", author.getAsMention())
                    .replace("{authorID}", author.getId())
                    .replace("{authorAvatarUrl}", author.getEffectiveAvatarUrl());
        }

        if (guild != null) {
            message = message
                    .replace("{guildName}", guild.getName())
                    .replace("{guildMemberCount}", guild.getMemberCount() + "")
                    .replace("{guildBoostTier}", guild.getBoostTier().getKey() + "")
                    .replace("{guildBannerUrl}", guild.getBannerUrl() == null ? "No Banner" : guild.getBannerUrl())
                    .replace("{guildOwnerTag}", guild.getOwner().getUser().getAsTag())
                    .replace("{guildOwnerMention}", guild.getOwner().getUser().getAsMention())
                    .replace("{guildOwnerID}", guild.getOwner().getUser().getId())
                    .replace("{guildOwnerAvatarUrl}", guild.getOwner().getUser().getEffectiveAvatarUrl())
                    .replace("{guildOwnerName}", guild.getOwner().getUser().getName());
        }

        JDA jda = heart.getJda();
        if (jda != null)
            message = message
                    .replace("{botTag}", jda.getSelfUser().getAsTag())
                    .replace("{botMention}", jda.getSelfUser().getAsMention())
                    .replace("{botAvatarUL}", jda.getSelfUser().getEffectiveAvatarUrl())
                    .replace("{botID}", jda.getSelfUser().getId())
                    .replace("{botName}", jda.getSelfUser().getName());

        message = message
                .replace("{botAuthor}", "i0#0001")
                .replace("{pluginMode}", heart.isPlugin() ? "Yes" : "No")
                .replace("{version}", Heart.VERSION);

        return message;
    }

    public String replacePlaceholders(String message, EmbedMaker embedMaker) {
        return replacePlaceholders(message, embedMaker.getUser(), embedMaker.getAuthor(), embedMaker.getGuild());
    }

    public String replacePlaceholders(String message) {
        return replacePlaceholders(message, null, null, null);
    }

    public String replacePlaceholders(String message, User user) {
        return replacePlaceholders(message, user, null, null);
    }

    public String replacePlaceholders(String message, User user, User author) {
        return replacePlaceholders(message, user, author, null);
    }


}
