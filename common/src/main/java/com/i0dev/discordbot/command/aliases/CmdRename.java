package com.i0dev.discordbot.command.aliases;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.command.CmdTicket;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.command.Ticket;
import com.i0dev.discordbot.util.ConfigUtil;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdRename extends DiscordCommand {
    public CmdRename(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("rename");
        setPermissionOverride("ticket_close");
        setDescription("Shortcut for /ticket rename");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.STRING, "name", "Rename the ticket to a new name!", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        CmdTicket cmdTicket = heart.getCommand(CmdTicket.class);

        if (!cmdTicket.ticketCheck(e, data)) return;
        Ticket ticket = cmdTicket.getStorage().getTicketByID(e.getChannel().getId());
        String newTicketName = e.getOption("name").getAsString().replace(" ", "-") + "-" + ticket.getTicketNumber();
        ((TextChannel) e.getChannel()).getManager().setName(newTicketName).queue();
        ticket.setTicketName(newTicketName);
        ConfigUtil.save(cmdTicket.getStorage());
        data.replySuccess("Renamed the ticket to: " + newTicketName.toLowerCase());
    }
}
