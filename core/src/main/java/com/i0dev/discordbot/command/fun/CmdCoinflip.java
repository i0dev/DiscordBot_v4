package com.i0dev.discordbot.command.fun;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CmdCoinflip extends DiscordCommand {
    public CmdCoinflip(Heart heart, CommandData configSection) {
        super(heart, configSection);
    }

    @Override
    protected void setupCommand() {
        setCommand("coinflip");
        setDescription("Flip a coin!");
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content("You flipped a coin and you got: `{flip}`".replace("{flip}", heart.genMgr().randomNumber(2) == 1 ? "Heads" : "Tails"))
                .colorHexCode(heart.successColor())
                .build());
    }
}
