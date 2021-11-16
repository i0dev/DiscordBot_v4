package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;

public class CmdMembers extends DiscordCommand {
    public CmdMembers(Heart heart, CommandData configSection) {
        super(heart, configSection);
    }

    @Override
    protected void setupCommand() {
        setCommand("members");
        setDescription("Sends guild member information.");
        addRequirement(Requirement.IN_GUILD);
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        List<Member> members = e.getGuild().getMembers();
        String info = "Members: " + "`" + members.size() + "`" + "\n" +
                "Humans: " + "`" + members.stream().filter(member -> !member.getUser().isBot()).count() + "`" + "\n" +
                "Bots: " + "`" + members.stream().filter(member -> member.getUser().isBot()).count() + "`" + "\n";
        String status = "Online: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.ONLINE)).count() + "`" + "\n" +
                "DND: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)).count() + "`" + "\n" +
                "Idle: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.IDLE)).count() + "`" + "\n" +
                "Offline: " + "`" + members.stream().filter(member -> member.getOnlineStatus().equals(OnlineStatus.OFFLINE)).count() + "`" + "\n";
        data.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__Member Information__", info, true),
                        new MessageEmbed.Field("__Member Status__", status, true)
                })
                .build());
    }

}
