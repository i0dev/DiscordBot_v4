package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.configs.TicketConfig;
import com.i0dev.discordbot.config.storage.TicketStorage;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.LogObject;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.command.Ticket;
import com.i0dev.discordbot.object.command.TicketOption;
import com.i0dev.discordbot.task.TaskRunTicketLogQueue;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.File;
import java.sql.ResultSet;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public class CmdTicket extends DiscordCommand {

    public CmdTicket(Heart heart) {
        super(heart);
    }


    TicketConfig cnf;
    TicketStorage storage;

    @Override
    public void initialize() {
        new File(heart.getDataFolder() + "/ticketLogs/").mkdir();
        cnf = heart.getConfig(TicketConfig.class);
        storage = heart.getConfig(TicketStorage.class);
    }

    @Override
    public void deinitialize() {
        cnf = null;
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
                .setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES,
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
            ticket.setAdminOnlyMode(false);
            setTicketNormalaMode(ticket);
            data.replySuccess("Ticket is no longer admin only.");
            return;
        } else {
            ticket.setAdminOnlyMode(true);
            setTicketAdminOnly(ticket);
            data.replySuccess("Ticket is now in admin only mode.");
        }
        heart.cnfMgr().save(storage);
    }

    public void close(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;
        Ticket ticket = storage.getTicketByID(e.getChannel().getId());
        String reason = e.getOption("reason") == null ? cnf.getDefaultCloseReason() : e.getOption("close").getAsString();
        closeTicket(ticket, reason, e.getUser());
        e.deferReply().queue();
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
        ResultSet result = heart.sqlMgr().runQueryWithResult("select * from DiscordUser where ticketsClosed != 0 ORDER BY ticketsClosed DESC LIMIT " + cnf.getTicketTopMaxDisplay());
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

        cnf.getTicketOptions().forEach(ticketOption -> msg.append(Emoji.fromMarkdown(ticketOption.getEmoji()).getAsMention()).append("** - ").append(ticketOption.getDisplayName()).append("**\n"));

        List<Button> buttons = new ArrayList<>();
        if (cnf.isTicketCreateButtonMode()) {
            for (TicketOption ticketOption : cnf.getTicketOptions()) {
                Button button = Button.primary("TICKET_OPTION_" + ticketOption.getTicketID(), Emoji.fromUnicode(ticketOption.getEmoji())).withLabel(ticketOption.getButtonLabel());
                buttons.add(button);
            }
        }

        MessageAction panelAction = e.getChannel().sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .authorImg(heart.getGlobalImageUrl())
                .authorName("Ticket Creation Panel")
                .image(image)
                .content(msg.toString())
                .build()));

        if (cnf.isTicketCreateButtonMode()) panelAction = panelAction.setActionRow(buttons);

        Message panel = panelAction.complete();

        if (!cnf.isTicketCreateButtonMode())
            cnf.getTicketOptions().forEach(ticketOption -> panel.addReaction(Emoji.fromUnicode(ticketOption.getEmoji()).getAsMention()).queue());
        if (pin) panel.pin().queue();

        e.reply("Ticket panel sent.").setEphemeral(true).queue();
    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        if (!ticketCheck(e, data)) return;
        User user = e.getOption("user").getAsUser();
        ((TextChannel) e.getChannel()).putPermissionOverride(e.getGuild().getMember(user))
                .setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES,
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

    public boolean ticketCheck(GuildChannel channel) {
        return heart.getConfig(TicketStorage.class).getTicketByID(channel.getId()) != null;
    }

    public boolean ticketCheck(String channelID) {
        return heart.getConfig(TicketStorage.class).getTicketByID(channelID) != null;
    }

    public boolean ticketCheck(SlashCommandEvent e, CommandEventData data) {
        boolean isTicket = heart.getConfig(TicketStorage.class).getTicketByID(e.getChannel().getId()) != null;
        if (!isTicket) data.replyFailure("This command can only be used in a ticket channel.");
        return isTicket;
    }

    public void setTicketNormalaMode(Ticket ticket) {
        TextChannel channel = heart.getJda().getTextChannelById(ticket.getChannelID());
        if (channel == null) return;
        TicketOption option = getTicketOptionById(ticket.getTicketID());
        if (option == null) return;
        for (Long roleID : option.getRolesToSee().isEmpty() ? cnf.getDefaultRolesToSeeTickets() : option.getRolesToSee()) {
            Role role = channel.getGuild().getRoleById(roleID);
            if (role == null) continue;
            channel.putPermissionOverride(role)
                    .setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                    .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                            Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }
    }

    public void denyPermissions(TextChannel channel, List<Long> roleIds) {
        for (Long roleID : roleIds) {
            Role role = channel.getGuild().getRoleById(roleID);
            if (role == null) continue;
            channel.putPermissionOverride(role)
                    .setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE,
                            Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                            Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                    .queue();
        }
    }

    public void setTicketAdminOnly(Ticket ticket) {
        TextChannel channel = heart.getJda().getTextChannelById(ticket.getChannelID());
        if (channel == null) return;
        TicketOption option = getTicketOptionById(ticket.getTicketID());
        if (option == null) return;
        denyPermissions(channel, option.getRolesToSee().isEmpty() ? cnf.getDefaultRolesToSeeTickets() : option.getRolesToSee());
        for (Long roleID : cnf.getDefaultAdminOnlySeeRoles()) {
            Role role = channel.getGuild().getRoleById(roleID);
            if (role == null) continue;
            channel.putPermissionOverride(role)
                    .setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE)
                    .queue();
        }
    }

    public TextChannel createTicket(TicketOption option, User owner, Guild guild) {
        Member member = guild.getMember(owner);
        Category category = heart.getJda().getCategoryById(option.getCategory()) == null ? heart.getJda().getCategoryById(cnf.getDefaultTicketCreateCategoryID()) : heart.getJda().getCategoryById(option.getCategory());
        long newTicketNumber = storage.getTicketNumber() + 1;
        storage.setTicketNumber(newTicketNumber);
        String channelName = option.getChannelName().replace("{num}", String.valueOf(newTicketNumber));
        TextChannel channel = category.createTextChannel(channelName).complete();
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(newTicketNumber);
        ticket.setTicketName(channelName);
        ticket.setTicketOwnerID(owner.getIdLong());
        ticket.setAdminOnlyMode(option.isAdminOnlyDefault());
        ticket.setChannelID(channel.getIdLong());
        ticket.setTicketID(option.getTicketID());
        storage.getTickets().add(ticket);
        heart.cnfMgr().save(storage);

        channel.putPermissionOverride(member)
                .setAllow(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE, Permission.USE_APPLICATION_COMMANDS)
                .setDeny(Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                        Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                .queue();

        channel.putPermissionOverride(guild.getPublicRole())
                .setDeny(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_ADD_REACTION, Permission.CREATE_INSTANT_INVITE,
                        Permission.MESSAGE_MENTION_EVERYONE, Permission.MESSAGE_MANAGE, Permission.MESSAGE_TTS,
                        Permission.MANAGE_WEBHOOKS, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_CHANNEL)
                .queue();

        if (option.isAdminOnlyDefault()) setTicketAdminOnly(ticket);
        else setTicketNormalaMode(ticket);
        List<String> toPing = Arrays.asList(owner.getAsMention());
        if (option.isPingStaff()) {
            if (option.getRolesToPing().isEmpty()) {
                for (Long aLong : cnf.getDefaultRolesToPing()) {
                    Role role = guild.getRoleById(aLong);
                    if (role == null) continue;
                    toPing.add(role.getAsMention());
                }
            } else {
                for (Long aLong : option.getRolesToPing()) {
                    Role role = guild.getRoleById(aLong);
                    if (role == null) continue;
                    toPing.add(role.getAsMention());
                }
            }
        }
        String pingMessage = heart.genMgr().formatStringList(toPing, ", ", false);
        channel.sendMessage(pingMessage).queue();

        String userInfo = "Linked IGN: `{ign}`\n" +
                "Ticket Number: `" + newTicketNumber + "`\n" +
                "Category: `" + category.getName() + "`\n";

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                        .user(owner)
                        .authorImg(owner.getEffectiveAvatarUrl())
                        .authorName("New ticket from: {tag}")
                        .fields(new MessageEmbed.Field[]{
                                new MessageEmbed.Field("__Questions:__", "```" + heart.genMgr().formatStringList(option.getQuestions(), "\n", true) + "```", true),
                                new MessageEmbed.Field("__Information:__", userInfo, true)
                        })

                        .build()))
                .setActionRow(Button.danger("BUTTON_TICKET_CLOSE", cnf.getCloseTicketButtonLabel()).withEmoji(net.dv8tion.jda.api.entities.Emoji.fromMarkdown(cnf.getCloseTicketButtonEmoji())),
                        Button.success("BUTTON_TICKET_ADMIN_ONLY", cnf.getAdminOnlyButtonLabel()).withEmoji(net.dv8tion.jda.api.entities.Emoji.fromMarkdown(cnf.getAdminOnlyButtonEmoji())))
                .queue();

        createTicketLogs(ticket, channel, owner);

        return channel;
    }

    public void createTicketLogs(Ticket ticket, TextChannel channel, User owner) {
        File ticketLogsFile = new File(heart.getDataFolder() + "/ticketLogs/" + channel.getId() + ".log");

        String Zone = ZonedDateTime.now().getZone().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        Long ticketOwnerID = ticket.getTicketOwnerID();
        String ticketOwnerAvatarURL = owner.getEffectiveAvatarUrl();
        String ticketOwnerTag = owner.getAsTag();
        boolean adminOnlyMode = ticket.isAdminOnlyMode();

        StringBuilder toFile = new StringBuilder();
        toFile.append("Ticket information:").append("\n");
        toFile.append("     Channel ID: ").append(channel.getId()).append("\n");
        toFile.append("     Channel Name: ").append(channel.getName()).append("\n");
        toFile.append("     Ticket Owner ID: ").append(ticketOwnerID).append("\n");
        toFile.append("     Ticket Owner Tag: ").append(ticketOwnerTag).append("\n");
        toFile.append("     Ticket Owner Avatar: ").append(ticketOwnerAvatarURL).append("\n");
        toFile.append("     Admin Only Mode: ").append(adminOnlyMode).append("\n");
        toFile.append("Ticket logs (TimeZone: ").append(Zone).append("):");

        heart.getTask(TaskRunTicketLogQueue.class).getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));
    }

    public boolean isMaxTicketsOpen(User user) {
        AtomicInteger count = new AtomicInteger(0);
        storage.getTickets().stream().filter(ticket -> user.getIdLong() == ticket.getTicketOwnerID()).forEach(ticket -> count.incrementAndGet());
        return count.get() > cnf.getMaxTicketsPerUser();
    }

    public TicketOption getTicketOptionById(String id) {
        return cnf.getTicketOptions().stream().filter(option -> option.getTicketID().equals(id)).findFirst().orElse(null);
    }

    public boolean hasPermission(ButtonClickEvent e) {
        return e.getGuild() != null && e.getMember() != null && e.getMember().hasPermission(Permission.ADMINISTRATOR) && heart.gCnf().isAdministratorBypassPermissions();
    }


    @SneakyThrows
    void closeTicket(Ticket ticket, String reason, User closer) {
        File ticketLogsFile = new File(heart.getDataFolder() + "/ticketLogs/" + ticket.getChannelID() + ".log");
        TextChannel channel = heart.getJda().getTextChannelById(ticket.getChannelID());
        DiscordUser discordUser = heart.genMgr().getDiscordUser(closer);
        String toFile = "\n\nClosed Ticket Information:\n " +
                "  Ticket Closer Tag: " + closer.getAsTag() + "\n" +
                "   Ticket Closer ID: " + closer.getId() + "\n" +
                "   Ticket Close reason: " + reason;
        heart.getTask(TaskRunTicketLogQueue.class).getToLog().add(new LogObject(toFile, ticketLogsFile));
        heart.getTask(TaskRunTicketLogQueue.class).execute();
        ticketLogsFile = new File(heart.getDataFolder() + "/ticketLogs/" + ticket.getChannelID() + ".log");
        User ticketOwner = heart.getJda().retrieveUserById(ticket.getTicketOwnerID()).complete();

        discordUser.setTicketsClosed(discordUser.getTicketsClosed() + 1);
        discordUser.save();

        channel.delete().queueAfter(5, TimeUnit.SECONDS);

        channel.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .colorHexCode(heart.successColor())
                .content("This ticket will close in 5 seconds.")
                .build())).queue();

        EmbedMaker.EmbedMakerBuilder embedMaker = EmbedMaker.builder()
                .authorImg(ticketOwner.getEffectiveAvatarUrl())
                .user(ticketOwner)
                .author(closer)
                .authorName("{tag}'s ticket was closed by {authorTag}")
                .field(new MessageEmbed.Field("Ticket " + ticket.getTicketName(), "Close reason: `{reason}`".replace("{reason}", reason), false));

        TextChannel logs;
        if (ticket.isAdminOnlyMode()) logs = heart.getJda().getTextChannelById(cnf.getAdminLogsChannelID());
        else logs = heart.getJda().getTextChannelById(cnf.getAdminLogsChannelID());

        if (logs != null) {
            logs.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(embedMaker.build())).queueAfter(5, TimeUnit.MILLISECONDS);
            logs.sendFile(ticketLogsFile).queueAfter(5 + 1000, TimeUnit.MILLISECONDS);
        }

        storage.getTickets().remove(ticket);
        try {
            ticketOwner.openPrivateChannel().complete().sendMessageEmbeds(heart.msgMgr().createMessageEmbed(embedMaker.build())).completeAfter(5, TimeUnit.SECONDS);
            embedMaker.authorName("Your ticket was closed by {authorTag}");
            ticketOwner.openPrivateChannel().complete().sendFile(ticketLogsFile).queueAfter(6, TimeUnit.SECONDS);
        } catch (Exception ignored) {

        }
    }


    // Create & Admin Only Handler
    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getUser().isBot()) return;
        if (e.getGuild() == null) return;
        if (!heart.genMgr().isAllowedGuild(e.getGuild())) return;
        if (e.getButton() == null || e.getButton().getId() == null) return;

        if (heart.genMgr().getDiscordUser(e.getUser().getIdLong()).isBlacklisted()) {
            e.reply("You are blacklisted from using this bot.").setEphemeral(true).queue();
            return;
        }


        if ("BUTTON_TICKET_ADMIN_ONLY".equalsIgnoreCase(e.getButton().getId())) {

            if (true) {
                e.reply("You don't have permission to use admin only.").setEphemeral(true).queue();
            }

            Ticket ticket = storage.getTicketByID(e.getChannel().getId());
            if (ticket.isAdminOnlyMode()) {
                ticket.setAdminOnlyMode(false);
                setTicketNormalaMode(ticket);
                e.getChannel().sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder().colorHexCode(heart.successColor()).content("Ticket is no longer admin only.").build())).queue();
                return;
            } else {
                ticket.setAdminOnlyMode(true);
                setTicketAdminOnly(ticket);
                e.getChannel().sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder().colorHexCode(heart.successColor()).content("Ticket is now in admin only mode.").build())).queue();
            }
            heart.cnfMgr().save(storage);
            return;
        }
        if ("BUTTON_TICKET_CLOSE".equalsIgnoreCase(e.getButton().getId())) {
            Ticket ticket = storage.getTicketByID(e.getChannel().getId());
            if (ticket == null) return;
            if (!cnf.isAllowTicketOwnerToCloseOwnTicket() && ticket.getTicketOwnerID() != e.getUser().getIdLong()) {
                if (true) {
                    e.reply("You don't have permission to close tickets.").setEphemeral(true).queue();
                }
            }
            closeTicket(ticket, cnf.getDefaultCloseReason(), e.getUser());
            e.deferReply().queue();
            return;
        }

        if (!e.getButton().getId().startsWith("TICKET_OPTION_")) return;
        String ticketID = e.getButton().getId().replace("TICKET_OPTION_", "");
        TicketOption ticketOption = getTicketOptionById(ticketID);
        if (isMaxTicketsOpen(e.getUser())) {
            e.reply("You have reached the maximum amount of tickets open. Please close one before creating a new one.").setEphemeral(true).queue();
            return;
        }
        TextChannel channel = createTicket(ticketOption, e.getUser(), e.getGuild());
        e.reply("Your ticket has been created. You can now start chatting in <#" + channel.getId() + ">").setEphemeral(true).queue();
    }

    // LOG HANDLING
    @Override
    @SneakyThrows
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!ticketCheck(e.getChannel().getId())) return;
        File ticketLogsFile = new File(heart.getDataFolder() + "/ticketLogs/" + e.getChannel().getId() + ".log");
        StringBuilder toFile = new StringBuilder();
        for (Message.Attachment attachment : e.getMessage().getAttachments()) {
            toFile.append(heart.genMgr().formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ")
                    .append("[FILE]" + attachment.getUrl())
                    .append("\n");
        }
        if (e.getMessage().getEmbeds().size() != 0) {
            MessageEmbed embed = e.getMessage().getEmbeds().get(0);
            toFile.append(heart.genMgr().formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ")
                    .append("[EMBED]" + "\n   Title: ")
                    .append(embed.getTitle()).append("\n   Desc: ")
                    .append(embed.getDescription()).append("\n");
            if (embed.getFooter() != null) {
                toFile.append("   Footer: ").append(embed.getFooter().getText());
            }
        } else {
            if (!e.getMessage().getContentDisplay().equals("")) {
                toFile.append(heart.genMgr().formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ").append(e.getMessage().getContentDisplay());
            }
        }
        heart.getTask(TaskRunTicketLogQueue.class).getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));
    }

    @Override
    @SneakyThrows
    public void onMessageUpdate(MessageUpdateEvent e) {
        if (!ticketCheck(e.getChannel().getId())) return;
        File ticketLogsFile = new File(heart.getDataFolder() + "/ticketLogs/" + e.getChannel().getId() + ".log");
        StringBuilder toFile = new StringBuilder();

        toFile.append(heart.genMgr().formatDate(System.currentTimeMillis())).append(" [").append(e.getAuthor().getAsTag()).append("]: ")
                .append("[MESSAGE EDIT]" + e.getMessage().getContentDisplay())
                .append("\n");

        heart.getTask(TaskRunTicketLogQueue.class).getToLog().add(new LogObject(toFile.toString(), ticketLogsFile));
    }
}
