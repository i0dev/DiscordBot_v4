package com.i0dev.discordbot.config.configs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.command.TicketOption;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class TicketConfig extends AbstractConfiguration {
    public TicketConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    long ticketLogsChannelID = 0L;
    long adminLogsChannelID = 0L;
    long defaultTicketCreateCategoryID = 0L;

    long maxTicketsPerUser = 4L;
    long ticketTopMaxDisplay = 30L;

    boolean ticketCreateButtonMode = true;
    boolean allowTicketOwnerToCloseOwnTicket = true;

    String defaultCloseReason = "Have a wonderful day!";
    String adminOnlyButtonLabel = "Admin Only";
    String closeTicketButtonLabel = "Close Ticket";
    String adminOnlyButtonEmoji = "U+1F514";
    String closeTicketButtonEmoji = "U+1F5D1";

    List<Long> ticketCreateChannelListeners = Collections.singletonList(0L);
    List<Long> defaultRolesToPing = new ArrayList<>();
    List<Long> defaultAdminOnlySeeRoles = new ArrayList<>();
    List<Long> defaultRolesToSeeTickets = new ArrayList<>();

    List<TicketOption> ticketOptions = Arrays.asList(
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
            ));
}
