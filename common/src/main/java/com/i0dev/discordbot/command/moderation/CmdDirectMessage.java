package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdDirectMessage extends DiscordCommand {
    public CmdDirectMessage(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("direct_message");
        setDescription("Send a message to a user from the bot.");
        addOption(new OptionData(OptionType.USER, "user", "The user to direct message.", true));
        addOption(new OptionData(OptionType.STRING, "message", "The message to send to the target user.", true));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        String message = e.getOption("message").getAsString();

        user.openPrivateChannel().complete().sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .title("Incoming Direct Message:")
                .content(message)
                .colorHexCode(heart.normalColor())
                .build())).queue();

        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have successfully sent a direct message to {tag}.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
