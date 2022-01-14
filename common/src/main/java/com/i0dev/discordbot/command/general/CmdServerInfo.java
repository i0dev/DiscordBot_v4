package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.ArrayList;
import java.util.List;

public class CmdServerInfo extends DiscordCommand {

    public CmdServerInfo(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("server_info");
        addRequirement(Requirement.IN_GUILD);
        setDescription("Sends information about the server.");
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        Guild guild = e.getGuild();

        StringBuilder general = new StringBuilder();
        general.append("Server Owner: ").append(guild.getOwner().getAsMention()).append("\n");
        general.append("Member Count: `").append(guild.getMemberCount()).append("`\n");
        general.append("Categories: `").append(guild.getCategories().size()).append("`\n");
        general.append("Text Channels: `").append(guild.getTextChannels().size()).append("`\n");
        general.append("Voice Channels: `").append(guild.getVoiceChannels().size()).append("`\n");
        general.append("Roles: `").append(guild.getRoles().size()).append("`\n");
        general.append("Creation Date: <t:").append((guild.getTimeCreated().toInstant().toEpochMilli() / 1000L)).append(":R>\n");
        List<Member> members = e.getGuild().getMembers();
        StringBuilder memberInfo = new StringBuilder();
        memberInfo.append("Members: ").append("`").append(members.size()).append("`").append("\n");
        memberInfo.append("Humans: ").append("`").append(members.stream().filter(member -> !member.getUser().isBot()).count()).append("`").append("\n");
        memberInfo.append("Bots: ").append("`").append(members.stream().filter(member -> member.getUser().isBot()).count()).append("`").append("\n");
        memberInfo.append("\n");
        memberInfo.append("Online: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count()).append("`").append("\n");
        memberInfo.append("DND: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count()).append("`").append("\n");
        memberInfo.append("Idle: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count()).append("`").append("\n");
        memberInfo.append("Offline: ").append("`").append(members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count()).append("`").append("\n");

        List<String> emotes = new ArrayList<>();
        guild.getEmotes().forEach(emote -> emotes.add(emote.getAsMention()));
        int length = 0;
        List<String> toRemove = new ArrayList<>();
        for (String emote : emotes) {
            length += emote.length();
            if (length > 950)
                toRemove.add(emote);
        }
        toRemove.forEach(emotes::remove);
        data.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__General Info__", general.toString(), true),
                        new MessageEmbed.Field("__Member Info__", memberInfo.toString(), true),
                        new MessageEmbed.Field("__Server Emotes (Showing {x} out of {max} emojis)__".replace("{max}", guild.getEmotes().size() + "").replace("{x}", (guild.getEmotes().size() - toRemove.size()) + ""), heart.genMgr().formatStringList(emotes, ", ", true), false)
                })
                .authorName(guild.getName() + "'s Server Information")
                .authorImg(guild.getIconUrl())
                .build());
    }

}
