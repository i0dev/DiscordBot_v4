package com.i0dev.discordbot.object.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Ticket {
    long channelID;

    String ticketName;
    long ticketOwnerID, ticketNumber;
    boolean adminOnlyMode;
}
