package com.i0dev.discordbot.config.storage;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.command.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Setter
@NoArgsConstructor
public class TicketStorage extends AbstractConfiguration {

    public TicketStorage(Heart heart, String path) {
        this.path = path;
        this.heart = heart;    }


    long ticketNumber = 1;
    List<Ticket> tickets = new ArrayList<>();

    public Ticket getTicketByID(String ID) {
        return tickets.stream().filter(suggestion -> (suggestion.getChannelID() + "").equalsIgnoreCase(ID)).findFirst().orElse(null);
    }


}
