package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.PermissionConfig;
import com.i0dev.discordbot.config.storage.TicketStorage;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.PermissionNode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DiscordCommandManager extends AbstractManager {

    public DiscordCommandManager(Heart heart) {
        super(heart);
    }

    @Override
    public void onSlashCommand(SlashCommandEvent e) {
        CommandEventData data = new CommandEventData(heart, e);
        DiscordUser user = heart.genMgr().getDiscordUser(e.getUser().getIdLong());
        for (DiscordCommand command : heart.getCommands()) {
            try {
                if (command.getCommand() == null) continue;
                if (!command.getCommand().equalsIgnoreCase(e.getName())) continue;
                if (!hasPermission(e, command)) {
                    if (!e.isAcknowledged())
                        data.replyFailure("You don't have permission to use this command!");
                    return;
                }
                if (isBlacklisted(data.getDiscordUser())) {
                    data.replyFailure("You are blacklisted from using this bot!");
                    return;
                }
                if (!isValidGuild(e.getGuild())) {
                    data.replyFailure("This bot is not allowed to be used in this server.");
                    return;
                }
                // Requirements
                if (null == e.getGuild() && command.getRequirements().contains(Requirement.IN_GUILD)) {
                    data.replyFailure("This command can only be used in a server.");
                    return;
                }
                if (!user.isLinked() && command.getRequirements().contains(Requirement.LINKED)) {
                    data.replyFailure("You need to link your account in-game before you can use this command.");
                    return;
                }
                if (command.getRequirements().contains(Requirement.IS_TICKET) && heart.getConfig(TicketStorage.class).getTicketByID(e.getChannel().getId()) == null) {
                    data.replyFailure("This command can only be used in a ticket channel.");
                    return;
                }
                // End Requirements
                command.execute(e, data);
                return;
            } catch (Exception exception) {
                e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                        .colorHexCode(heart.failureColor())
                        .content(exception.getMessage())
                        .title("An error occurred while executing this command.")
                        .build())).queue();
                exception.printStackTrace();
                return;
            }
        }
        data.replyFailure("This command does not exist!");
    }


    private boolean isValidGuild(Guild guild) {
        return heart.getAllowedGuilds().contains(guild);
    }

    public boolean isBlacklisted(DiscordUser user) {
        return user.isBlacklisted();
    }

    public boolean hasPermission(SlashCommandEvent e, DiscordCommand cmd) {
        if (e.getGuild() != null && e.getMember() != null && e.getMember().hasPermission(Permission.ADMINISTRATOR) && heart.gCnf().isAdministratorBypassPermissions())
            return true;
        AtomicBoolean allowed = new AtomicBoolean(false);
        PermissionNode node = heart.getConfig(PermissionConfig.class).getPermissions().stream().filter(permissionNode -> permissionNode.getCommandID().equalsIgnoreCase(cmd.getCommand())).findFirst().orElse(null);

        heart.logDebug(node + "");
        heart.logDebug(e.getCommandPath().replace("/", "_").toLowerCase());

        if (node == null) {
            if (!e.getMember().hasPermission(Permission.ADMINISTRATOR))
                e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                        .colorHexCode(heart.failureColor())
                        .content("This command has no permissions set in config so it is default to admin only!\n" +
                                "Reference commandID: `" + e.getCommandPath().replace("/", "_").toLowerCase() + "`")
                        .build())).queue();
            return e.getMember().hasPermission(Permission.ADMINISTRATOR);
        }

        List<Long> usersRoleIds = e.getMember().getRoles().stream().map(Role::getIdLong).collect(Collectors.toList());
        if (node.getUsersAllowed().contains(e.getUser().getIdLong())) allowed.set(true);
        if (anyMatch(node.getRolesAllowed(), usersRoleIds)) allowed.set(true);
        if (anyMatch(node.getRolesDenied(), usersRoleIds)) allowed.set(false);
        if (node.getUsersDenied().contains(e.getUser().getIdLong())) allowed.set(false);

        return allowed.get();
    }

    public boolean hasPermission(ButtonClickEvent e, String cmdID) {
        if (e.getGuild() != null && e.getMember() != null && e.getMember().hasPermission(Permission.ADMINISTRATOR) && heart.gCnf().isAdministratorBypassPermissions())
            return true;
        AtomicBoolean allowed = new AtomicBoolean(false);
        PermissionNode node = heart.getConfig(PermissionConfig.class).getPermissions().stream().filter(permissionNode -> permissionNode.getCommandID().equalsIgnoreCase(cmdID)).findFirst().orElse(null);

        if (node == null) {
            if (!e.getMember().hasPermission(Permission.ADMINISTRATOR))
                e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                        .colorHexCode(heart.failureColor())
                        .content("This command has no permissions set in config so it is default to admin only!\n" +
                                "Reference commandID: `" + cmdID .replace("/", "_").toLowerCase() + "`")
                        .build())).queue();
            return e.getMember().hasPermission(Permission.ADMINISTRATOR);
        }

        List<Long> usersRoleIds = e.getMember().getRoles().stream().map(Role::getIdLong).collect(Collectors.toList());
        if (node.getUsersAllowed().contains(e.getUser().getIdLong())) allowed.set(true);
        if (anyMatch(node.getRolesAllowed(), usersRoleIds)) allowed.set(true);
        if (anyMatch(node.getRolesDenied(), usersRoleIds)) allowed.set(false);
        if (node.getUsersDenied().contains(e.getUser().getIdLong())) allowed.set(false);

        return allowed.get();
    }

    private boolean anyMatch(List<Long> parent, List<Long> check) {
        return parent.stream().anyMatch(check::contains);
    }
}
