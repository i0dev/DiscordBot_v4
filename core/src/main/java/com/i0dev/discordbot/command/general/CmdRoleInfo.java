package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

public class CmdRoleInfo extends DiscordCommand {

    public CmdRoleInfo(Heart heart, CommandData commandData) {
        super(heart, commandData);
    }

    @Override
    protected void setupCommand() {
        setCommand("role_info");
        setDescription("Get information about a selected role.");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.ROLE, "role", "The target role", true));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        Role role = e.getOption("role").getAsRole();
        Color color = role.getColor();
        String format = "#99aab5";
        if (color != null)
            format = String.format("#%02x%02x%02x", color.getRed(), color.getBlue(), color.getGreen());


        StringBuilder msg = new StringBuilder();
        msg.append("Name: `").append(role.getName()).append("`\n");
        msg.append("Role ID: `").append(role.getId()).append("`\n");
        msg.append("Color: `").append(format).append("`\n");
        msg.append("Mention: ").append(role.getAsMention()).append("\n");
        msg.append("Position: `").append(role.getPosition()).append("`\n");
        msg.append("Mentionable: `").append(role.isMentionable() ? "Yes" : "No").append("`\n");
        msg.append("Hoisted: `").append(role.isHoisted() ? "Yes" : "No").append("`\n");

        data.reply(EmbedMaker.builder().colorHexCode(format).field(new MessageEmbed.Field("__Role Information__ ", msg.toString(), true)).build());
    }

}
