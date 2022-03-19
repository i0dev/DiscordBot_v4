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

import com.google.gson.Gson;
import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.configs.MovementConfig;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.MovementObject;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class CmdMovement extends DiscordCommand {

    public CmdMovement(Heart heart) {
        super(heart);
    }

    GuildMessageChannel channel;
    MovementConfig cnf;

    @Override
    public void initialize() {
        cnf = heart.getConfig(MovementConfig.class);
        channel = (GuildMessageChannel) heart.getJda().getGuildChannelById(cnf.getMovementsChannelId());
    }

    @Override
    public void deinitialize() {
        cnf = null;
        channel = null;
    }

    @Override
    protected void setupCommand() {
        setCommand("movement");
        setDescription("The movement module.");
        addSubcommand(new SubcommandData("promote", "Promotes a user.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to promote.", true))
                .addOptions(new OptionData(OptionType.ROLE, "role", "The role to promote them to.", true))
        );
        addSubcommand(new SubcommandData("demote", "Demotes a user to a specified role.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to demote.", true))
                .addOptions(new OptionData(OptionType.ROLE, "role", "The role to demote them to.", true))
        );
        addSubcommand(new SubcommandData("remove", "Removes a user from all staff roles.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to remove.", true))
        );
        addSubcommand(new SubcommandData("resign", "Resigns a user from all staff roles.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to resign.", true))
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        if ("promote".equals(e.getSubcommandName())) promote(e, data);
        if ("demote".equals(e.getSubcommandName())) demote(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("resign".equals(e.getSubcommandName())) resign(e, data);
    }

    public void promote(SlashCommandInteractionEvent e, CommandEventData data) {
        Member member = e.getOption("user").getAsMember();
        Role role = e.getOption("role").getAsRole();
        MovementObject pastObject = getParentMovementObjectFromMember(member);
        DiscordUser discordUser = heart.genMgr().getDiscordUser(member);

        discordUser.addRole(role);

        if (pastObject != null) {
            discordUser.removeRole(pastObject.getMainRole());
            pastObject.getExtraRoles().forEach(discordUser::removeRole);
        }

        MovementObject newObject = getMovementObjectFromMainRole(role);
        if (newObject != null) {
            discordUser.addRole(newObject.getMainRole());
            newObject.getExtraRoles().forEach(discordUser::addRole);
        }

        if (channel == null) {
            heart.logDebug("Movement channel is null. Did not send movement message, but still gave roles.");
            return;
        }

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .authorName(cnf.getPromoteTitle())
                .user(member.getUser())
                .author(e.getUser())
                .colorHexCode(heart.successColor())
                .authorImg(member.getUser().getEffectiveAvatarUrl())
                .content(cnf.getPromoteContent()
                        .replace("{role}", role.getAsMention())
                        .replace("{displayName}", newObject == null ? role.getAsMention() : newObject.getDisplayName())
                )
                .build())).queue();

        discordUser.refreshNickname();

        data.replySuccess("Promoted " + member.getUser().getAsTag() + " to " + role.getAsMention());
    }

    public void demote(SlashCommandInteractionEvent e, CommandEventData data) {
        Member member = e.getOption("user").getAsMember();
        Role role = e.getOption("role").getAsRole();
        MovementObject pastObject = getParentMovementObjectFromMember(member);
        DiscordUser discordUser = heart.genMgr().getDiscordUser(member);
        if (pastObject != null) {
            discordUser.removeRole(pastObject.getMainRole());
            pastObject.getExtraRoles().forEach(discordUser::removeRole);
        }

        cnf.getExtraRolesToRemoveOnDemotion().forEach(discordUser::removeRole);

        discordUser.addRole(role);

        MovementObject newObject = getMovementObjectFromMainRole(role);
        if (newObject != null) {
            discordUser.addRole(newObject.getMainRole());
            newObject.getExtraRoles().forEach(discordUser::addRole);
        }

        if (channel == null) {
            heart.logDebug("Movement channel is null. Did not send movement message, but still removed/added roles.");
            return;
        }

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .authorName(cnf.getDemoteTitle())
                .user(member.getUser())
                .author(e.getUser())
                .colorHexCode(heart.failureColor())
                .authorImg(member.getUser().getEffectiveAvatarUrl())
                .content(cnf.getDemoteContent()
                        .replace("{role}", role.getAsMention())
                        .replace("{displayName}", newObject == null ? role.getAsMention() : newObject.getDisplayName())
                )
                .build())).queue();

        discordUser.refreshNickname();

        data.replySuccess("Demoted " + member.getUser().getAsTag() + " to " + role.getAsMention());
    }

    public void remove(SlashCommandInteractionEvent e, CommandEventData data) {
        Member member = e.getOption("user").getAsMember();
        MovementObject pastObject = getParentMovementObjectFromMember(member);
        DiscordUser discordUser = heart.genMgr().getDiscordUser(member);

        if (pastObject != null) {
            discordUser.removeRole(pastObject.getMainRole());
            pastObject.getExtraRoles().forEach(discordUser::removeRole);
        }

        cnf.getExtraRolesToRemoveOnDemotion().forEach(discordUser::removeRole);


        if (channel == null) {
            heart.logDebug("Movement channel is null. Did not send movement message, but still removed roles.");
            return;
        }

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .authorName(cnf.getRemoveTitle())
                .user(member.getUser())
                .author(e.getUser()).colorHexCode(heart.failureColor())

                .authorImg(member.getUser().getEffectiveAvatarUrl())
                .content(cnf.getRemoveContent())
                .build())).queue();

        discordUser.refreshNickname();
        data.replySuccess("Removed " + member.getUser().getAsTag() + " from the staff team.");

    }

    public void resign(SlashCommandInteractionEvent e, CommandEventData data) {
        Member member = e.getOption("user").getAsMember();
        MovementObject pastObject = getParentMovementObjectFromMember(member);
        DiscordUser discordUser = heart.genMgr().getDiscordUser(member);

        if (pastObject != null) {
            discordUser.removeRole(pastObject.getMainRole());
            pastObject.getExtraRoles().forEach(discordUser::removeRole);
        }

        cnf.getExtraRolesToRemoveOnDemotion().forEach(discordUser::removeRole);


        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .authorName(cnf.getResignTitle())
                .user(member.getUser())
                .author(e.getUser())
                .colorHexCode(heart.failureColor())
                .authorImg(member.getUser().getEffectiveAvatarUrl())
                .content(cnf.getResignContent())
                .build())).queue();

        discordUser.refreshNickname();


        data.replySuccess("You resigned " + member.getUser().getAsTag() + " from the staff team.");

    }

    /*
    Movement utilities
     */


    public MovementObject getParentMovementObjectFromMember(Member member) {
        List<Long> roleIDS = member.getRoles().stream().map(Role::getIdLong).collect(Collectors.toList());
        return cnf.getMovementOptions().stream().filter(obj -> roleIDS.contains(obj.getMainRole())).findFirst().orElse(null);
    }

    public MovementObject getMovementObjectFromMainRole(Role mainRole) {
        return cnf.getMovementOptions().stream().filter(obj -> obj.getMainRole() == mainRole.getIdLong()).findFirst().orElse(null);
    }

}
