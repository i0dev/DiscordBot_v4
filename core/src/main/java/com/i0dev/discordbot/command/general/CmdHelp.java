package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import com.i0dev.discordbot.object.config.MultiCommandData;
import com.i0dev.discordbot.object.config.NamedCommandData;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

public class CmdHelp extends DiscordCommand {

    public CmdHelp(Heart heart, CommandData commandData) {
        super(heart, commandData);
    }

    int pages = 2;

    @Override
    protected void setupCommand() {
        setCommand("help");

        List<Command.Choice> choices = new ArrayList<>();
        for (int i = 0; i < pages; i++) {
            choices.add(new Command.Choice("" + i + 1, i + 1));
        }
        addOption(new OptionData(OptionType.INTEGER, "page", "page number", true).setRequiredRange(1, pages)
                .addChoices(choices)
        );
        setDescription("Gets the help page.");
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        StringBuilder commands = new StringBuilder();
        StringBuilder multiCommands = new StringBuilder();
        for (DiscordCommand command : getHeart().getCommands()) {
            if (command.getConfigSection() instanceof MultiCommandData) {
                multiCommands.append("**/").append(command.getCommand()).append("**").append("\n");
                for (SubcommandData subCommand : command.getSubCommands()) {
                    multiCommands.append("• `").append(subCommand.getName()).append("` - *").append(subCommand.getDescription()).append("*\n");
                }
                multiCommands.append("\n");
            } else
                commands.append("`/").append(command.getCommand()).append("` - *").append(command.getDescription()).append("*\n");
        }


        data.reply(EmbedMaker.builder()
                .authorName("DiscordBot Help Page")
                .fields(new MessageEmbed.Field[]{
                                new MessageEmbed.Field("__**Commands**__", commands.toString(), true),
                                new MessageEmbed.Field("__**Multi Commands**__", multiCommands.toString(), true)
                        }
                )
                .authorImg(heart.getGlobalImageUrl())
                .build());
    }

}
