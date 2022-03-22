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
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.util.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdProfile extends DiscordCommand {
    public CmdProfile(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("profile");
        setDescription("Sends the users profile.");
        addOption(new OptionData(OptionType.USER, "user", "get the profile of target user", false));
        addRequirement(Requirement.IN_GUILD);
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        User user;
        if (e.getOptions().size() == 0) user = e.getUser();
        else user = e.getOption("user").getAsUser();

        DiscordUser discordUser = heart.genMgr().getDiscordUser(user);
        Member member = e.getGuild().getMember(user);

        StringBuilder bot = new StringBuilder();
        bot.append("Tickets Closed: ").append("`{ticketsClosed}`").append("\n");
        bot.append("Discord Invites: ").append("`{invites}`").append("\n");
        bot.append("Warnings: ").append("`{warnings}`").append("\n");
        bot.append("Blacklisted: ").append("`{blacklisted}`").append("\n");
        bot.append("Timed Out: ").append("`{timedOut}`").append("\n");

        User invitedBy = null;
        if (discordUser.getInvitedByID() != 0)
            invitedBy = heart.genMgr().retrieveUser(user.getIdLong());
        bot.append("Invited By: `").append(invitedBy == null ? "Unknown" : invitedBy.getAsTag()).append("`\n");
        bot.append("Linked IGN: ").append("`{ign}`").append("\n");
        bot.append("Total Boosts: ").append("`{boosts}`").append("\n");

        StringBuilder general = new StringBuilder();
        general.append("DiscordID: ").append("`{id}`").append("\n");
        general.append("Account Creation: ").append("{timeCreated}").append("\n");
        general.append("Server Joined: ").append("<t:").append(member.getTimeJoined().toInstant().toEpochMilli() / 1000L).append(":R>").append("\n");
        general.append("Mention: ").append("{mention}").append("\n");
        general.append("Tag: ").append("`{tag}`").append("\n");
        general.append("Is Bot: ").append("`{isBot}`").append("\n");
        general.append("Online Status: ").append("`").append(member.getOnlineStatus().getKey()).append("`").append("\n");
        general.append("Currently Boosting: ").append("`{isBoosting}`").append("\n");

        data.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__General Info__", general.toString(), true),
                        new MessageEmbed.Field("__Activity Info__", bot.toString(), true),
                        new MessageEmbed.Field("__Server Roles__", e.getGuild().getMember(user) == null ? "`Not in discord.`" : Utility.formatRolesList(member.getRoles()), false)
                })
                .authorName("{tag}'s User Profile")
                .authorImg(user.getEffectiveAvatarUrl())
                .user(user)
                .guild(e.getGuild())
                .thumbnail(discordUser.getMinecraftSkinTexture())
                .build());
    }

}
