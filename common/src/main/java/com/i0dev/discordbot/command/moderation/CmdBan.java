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

        heart.logDiscord(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .authorImg(e.getUser().getEffectiveAvatarUrl())
                .authorName("Moderation Log")
                .content("{authorTag} has banned {tag} from the server.")
                .build()
        );
    }
}
