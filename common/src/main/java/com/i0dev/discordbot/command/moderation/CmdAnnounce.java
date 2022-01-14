package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdAnnounce extends DiscordCommand {
    public CmdAnnounce(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("announce");
        setDescription("Announce a message to a channel");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.CHANNEL, "channel", "The channel to send the announcement in.", true));
        addOption(new OptionData(OptionType.STRING, "announcement", "The channel to send the announcement in.", true));
        addOption(new OptionData(OptionType.BOOLEAN, "embed", "Set to disabled to disable the embed. It is enabled as default.", false));
        addOption(new OptionData(OptionType.STRING, "color", "A custom special color hex code for the announcement embed.", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        GuildChannel channel = e.getOption("channel").getAsGuildChannel();
        String color = e.getOption("color") == null ? heart.normalColor() : e.getOption("color").getAsString();
        boolean embed = e.getOption("embed") == null || e.getOption("embed").getAsBoolean();
        String announcement = e.getOption("announcement").getAsString();
        if (embed) {
            ((TextChannel) channel).sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                    .user(e.getUser())
                    .content(announcement)
                    .colorHexCode(color)
                    .build())).queue();
        } else ((TextChannel) channel).sendMessage(announcement).queue();


        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content("You have successfully send an announcement in {channel}.".replace("{channel}", channel.getAsMention()))
                .colorHexCode(heart.successColor())
                .build());
    }
}
