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

package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.util.Utility;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CmdInvite extends DiscordCommand {

    public CmdInvite(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("invite");
        setDescription("The invite module.");
        addSubcommand(new SubcommandData("invites", "Check a users invites")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to check invites of.", false)));
        addSubcommand(new SubcommandData("leaderboard", "Check the current invite leaderboard."));
        addSubcommand(new SubcommandData("add", "Add invites to a user.")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to add invites to.", true),
                        new OptionData(OptionType.INTEGER, "amount", "The amount of invites to add.", true)
                                .setRequiredRange(0, 100000000)
                )
        );
        addSubcommand(new SubcommandData("remove", "Remove invites from a user.")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to remove invites from.", true),
                        new OptionData(OptionType.INTEGER, "amount", "The amount of invites to remove.", true)
                                .setRequiredRange(0, 100000000)
                )
        );
        addSubcommand(new SubcommandData("clear", "Clear all invite data."));

    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("invites".equals(e.getSubcommandName())) invites(e, data);
        if ("leaderboard".equals(e.getSubcommandName())) leaderboard(e, data);
        if ("add".equals(e.getSubcommandName())) add(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("clear".equals(e.getSubcommandName())) clear(e, data);
    }

    public void invites(SlashCommandEvent e, CommandEventData data) {
        User user = e.getUser();
        if (e.getOption("user") != null) user = e.getOption("user").getAsUser();

        data.reply(EmbedMaker.builder()
                .user(user)
                .content("{tag}'s discord Invites: `{invites}`")
                .colorHexCode(heart.successColor())
                .build());
    }

    @SneakyThrows
    public void leaderboard(SlashCommandEvent e, CommandEventData data) {
        List<String> list = new ArrayList<>();
        ResultSet result = heart.sqlMgr().runQueryWithResult("select * from DiscordUser order by discordInvites desc limit " + heart.cnf().getInviteLeaderboardMaxDisplay());
        int place = 1;
        while (result.next()) {
            long id = result.getLong("id");
            long invites = result.getLong("discordInvites");
            if (invites == 0) continue;
            User user = heart.getJda().getUserById(id);
            if (user == null) continue;

            list.add("**#" + place + ".** " + user.getAsTag() + ": `" + invites + " invites`");
            place++;
        }

        if (list.size() == 0) {
            data.reply(EmbedMaker.builder()
                    .user(e.getUser())
                    .content("There is currently not any invite data.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }
        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .title("Invited Users Leaderboard")
                .content(Utility.formatStringList(list, "\n", false))
                .colorHexCode(heart.successColor())
                .build());

    }

    public void add(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        long amount = e.getOption("amount").getAsLong();
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        discordUser.setDiscordInvites(discordUser.getDiscordInvites() + amount);
        discordUser.save();

        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have added `{amt}` invites to {tag}, they now have a total of: {invites} invites.".replace("{amt}", amount + ""))
                .colorHexCode(heart.successColor())
                .build());
    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        long amount = e.getOption("amount").getAsLong();
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        discordUser.setDiscordInvites(discordUser.getDiscordInvites() - amount);
        discordUser.save();

        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have removed `{amt}` invites from {tag}, they now have a total of: {invites} invites.".replace("{amt}", amount + ""))
                .colorHexCode(heart.successColor())
                .build());
    }


    public void clear(SlashCommandEvent e, CommandEventData data) {
        heart.sqlMgr().runQuery("update DiscordUser set discordInvites = 0;");

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have cleared invite statistics.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
