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
import com.i0dev.discordbot.config.configs.PermissionConfig;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.PermissionNode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CmdCommandInfo extends DiscordCommand {

    public CmdCommandInfo(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("command_info");
        setDescription("Get information about a command.");
        addOption(new OptionData(OptionType.STRING, "command_id", "The ID of the command you want information about.", false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        String commandID = e.getOption("command_id").getAsString();
        data.replyFailure("Command is temporarily unavailable.");
         /*
        List<String> allCommandIds = heart.getAllCommandIDS();
        List<PermissionNode> nodes = heart.getConfig(PermissionConfig.class).getPermissions();

        AtomicBoolean breakLoop = new AtomicBoolean(false);

      for (DiscordCommand cmd : heart.getCommands()) {
            if (breakLoop.get()) break;
            for (String cmdID : allCommandIds) {
                if (breakLoop.get()) break;
                if (commandID.startsWith(cmdID)) {
                    if (cmdID.length() == cmd.getCommand().length()) {
                        PermissionNode node = nodes.stream().filter(permissionNode -> permissionNode.getCommandID().equalsIgnoreCase(cmdID)).findFirst().orElse(null);

                        StringBuilder subCommands = new StringBuilder();
                        cmd.getSubCommands().forEach(subCommand -> {
                            subCommands.append("• `/").append(cmd.getCommand()).append(" ").append(subCommand.getName()).append("` - ").append(subCommand.getDescription()).append("\n");
                        });

                        StringBuilder groupsWithPermissions = new StringBuilder("");
                        if (node == null)
                            groupsWithPermissions.append("No permissions set.  ");
                        else
                            node.getGroups().forEach(group -> groupsWithPermissions.append(group).append(", "));

                        if (groupsWithPermissions.length() == 0) groupsWithPermissions.append("No permissions set.  ");

                        StringBuilder rolesWithPermissions = new StringBuilder("");
                        if (node == null)
                            rolesWithPermissions.append("No permissions set.  ");
                        else
                            node.getRolesAllowed().stream().map(roleID -> e.getGuild().getRoleById(roleID)).filter(Objects::nonNull).forEach(role -> rolesWithPermissions.append(role.getAsMention()).append(", "));

                        if (rolesWithPermissions.length() == 0) rolesWithPermissions.append("No permissions set.  ");

                        StringBuilder usersWithPermissions = new StringBuilder();
                        if (node == null)
                            usersWithPermissions.append("No permissions set.  ");
                        else
                            node.getUsersAllowed().stream().map(userId -> heart.getJda().getUserById(userId)).filter(Objects::nonNull).forEach(user -> rolesWithPermissions.append("`").append(user.getAsTag()).append("`, "));

                        if (usersWithPermissions.length() == 0) usersWithPermissions.append("No permissions set.  ");


                        StringBuilder info = new StringBuilder();
                        info.append("Command description: `").append(cmd.getDescription()).append("`\n");
                        info.append("Groups with permissions: `").append(groupsWithPermissions.substring(0, groupsWithPermissions.toString().length() - 2)).append("`\n");
                        info.append("Roles with permissions: `").append(rolesWithPermissions.substring(0, rolesWithPermissions.toString().length() - 2)).append("`\n");
                        info.append("Users with permissions: `").append(usersWithPermissions.substring(0, usersWithPermissions.toString().length() - 2)).append("`\n");


                        MessageEmbed.Field subCommandField = cmd.getSubCommands().isEmpty() ? null : new MessageEmbed.Field("Sub Commands", subCommands.toString(), false);
                        e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                                .authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl())
                                .authorName("Command Info for " + commandID)
                                .fields(
                                        new MessageEmbed.Field[]{
                                                subCommandField,
                                                new MessageEmbed.Field("Information", info.toString(), false),
                                        }
                                )

                                .build())).queue();
                        breakLoop.set(true);
                        break;
                    } else {
                        String newID = cmdID.replace(cmd.getCommand(), "");

                        for (SubcommandData subCommand : cmd.getSubCommands()) {
                            if (subCommand.getName().equalsIgnoreCase(newID)) {

                                PermissionNode node = nodes.stream().filter(permissionNode -> permissionNode.getCommandID().equalsIgnoreCase(cmdID)).findFirst().orElse(null);

                                StringBuilder groupsWithPermissions = new StringBuilder("");
                                if (node == null)
                                    groupsWithPermissions.append("No permissions set.  ");
                                else
                                    node.getGroups().forEach(group -> groupsWithPermissions.append(group).append(", "));

                                if (groupsWithPermissions.length() == 0)
                                    groupsWithPermissions.append("No permissions set.  ");

                                StringBuilder rolesWithPermissions = new StringBuilder("");
                                if (node == null)
                                    rolesWithPermissions.append("No permissions set.  ");
                                else
                                    node.getRolesAllowed().stream().map(roleID -> e.getGuild().getRoleById(roleID)).filter(Objects::nonNull).forEach(role -> rolesWithPermissions.append(role.getAsMention()).append(", "));
                                if (rolesWithPermissions.length() == 0)
                                    rolesWithPermissions.append("No permissions set.  ");

                                StringBuilder usersWithPermissions = new StringBuilder();
                                if (node == null)
                                    usersWithPermissions.append("No permissions set.  ");
                                else
                                    node.getUsersAllowed().stream().map(userId -> heart.getJda().getUserById(userId)).filter(Objects::nonNull).forEach(user -> rolesWithPermissions.append("`").append(user.getAsTag()).append("`, "));
                                if (usersWithPermissions.length() == 0)
                                    usersWithPermissions.append("No permissions set.  ");


                                StringBuilder info = new StringBuilder();
                                info.append("Parent command: `/").append(cmd.getCommand()).append("`\n");
                                info.append("Command description: `").append(subCommand.getDescription()).append("`\n");
                                info.append("Groups with permissions: `").append(groupsWithPermissions.substring(0, groupsWithPermissions.toString().length() - 2)).append("`\n");
                                info.append("Roles with permissions: `").append(rolesWithPermissions.substring(0, rolesWithPermissions.toString().length() - 2)).append("`\n");
                                info.append("Users with permissions: `").append(usersWithPermissions.substring(0, usersWithPermissions.toString().length() - 2)).append("`\n");


                                e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                                        .authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl())
                                        .authorName("Sub-Command Info for " + commandID)
                                        .fields(
                                                new MessageEmbed.Field[]{
                                                        new MessageEmbed.Field("Information", info.toString(), true),
                                                }
                                        )

                                        .build())).queue();
                                breakLoop.set(true);
                                break;


                            }
                        }
                    }


                    break;
                }
            }
        }

     */
    }

}
