package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdKick extends DiscordCommand {
    public CmdKick(Heart heart, CommandData configSection) {
        super(heart, configSection);
    }

    @Override
    protected void setupCommand() {
        setCommand("kick");
        setDescription("kick a user from the discord.");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.USER, "user", "The user to kick.", true));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        e.getGuild().kick(user.getId()).queue();
        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have successfully kicked {tag} from the server.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
