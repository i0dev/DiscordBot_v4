package com.i0dev.discordbot.command.aliases;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.command.CmdTicket;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.command.Ticket;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdClose extends DiscordCommand {
    public CmdClose(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("close");
        setPermissionOverride("ticket_close");
        setDescription("Shortcut for /ticket close");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.STRING, "reason", "The reason for closing the ticket.", false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        CmdTicket cmdTicket = heart.getCommand(CmdTicket.class);
        if (!cmdTicket.ticketCheck(e, data)) return;
        Ticket ticket = cmdTicket.getStorage().getTicketByID(e.getChannel().getId());
        String reason = e.getOption("reason") == null ? cmdTicket.getCnf().getDefaultCloseReason() : e.getOption("reason").getAsString();
        cmdTicket.closeTicket(ticket, reason, e.getUser());
        if (e.isAcknowledged()) return;
        e.deferReply().queue();
    }
}
