package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.util.Utility;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CmdRoles extends DiscordCommand {

    public CmdRoles(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("roles");
        addRequirement(Requirement.IN_GUILD);
        setDescription("Get a list of guild roles.");
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        data.reply(EmbedMaker.builder()
                .field(new MessageEmbed.Field("__List of guild roles__", Utility.formatRolesList(e.getGuild().getRoles()), true))
                .build());
    }

}
