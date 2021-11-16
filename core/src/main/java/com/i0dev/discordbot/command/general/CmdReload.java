package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CmdReload extends DiscordCommand {

    public CmdReload(Heart heart, CommandData commandData) {
        super(heart, commandData);
    }

    @Override
    protected void setupCommand() {
        setCommand("reload");
        setDescription("Reloads the configuration for the bot.");
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        heart.registerConfigs();
        data.reply(EmbedMaker.builder()
                .content("You have reloaded the configuration.")
                .colorHexCode(getHeart().successColor())
                .build());
    }

}
