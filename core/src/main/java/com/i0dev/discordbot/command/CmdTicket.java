package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.SuggestionStorage;
import com.i0dev.discordbot.config.TicketStorage;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.command.Suggestion;
import com.i0dev.discordbot.object.command.Ticket;
import com.i0dev.discordbot.object.command.TicketOption;
import com.i0dev.discordbot.object.config.CommandData;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class CmdTicket extends DiscordCommand {

    public CmdTicket(Heart heart, CommandData configSection) {
        super(heart, configSection);
    }

    TextChannel adminLogs, ticketLogs, ticketCreate;

    List<String> rolesToSeeTickets;
    List<TicketOption> ticketOptions;
    List<String> adminOnlySeeRoles;

    String adminOnlyLabel, closeTicketLabel;
    String adminOnlyEmoji, closeTicketEmoji;

    TicketStorage storage;

    long ticketTopLimit;
    boolean buttonsEnabled;

    @Override
    public void initialize() {
        buttonsEnabled = getConfigOption("buttonsEnabled").getAsBoolean();
        adminLogs = heart.getJda().getTextChannelById(getConfigOption("adminLogsChannel").getAsLong());
        ticketLogs = heart.getJda().getTextChannelById(getConfigOption("ticketLogsChannel").getAsLong());
        ticketCreate = heart.getJda().getTextChannelById(getConfigOption("ticketCreateChannel").getAsLong());
        rolesToSeeTickets = getConfigOptionList("rolesToSeeTickets");
        adminOnlySeeRoles = getConfigOptionList("adminOnlySeeRoles");
        ticketOptions = new ArrayList<>();
        getConfigOptionJsonArray("ticketOptions").forEach(o -> ticketOptions.add((TicketOption) heart.cnfMgr().JsonToObject(o, TicketOption.class)));
        adminOnlyEmoji = getConfigOption("adminOnlyEmoji").getAsString();
        closeTicketEmoji = getConfigOption("closeTicketEmoji").getAsString();
        adminOnlyLabel = getConfigOption("adminOnlyLabel").getAsString();
        closeTicketLabel = getConfigOption("closeTicketLabel").getAsString();
        storage = heart.getConfig(TicketStorage.class);
        ticketTopLimit = getConfigOption("ticketTopLimit").getAsLong();
    }

    @Override
    public void deinitialize() {
        adminLogs = null;
        ticketLogs = null;
        ticketCreate = null;
        rolesToSeeTickets = null;
        buttonsEnabled = false;
        ticketOptions = null;
        adminOnlySeeRoles = null;
        adminOnlyEmoji = null;
        closeTicketEmoji = null;
        adminOnlyLabel = null;
        ticketTopLimit = 0;
        closeTicketLabel = null;
        storage = null;
    }

    @Override
    protected void setupCommand() {
        setCommand("ticket");
        setRegisterListener(true);
        setDescription("The ticket module.");
        addRequirement(Requirement.IN_GUILD);
        addSubcommand(new SubcommandData("add", "Creates a suggestion to be added to the pending channel.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to add to the ticket.", true)));
        addSubcommand(new SubcommandData("admin_only", "Makes the current ticket admin only."));
        addSubcommand(new SubcommandData("close", "Close this current ticket.")
                .addOptions(new OptionData(OptionType.STRING, "reason", "The reason for closing the ticket.", false)));
        addSubcommand(new SubcommandData("info", "Get information on the ticket."));
        addSubcommand(new SubcommandData("leaderboard", "Send ticket top leaderboard."));
        addSubcommand(new SubcommandData("panel", "Sends the ticket panel.")
                .addOptions(new OptionData(OptionType.BOOLEAN, "pin", "Weather to pin the ticket panel or not.", true))
                .addOptions(new OptionData(OptionType.STRING, "image", "If you want to have an image that's embedded into the panel", false))
        );
        addSubcommand(new SubcommandData("remove", "Removes a user from a ticket.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to remove from the ticket.", true)));
        addSubcommand(new SubcommandData("rename", "Renames the current ticket.")
                .addOptions(new OptionData(OptionType.STRING, "name", "The new name you wish to rename the ticket", true)));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("add".equals(e.getSubcommandName())) add(e, data);
        if ("admin_only".equals(e.getSubcommandName())) adminOnly(e, data);
        if ("close".equals(e.getSubcommandName())) close(e, data);
        if ("info".equals(e.getSubcommandName())) info(e, data);
        if ("leaderboard".equals(e.getSubcommandName())) leaderboard(e, data);
        if ("panel".equals(e.getSubcommandName())) panel(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("rename".equals(e.getSubcommandName())) rename(e, data);
    }

    public void add(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;
        User user = e.getOption("user").getAsUser();
        ((TextChannel) e.getChannel()).putPermissionOverride(Objects.requireNonNull(e.getGuild().getMember(user)))
                .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                        Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                .queue();
        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .colorHexCode(heart.successColor())
                .content("Added {tag} to the ticket.")
                .build());
    }

    public void adminOnly(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;
        Ticket ticket = storage.getTicketByID(e.getChannel().getId());
        if (ticket.isAdminOnlyMode()) {
            data.replyFailure("This ticket is already admin only.");
            return;
        }
        ticket.setAdminOnlyMode(true);
        heart.cnfMgr().save(storage);
        for (String roleID : rolesToSeeTickets) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            ((TextChannel) e.getChannel()).putPermissionOverride(role)
                    .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS,
                            Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                            Permission.CREATE_INSTANT_INVITE, Permission.MESSAGE_MENTION_EVERYONE,
                            Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS, Permission.MANAGE_WEBHOOKS,
                            Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }
        for (String roleID : adminOnlySeeRoles) {
            Role role = e.getGuild().getRoleById(roleID);
            if (role == null) continue;
            ((TextChannel) e.getChannel()).putPermissionOverride(role)
                    .setAllow(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                    .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                            Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }
        data.replySuccess("Ticket is now in admin only mode.");
    }

    public void close(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;

    }

    public void info(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;
        Ticket ticket = storage.getTicketByID(e.getChannel().getId());
        StringBuilder msg = new StringBuilder();
        User user = heart.getJda().retrieveUserById(ticket.getTicketOwnerID()).complete();
        msg.append("Channel Name: `").append(ticket.getTicketName()).append("`\n");
        msg.append("Owner Tag: `").append(user.getAsTag()).append("`\n");
        msg.append("Ticket ID: `").append(ticket.getChannelID()).append("`\n");
        msg.append("Admin Only: `").append(ticket.isAdminOnlyMode() ? "Yes" : "No").append("`\n");
        data.reply(EmbedMaker.builder()
                .authorImg(user.getEffectiveAvatarUrl())
                .content(msg.toString())
                .authorName("Ticket information for: " + ticket.getTicketName())
                .build());
    }

    @SneakyThrows
    public void leaderboard(SlashCommandEvent e, CommandEventData data) {
        List<String> list = new ArrayList<>();
        ResultSet result = heart.sqlMgr().runQueryWithResult("select * from DiscordUser where ticketsClosed != 0 ORDER BY ticketsClosed DESC LIMIT " + ticketTopLimit);
        int count = 1;
        while (result.next()) {
            long id = result.getLong("id");
            User user = heart.getJda().getUserById(id);
            if (user == null) continue;
            DiscordUser discordUser = heart.genMgr().getDiscordUser(id);
            if (discordUser.getTicketsClosed() == 0) continue;
            list.add("**#" + count + "**. *" + user.getAsTag() + "*: `" + discordUser.getTicketsClosed() + " tickets closed`");
            count++;
        }

        if (list.size() == 0) {
            data.reply(EmbedMaker.builder()
                    .user(e.getUser())
                    .content("There are no users with any tickets closed.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content(heart.genMgr().formatStringList(list, "\n", false))
                .title("Tickets closed leaderboard")
                .colorHexCode(heart.successColor())
                .build());
    }

    public void panel(SlashCommandEvent e, CommandEventData data) {
        boolean pin = e.getOption("pin").getAsBoolean();
        String image = e.getOption("image") == null ? null : e.getOption("image").getAsString();
        StringBuilder msg = new StringBuilder();

        msg.append("Click the button below to create a ticket.").append("\n\n");

        ticketOptions.forEach(ticketOption -> msg.append(Emoji.fromMarkdown(ticketOption.getEmoji()).getAsMention()).append("** - ").append(ticketOption.getDisplayName()).append("**\n"));

        List<Button> buttons = new ArrayList<>();
        if (buttonsEnabled) {
            int index = 0;
            for (TicketOption ticketOption : ticketOptions) {
                Button button = Button.primary("TICKET_OPTION_" + index, Emoji.fromUnicode(ticketOption.getEmoji())).withLabel(ticketOption.getButtonLabel());
                buttons.add(button);
                index++;
            }
        }

        MessageAction panelAction = e.getChannel().sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .authorImg(heart.getGlobalImageUrl())
                .authorName("Ticket Creation Panel")
                .image(image)
                .content(msg.toString())
                .build()));

        if (buttonsEnabled) panelAction = panelAction.setActionRow(buttons);

        Message panel = panelAction.complete();

        if (!buttonsEnabled)
            ticketOptions.forEach(ticketOption -> panel.addReaction(Emoji.fromUnicode(ticketOption.getEmoji()).getAsMention()).queue());
        if (pin) panel.pin().queue();

        e.reply("Ticket panel sent.").setEphemeral(true).queue();
    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;
        User user = e.getOption("user").getAsUser();
        ((TextChannel) e.getChannel()).putPermissionOverride(e.getGuild().getMember(user))
                .setDeny(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE,
                        Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                        Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                .queue();

        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .colorHexCode(heart.successColor())
                .content("Removed {tag} to the ticket.")
                .build());
    }

    public void rename(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;
        Ticket ticket = storage.getTicketByID(e.getChannel().getId());
        String newTicketName = e.getOption("name").getAsString().replace(" ", "-") + ticket.getTicketNumber();
        ((TextChannel) e.getChannel()).getManager().setName(newTicketName).queue();
        ticket.setTicketName(newTicketName);
        heart.cnfMgr().save(storage);
        data.replySuccess("Renamed the ticket to: " + newTicketName);
    }

    // Utility methods

    public boolean ticketCheck(SlashCommandEvent e, CommandEventData data) {
        boolean isTicket = heart.getConfig(TicketStorage.class).getTicketByID(e.getChannel().getId()) != null;
        if (!isTicket) data.replyFailure("This command can only be used in a ticket channel.");
        return isTicket;
    }


}
