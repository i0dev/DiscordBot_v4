/*
 * MIT License
 *
 * Copyright (c) i0dev
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

public class CmdRoleInfo extends DiscordCommand {

    public CmdRoleInfo(Heart heart) {
        super(heart);
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
