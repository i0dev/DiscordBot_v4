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

package com.i0dev.discordbot.config.configs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.command.TicketOption;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

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
    boolean allowTicketOwnerToAdminOnly = true;
    boolean countSelfClosedTicketsTowardsTicketTop = false;

    String ticketPanelDescription = "Click the corresponding button to create a ticket. If the matter of the ticket has sensitive information, please ask for the ticket to be made admin-only.";

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
                    new ArrayList<>(),
                    true,
                    "GREEN"
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
                    new ArrayList<>(),
                    true,
                    "RED"
            )
    );
}
