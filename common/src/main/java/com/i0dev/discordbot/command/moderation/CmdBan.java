package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdBan extends DiscordCommand {
    public CmdBan(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("ban");
        setDescription("Ban a user from the discord.");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.USER, "user", "The user to ban.", true));
        addOption(new OptionData(OptionType.INTEGER, "days", "How many days past to delete thee users messages.", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        int delDays = e.getOption("days") == null ? 0 : Integer.parseInt(e.getOption("days").getAsLong() + "");
        e.getGuild().ban(user, delDays).queue();
        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have successfully banned {tag} from the server.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
