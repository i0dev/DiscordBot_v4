package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmdHelp extends DiscordCommand {

    public CmdHelp(Heart heart) {
        super(heart);
    }

    int pages = 2;

    @Override
    protected void setupCommand() {
        setCommand("help");
        addOption(new OptionData(OptionType.INTEGER, "page", "page number", true));
        setDescription("Gets the help page.");
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        long page = e.getOption("page").getAsLong();
        List<DiscordCommand> basicCommands = heart.getCommands().stream().filter(cmd -> cmd.getSubCommands().isEmpty()).collect(Collectors.toList());
        List<DiscordCommand> multiCommands = heart.getCommands().stream().filter(cmd -> !cmd.getSubCommands().isEmpty()).collect(Collectors.toList());

        List<String> helpRows = new ArrayList<>();
        basicCommands.forEach(command1 -> helpRows.add("`/" + command1.getCommand() + "` - " + command1.getDescription()));
        multiCommands.forEach(cmd -> {
            StringBuilder sb = new StringBuilder();
            sb.append("**/").append(cmd.getCommand()).append("**\n");
            cmd.getSubCommands().forEach(subCmd -> {
                sb.append("** • **`").append(subCmd.getName()).append("` - ").append(subCmd.getDescription()).append("\n");
            });
            helpRows.add(sb.toString());
        });

        long maxPages = (long) Math.ceil(helpRows.size() / (double) pages);
        long rowsPerPAge = 10;
        int startingRow = page == 1 ? 0 : (int) (page * rowsPerPAge);
        List<String> currentRows = helpRows.subList(startingRow, (int) Math.min(startingRow + rowsPerPAge, helpRows.size()));

        StringBuilder sb = new StringBuilder();
        currentRows.forEach(sb::append);

        data.reply(EmbedMaker.builder()
                .authorName("DiscordBot Help - Page " + page + "/" + maxPages)
                .user(e.getUser())
                .authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl())
                .content(sb.toString())
                .build());

    }
}
