package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import com.i0dev.discordbot.object.config.MultiCommandData;
import com.i0dev.discordbot.object.config.NamedCommandData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class DiscordCommandManager extends AbstractManager {

    public DiscordCommandManager(Heart heart) {
        super(heart);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent e) {
        for (DiscordCommand command : heart.getCommands()) {
            try {
                if (command.getCommand() == null) continue;
                if (!command.getCommand().equalsIgnoreCase(e.getName())) continue;
                CommandEventData data = new CommandEventData(heart, e);
                DiscordUser user = heart.genMgr().getDiscordUser(e.getUser().getIdLong());
                if (!hasPermission(command.getConfigSection(), e)) {
                    data.reply(EmbedMaker.builder()
                            .content("You don't have permission to use this command!")
                            .colorHexCode(heart.failureColor())
                            .build());
                    return;
                }
                if (isBlacklisted(data.getDiscordUser())) {
                    data.reply(EmbedMaker.builder()
                            .content("You are blacklisted from using this bot!")
                            .colorHexCode(heart.failureColor())
                            .build());
                    return;
                }
                if (!isValidGuild(e.getGuild())) {
                    data.reply(EmbedMaker.builder()
                            .content("This bot is not allowed to be used in this server.")
                            .colorHexCode(heart.failureColor())
                            .build());
                    return;
                }
                // Requirements
                if (null == e.getGuild() && command.getRequirements().contains(Requirement.IN_GUILD)) {
                    data.reply(EmbedMaker.builder()
                            .content("This command can only be used in a server.")
                            .colorHexCode(heart.failureColor())
                            .build());
                    return;
                }
                if (!user.isLinked() && command.getRequirements().contains(Requirement.LINKED)) {
                    data.reply(EmbedMaker.builder()
                            .content("You need to link your account in-game before you can use this command.")
                            .colorHexCode(heart.failureColor())
                            .build());
                    return;
                }
                // End Requirements
                if (command.getConfigSection() instanceof MultiCommandData) {
                    MultiCommandData multiCommandData = (MultiCommandData) command.getConfigSection();
                    String subcommandName = e.getSubcommandName();
                    NamedCommandData subcommand = multiCommandData.getCommandDataBySubCommandName(subcommandName);
                    if (!hasPermission(subcommand, e)) {
                        data.reply(EmbedMaker.builder()
                                .content("You don't have permission to use this command!")
                                .colorHexCode(heart.failureColor())
                                .build());
                        return;
                    }
                }
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
        e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .content("This command does not exist!")
                .colorHexCode(heart.failureColor())
                .build())).queue();
    }


    private boolean isValidGuild(Guild guild) {
        return heart.getAllowedGuilds().contains(guild);
    }

    public boolean isBlacklisted(DiscordUser user) {
        return user.isBlacklisted();
    }

    public boolean hasPermission(CommandData configSection, SlashCommandEvent e) {
        if (e.getGuild() != null && e.getMember() != null && e.getMember().hasPermission(Permission.ADMINISTRATOR) && heart.gCnf().isAdministratorBypassPermissions())
            return true;
        AtomicBoolean allowed = new AtomicBoolean(false);
        if (configSection.isRequirePermission()) {
            if (e.getMember() == null) return false;
            configSection.getAllowedOverride().forEach(id -> {
                Role role = heart.getJda().getRoleById(id);
                if (role != null && e.getMember().getRoles().contains(role)) allowed.set(true);
                else if (e.getUser().getIdLong() == id) allowed.set(true);
            });
        } else allowed.set(true);
        configSection.getBlockedOverride().forEach(id -> {
            Role role = heart.getJda().getRoleById(id);
            if (role != null && e.getMember() != null && e.getMember().getRoles().contains(role))
                allowed.set(false);
            else if (e.getUser().getIdLong() == id) allowed.set(false);
        });
        return allowed.get();
    }

}
