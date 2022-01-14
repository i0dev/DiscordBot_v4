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

public class CmdHelp extends DiscordCommand {

    public CmdHelp(Heart heart) {
        super(heart);
    }

    int pages = 2;

    @Override
    protected void setupCommand() {
        setCommand("help");

        List<Command.Choice> choices = new ArrayList<>();
        for (int i = 0; i < pages; i++) {
            choices.add(new Command.Choice("" + i + 1, i + 1));
        }
        addOption(new OptionData(OptionType.INTEGER, "page", "page number", true)
                .addChoices(choices)
        );
        setDescription("Gets the help page.");
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        long page = e.getOption("page").getAsLong();
        StringBuilder commands = new StringBuilder();
        StringBuilder multiCommands = new StringBuilder();
        for (DiscordCommand command : getHeart().getCommands()) {
                commands.append("`/").append(command.getCommand()).append("` - *").append(command.getDescription()).append("*\n");
        }


        data.reply(EmbedMaker.builder()
                .authorName("DiscordBot Help Page " + page)
                .fields(new MessageEmbed.Field[]{
                                new MessageEmbed.Field("__**Commands**__", commands.toString(), true),
                                new MessageEmbed.Field("__**Multi Commands**__", multiCommands.toString(), true)
                        }
                )
                .authorImg(heart.getGlobalImageUrl())
                .build());
    }

}
