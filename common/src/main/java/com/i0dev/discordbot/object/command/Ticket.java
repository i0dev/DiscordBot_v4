package com.i0dev.discordbot.object.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Ticket {
    long channelID;

    String ticketName;
    long ticketOwnerID, ticketNumber;
    boolean adminOnlyMode;

    String ticketID;
}
