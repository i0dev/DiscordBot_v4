package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdChangelog extends DiscordCommand {
    public CmdChangelog(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("changelog");
        setDescription("Post a changelog update.");
        addOption(new OptionData(OptionType.STRING, "message", "The changelog post to post.", true));
        addOption(new OptionData(OptionType.CHANNEL, "channel", "A channel override if you don't want to use the default.", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        String message = e.getOption("message").getAsString();
        TextChannel channel;
        channel = e.getOption("channel") == null ? heart.getJda().getTextChannelById(heart.gCnf().getChangelogChannel()) : (TextChannel) e.getOption("channel").getAsGuildChannel();

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .user(e.getUser())
                .authorName("Changelog post from {tag}")
                .authorImg(e.getUser().getEffectiveAvatarUrl())
                .content(message)
                .colorHexCode(heart.normalColor())
                .build())).queue();

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content("You have successfully sent a changelog post.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
