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

package com.i0dev.discordbot.command.gamemode;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CmdPrison extends DiscordCommand {

    public CmdPrison(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("prison");
        setDescription("The Prison game-mode Module.");
        addSubcommand(new SubcommandData("leader", "Give a user the prison team leader role.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to give the role to.", true)));
        addSubcommand(new SubcommandData("confirm", "Confirm a team as playing.")
                .addOptions(new OptionData(OptionType.USER, "leader", "The leader of the team.", true))
                .addOptions(new OptionData(OptionType.STRING, "team", "The name of the team.", true))
                .addOptions(new OptionData(OptionType.INTEGER, "size", "The roster size of the team (-1 for unlimited).", true))
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        if ("leader".equals(e.getSubcommandName())) leader(e, data);
        if ("confirm".equals(e.getSubcommandName())) confirm(e, data);
    }

    public void leader(SlashCommandInteractionEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();

        DiscordUser discordUser = getHeart().genMgr().getDiscordUser(user);

        discordUser.addRole(heart.cnf().getPrisonLeaderRole());

        data.replySuccess("Successfully gave the user the prisons team leader role.");

    }

    public void confirm(SlashCommandInteractionEvent e, CommandEventData data) {
        User leader = e.getOption("leader").getAsUser();
        String team = e.getOption("team").getAsString();
        long size = e.getOption("size").getAsLong();

        GuildMessageChannel channel = (GuildMessageChannel) heart.getJda().getGuildChannelById(heart.cnf().getPrisonsConfirmedChannel());
        if (channel == null) return;
        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .authorName(heart.cnf().getPrisonConfirmTitle())
                .user(leader)
                .authorImg(leader.getEffectiveAvatarUrl())
                .content(heart.cnf().getPrisonConfirmFormat()
                        .replace("{team}", team)
                        .replace("{size}", size == -1 ? "Unlimited" : String.valueOf(size))
                )
                .build())).queue();

        data.replySuccess("Successfully confirmed the team **{team}** as playing with a roster size of **{size}**."
                .replace("{team}", team)
                .replace("{size}", size == -1 ? "Unlimited" : String.valueOf(size))
        );

    }

}
