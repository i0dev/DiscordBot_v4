package com.i0dev.discordbot.config.storage;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.command.Ticket;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Setter
public class TicketStorage extends AbstractConfiguration {

    public TicketStorage(Heart heart, String path) {
        super(heart, path);
    }


    long ticketNumber = 1;
    List<Ticket> tickets = new ArrayList<>();

    public Ticket getTicketByID(String ID) {
        return tickets.stream().filter(suggestion -> (suggestion.getChannelID() + "").equalsIgnoreCase(ID)).findFirst().orElse(null);
    }


}
