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

package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.configs.PermissionConfig;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.PermissionGroup;
import com.i0dev.discordbot.object.config.PermissionNode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GeneralManager extends AbstractManager {

    public GeneralManager(Heart heart) {
        super(heart);
    }

    public User retrieveUser(long id) {
        try {
            return heart.getJda().retrieveUserById(id).complete();
        } catch (Exception ignored) {
            return null;
        }
    }

    public DiscordUser getDiscordUser(ISnowflake iSnowflake) {
        return getDiscordUser(iSnowflake.getIdLong());
    }

    public DiscordUser getDiscordUser(long id) {
        if (id == 0) return null;
        DiscordUser user = (DiscordUser) heart.sqlMgr().getObject("id", id, DiscordUser.class);
        if (user == null) {
            user = new DiscordUser(id, heart);
            user.save();
        }
        return user;
    }

    public boolean isAllowedGuild(Guild guild) {
        return heart.getAllowedGuilds().contains(guild);
    }

    public void verifyMember(Member member) {
        DiscordUser user = getDiscordUser(member.getUser());
        heart.cnf().getVerifyRolesToGive().forEach(user::addRole);
        heart.cnf().getVerifyRolesToRemove().forEach(user::removeRole);
        heart.logDiscord(EmbedMaker.builder()
                .user(member.getUser())
                .colorHexCode(heart.successColor())
                .content("{tag} has verified themselves!")
                .build());
    }

    /*
    Welcome message handler
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        heart.cnf().getWelcomeRolesToGive().stream().map(aLong -> heart.getJda().getRoleById(aLong)).collect(Collectors.toList()).stream().filter(Objects::nonNull).forEach(role -> {
            DiscordUser user = heart.genMgr().getDiscordUser(e.getUser().getIdLong());
            user.addRole(role);
        });

        if (heart.cnf().isWelcomeMessagesEnabled()) {
            TextChannel channel = heart.getJda().getTextChannelById(heart.cnf().getWelcomeMessageChannel());
            if (channel != null) {
                if (heart.cnf().isWelcomePingUser()) channel.sendMessage(e.getMember().getAsMention()).queue();
                String image = heart.cnf().getWelcomeEmbedImageUrl().equals("") ? null : heart.cnf().getWelcomeEmbedImageUrl();
                String thumbnail = heart.cnf().isWelcomeUserMemberAvatarAsThumbnail() ? e.getUser().getEffectiveAvatarUrl() : heart.getJda().getSelfUser().getEffectiveAvatarUrl();

                channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                        .title(heart.cnf().getWelcomeEmbedTitle())
                        .author(e.getUser())
                        .user(e.getUser())
                        .guild(e.getGuild())
                        .thumbnail(thumbnail)
                        .image(image)
                        .content(heart.cnf().getWelcomeEmbedContent())
                        .build())).queue();
            }
        }
        /*
        Join Logs
         */
        heart.getExecutorService().schedule(() -> {
            if (heart.cnf().isJoinLogsEnabled()) {
                TextChannel joinLogsChannel = heart.getJda().getTextChannelById(heart.cnf().getJoinLeaveLogsChannel());
                if (joinLogsChannel != null)
                    joinLogsChannel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                            .authorImg(e.getUser().getEffectiveAvatarUrl())
                            .user(e.getUser())
                            .colorHexCode(heart.successColor())
                            .authorName("Member Join Log")
                            .content(heart.cnf().getJoinLogsFormat())
                            .build())).queue();
            }
        }, 3, TimeUnit.SECONDS);
    }

    /*
    Leave logs
     */

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        if (heart.cnf().isLeaveLogsEnabled()) {
            TextChannel leaveLogsChannel = heart.getJda().getTextChannelById(heart.cnf().getJoinLeaveLogsChannel());
            if (leaveLogsChannel == null) return;
            leaveLogsChannel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                    .authorImg(e.getUser().getEffectiveAvatarUrl())
                    .user(e.getUser())
                    .authorName("Member Leave Log")
                    .colorHexCode(heart.failureColor())
                    .content(heart.cnf().getLeaveLogsFormat())
                    .build())).queue();
        }
    }

    /*
    Permissions
     */

    public boolean hasPermission(Member member, String cmdID, IReplyCallback e) {
        if (member == null) return false;
        if (heart.cnf().isAdministratorBypassPermissions() && member.hasPermission(Permission.ADMINISTRATOR))
            return true;
        List<PermissionGroup> groups = heart.getConfig(PermissionConfig.class).getPermissionGroups();
        List<PermissionNode> nodes = heart.getConfig(PermissionConfig.class).getPermissions();
        PermissionNode node = nodes.stream().filter(permissionNode -> permissionNode.getCommandID().equalsIgnoreCase(cmdID)).findFirst().orElse(null);

        // If the cmd id doesn't have a permission node set in config
        if (node == null) {
            MessageEmbed msg = heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                    .colorHexCode(heart.failureColor())
                    .content("This command has no permissions set in config so it is default to admin only!\n" +
                            "Reference commandID: `" + cmdID + "`")
                    .build());

            e.replyEmbeds(msg).queue();
            return member.hasPermission(Permission.ADMINISTRATOR);
        }

        // Check node permissions
        if (node.getUsersDenied().contains(member.getUser().getIdLong())) return false;
        if (node.getRolesDenied().stream().anyMatch(deniedRoleId -> member.getRoles().stream().map(Role::getIdLong).collect(Collectors.toList()).contains(deniedRoleId)))
            return false;

        if (node.getUsersAllowed().contains(member.getUser().getIdLong())) return true;
        if (node.getRolesAllowed().stream().anyMatch(allowedRoleId -> member.getRoles().stream().map(Role::getIdLong).collect(Collectors.toList()).contains(allowedRoleId)))
            return true;

        // Check group permissions
        Set<PermissionGroup> memberGroups = groups.stream().filter(permissionGroup -> permissionGroup.getUsers().contains(member.getUser().getIdLong())).collect(Collectors.toSet());
        memberGroups.addAll(getAllOfMembersGroups(member, memberGroups));
        memberGroups.addAll(groups.stream().filter(PermissionGroup::isEveryoneHasPermission).collect(Collectors.toList()));

        System.out.println("All of " + member.getUser().getAsTag() + "'s groups: " + memberGroups);

        Set<Long> allowedRoles = new HashSet<>();
        Set<Long> allowedUsers = new HashSet<>();

        memberGroups.forEach(g -> allowedRoles.addAll(g.getRoles()));
        memberGroups.forEach(g -> allowedUsers.addAll(g.getUsers()));

        AtomicBoolean hasPermission = new AtomicBoolean(false);
        for (PermissionGroup memberGroup : memberGroups) {
            if (!node.getGroups().stream().map(String::toLowerCase).collect(Collectors.toList()).contains(memberGroup.getName().toLowerCase()))
                continue;

            if (memberGroup.isEveryoneHasPermission()) hasPermission.set(true);
            if (allowedUsers.contains(member.getUser().getIdLong())) hasPermission.set(true);
            if (allowedRoles.stream().anyMatch(allowedRoleId -> member.getRoles().stream().map(Role::getIdLong).collect(Collectors.toList()).contains(allowedRoleId)))
                hasPermission.set(true);
        }

        return hasPermission.get();
    }

    public Set<PermissionGroup> getAllOfMembersGroups(Member member, Set<PermissionGroup> alreadyAdded) {
        List<Role> roles = member.getRoles();
        Set<PermissionGroup> configGroups = new HashSet<>(heart.getConfig(PermissionConfig.class).getPermissionGroups());
        Set<PermissionGroup> groups = new HashSet<>();
        groups.addAll(configGroups.stream().filter(pg -> pg.getUsers().contains(member.getUser().getIdLong())).collect(Collectors.toSet()));
        groups.addAll(configGroups.stream().filter(pg -> pg.getRoles().stream().map(aLong -> heart.getJda().getRoleById(aLong)).collect(Collectors.toList()).stream().anyMatch(roles::contains)).collect(Collectors.toList()));
        List<PermissionGroup> toAdd = new ArrayList<>();
        groups.forEach(pg -> toAdd.addAll(getGroupsInheritedGroups(pg, alreadyAdded)));
        groups.addAll(toAdd);
        return groups;
    }

    public Set<PermissionGroup> getGroupsInheritedGroups(PermissionGroup group, Set<PermissionGroup> alreadyAdded) {
        alreadyAdded.add(group);
        for (String inheritGroup : group.getInheritGroups()) {
            PermissionGroup inheritGroupObject = heart.getConfig(PermissionConfig.class).getPermissionGroups().stream().filter(pg -> pg.getName().equalsIgnoreCase(inheritGroup)).findFirst().orElse(null);
            if (inheritGroupObject == null) continue;
            getGroupsInheritedGroups(inheritGroupObject, alreadyAdded);
        }
        return alreadyAdded;
    }

    /*
    For updating users names
     */

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
        if (!heart.cnf().isForceNicknameForLinkedUsers()) return;
        DiscordUser user = heart.genMgr().getDiscordUser(e.getUser());
        if (user == null) return;
        user.refreshNickname();
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
        if (!heart.cnf().isForceNicknameForLinkedUsers()) return;
        DiscordUser user = heart.genMgr().getDiscordUser(e.getUser());
        if (user == null) return;
        user.refreshNickname();
    }
}
