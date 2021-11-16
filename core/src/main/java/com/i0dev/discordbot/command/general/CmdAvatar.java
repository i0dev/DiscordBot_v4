package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdAvatar extends DiscordCommand {

    public CmdAvatar(Heart heart, CommandData commandData) {
        super(heart, commandData);
    }

    @Override
    protected void setupCommand() {
        setCommand("avatar");
        setDescription("Get the avatar of a user.");
        addOption(new OptionData(OptionType.USER, "user", "Get the avatar of a user.", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        User user;
        if (e.getOptions().size() == 0) user = e.getUser();
        else user = e.getOption("user").getAsUser();
        data.reply(EmbedMaker.builder()
                .user(user)
                .title("{tag}'s Avatar")
                .image(user.getEffectiveAvatarUrl())
                .build());
    }

}
