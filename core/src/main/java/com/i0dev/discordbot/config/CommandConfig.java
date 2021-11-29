package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.command.TicketOption;
import com.i0dev.discordbot.object.config.CommandData;
import com.i0dev.discordbot.object.config.MultiCommandData;
import com.i0dev.discordbot.object.config.NamedCommandData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@ToString
@Setter
public class CommandConfig extends AbstractConfiguration {

    public CommandConfig(Heart heart, String path) {
        super(heart, path);
    }

    //General
    CommandData help = new CommandData(heart, false);
    CommandData avatar = new CommandData(heart, false);
    CommandData members = new CommandData(heart, false);
    CommandData reload = new CommandData(heart, true);
    CommandData profile = new CommandData(heart, false);
    CommandData roleInfo = new CommandData(heart, false);
    CommandData roles = new CommandData(heart, false);
    CommandData serverLookup = new CommandData(heart, false);
    CommandData botInfo = new CommandData(heart, false);
    CommandData serverInfo = new CommandData(heart, false);

    // Moderation
    CommandData ban = new CommandData(heart, true);
    CommandData kick = new CommandData(heart, true);
    CommandData announce = new CommandData(heart, true);
    CommandData changelog = new CommandData(heart, true);
    CommandData directMessage = new CommandData(heart, true);
    CommandData prune = new CommandData(heart, true);
    CommandData verifyPanel = new CommandData(heart, true)
            .addOption("buttonLabel", "Click to verify")
            .addOption("buttonEmoji", "U+2705")
            .addMessage("content", "To ensure a safe and mutually beneficial experience, all users are required to verify themselves as an actual human. It is your responsibility as a client to read all the rules. Once you agree to them you will be bound by them for as long as you are on this server. When you are done reading them, select the reaction at the Bottom to acknowledge and agree to these terms, which will grant you access to the rest of the server.")
            .addMessage("title", "Secure Verification");

    // Fun
    CommandData coinflip = new CommandData(heart, false);

    // Multi Commands
    MultiCommandData blacklist = new MultiCommandData(heart, Arrays.asList(
            new NamedCommandData(heart, "add", true),
            new NamedCommandData(heart, "remove", true),
            new NamedCommandData(heart, "list", true),
            new NamedCommandData(heart, "clear", true)
    ), true);

    MultiCommandData mute = (MultiCommandData) new MultiCommandData(heart, Arrays.asList(
            new NamedCommandData(heart, "add", true),
            new NamedCommandData(heart, "remove", true),
            new NamedCommandData(heart, "list", true),
            new NamedCommandData(heart, "clear", true),
            new NamedCommandData(heart, "create", true)
    ), true).addOption("muteRole", 0L);

    MultiCommandData invite = new MultiCommandData(heart, Arrays.asList(
            new NamedCommandData(heart, "invites", false),
            (NamedCommandData) new NamedCommandData(heart, "leaderboard", false).addOption("limit", 30),
            new NamedCommandData(heart, "add", true),
            new NamedCommandData(heart, "remove", true),
            new NamedCommandData(heart, "clear", true)
    ), false);

    MultiCommandData suggestion = (MultiCommandData) new MultiCommandData(heart, Arrays.asList(
            new NamedCommandData(heart, "add", false),
            new NamedCommandData(heart, "accept", true),
            new NamedCommandData(heart, "deny", true)
    ), false)
            .addOption("pending", 0L)
            .addOption("accepted", 0L)
            .addOption("denied", 0L)
            .addOption("upvoteEmoji", "U+1F44D")
            .addOption("downvoteEmoji", "U+1F44E");

    MultiCommandData ticket = (MultiCommandData) new MultiCommandData(heart, Arrays.asList(
            new NamedCommandData(heart, "add", false),
            new NamedCommandData(heart, "admin_only", true),
            new NamedCommandData(heart, "close", true),
            new NamedCommandData(heart, "info", true),
            new NamedCommandData(heart, "leaderboard", true),
            new NamedCommandData(heart, "panel", true),
            new NamedCommandData(heart, "remove", true),
            new NamedCommandData(heart, "rename", true)
    ), false)
            .addOption("adminLogsChannel", 0L)
            .addOption("ticketCreateChannel", 0L)
            .addOption("ticketLogsChannel", 0L)
            .addOption("adminOnlyLabel", "Admin Only")
            .addOption("maxTicketsPerUser", 4L)
            .addOption("closeTicketLabel", "Close")
            .addOption("adminOnlyEmoji", "U+1F514")
            .addOption("closeTicketEmoji", "U+1F5D1")
            .addOption("ticketTopLimit", 20L)
            .addOption("rolesToPing", new ArrayList<>())
            .addOption("defaultCloseReason","Have a great day!")
            .addOption("allowTicketOwnerToCloseOwnTicket", true)
            .addOption("defaultCategory", 879086812776239166L)
            .addOption("buttonsEnabled", true)
            .addOption("adminOnlySeeRoles", Arrays.asList(0L, 0L))
            .addOption("rolesToSeeTickets", Arrays.asList(0L, 0L))
            .addOption("ticketOptions", Arrays.asList(
                    new TicketOption(
                            "support",
                            Arrays.asList("What is your IGN", "What realm is this ticket for?"),
                            0L,
                            "support-{num}",
                            "U+1F39F",
                            "Support Ticket",
                            "Support",
                            false,
                            false,
                            new ArrayList<>(),
                            new ArrayList<>()
                    ),
                    new TicketOption(
                            "admin",
                            Arrays.asList("What is your IGN", "What realm is this ticket for?"),
                            0L,
                            "admin-{num}",
                            "U+1F514",
                            "Admin Ticket",
                            "Admin",
                            true,
                            true,
                            new ArrayList<>(),
                            new ArrayList<>()
                    )

            ));
}