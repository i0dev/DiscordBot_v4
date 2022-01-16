package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GeneralManager extends AbstractManager {

    public GeneralManager(Heart heart) {
        super(heart);
    }

    public User retrieveUser(long id) {
        try {
            return heart.getJda().retrieveUserById(id).complete();
        } catch (Exception ignored) {
            return null;
        }
    }

    public DiscordUser getDiscordUser(ISnowflake iSnowflake) {
        return getDiscordUser(iSnowflake.getIdLong());
    }

    public DiscordUser getDiscordUser(long id) {
        if (id == 0) return null;
        DiscordUser user = (DiscordUser) heart.sqlMgr().getObject("id", id, DiscordUser.class);
        if (user == null) {
            user = new DiscordUser(id, heart);
            user.save();
        }
        return user;
    }

    public boolean isAllowedGuild(Guild guild) {
        return heart.getAllowedGuilds().contains(guild);
    }


    /*
    Welcome message handler
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (!heart.cnf().isWelcomeMessagesEnabled()) return;
        TextChannel channel = heart.getJda().getTextChannelById(heart.cnf().getWelcomeMessageChannel());
        if (channel == null) return;

        heart.cnf().getWelcomeRolesToGive().stream().map(aLong -> heart.getJda().getRoleById(aLong)).collect(Collectors.toList()).stream().filter(Objects::nonNull).forEach(role -> {
            DiscordUser user = heart.genMgr().getDiscordUser(e.getUser().getIdLong());
            user.addRole(role);
        });

        if (heart.cnf().isWelcomePingUser()) channel.sendMessage(e.getMember().getAsMention()).queue();

        String image = heart.cnf().getWelcomeEmbedImageUrl() == null ? null : heart.cnf().getWelcomeEmbedImageUrl();
        String thumbnail = heart.cnf().isWelcomeUserMemberAvatarAsThumbnail() ? e.getUser().getEffectiveAvatarUrl() : heart.getJda().getSelfUser().getEffectiveAvatarUrl();

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .title(heart.cnf().getWelcomeEmbedTitle())
                .author(e.getUser())
                .user(e.getUser())
                .thumbnail(thumbnail)
                .image(image)
                .content(heart.cnf().getWelcomeEmbedContent())
                .build())).queue();

        /*
        Join Logs
         */
        heart.getExecutorService().schedule(() -> {
            if (heart.cnf().isJoinLogsEnabled()) {
                TextChannel joinLogsChannel = heart.getJda().getTextChannelById(heart.cnf().getJoinLeaveLogsChannel());
                if (joinLogsChannel == null) return;
                joinLogsChannel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                        .authorImg(e.getUser().getEffectiveAvatarUrl())
                        .user(e.getUser())
                        .colorHexCode(heart.successColor())
                        .title("Member Join Log")
                        .content(heart.cnf().getJoinLogsFormat())
                        .build())).queue();
            }
        }, 3, TimeUnit.SECONDS);
    }

    /*
    Leave logs
     */

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        if (heart.cnf().isLeaveLogsEnabled()) {
            TextChannel leaveLogsChannel = heart.getJda().getTextChannelById(heart.cnf().getJoinLeaveLogsChannel());
            if (leaveLogsChannel == null) return;
            leaveLogsChannel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                    .authorImg(e.getUser().getEffectiveAvatarUrl())
                    .user(e.getUser())
                    .title("Member Leave Log")
                    .colorHexCode(heart.failureColor())
                    .content(heart.cnf().getLeaveLogsFormat())
                    .build())).queue();
        }
    }
}
