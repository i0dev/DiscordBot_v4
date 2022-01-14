package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class CmdServerLookup extends DiscordCommand {
    public CmdServerLookup(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("server_lookup");
        addOption(new OptionData(OptionType.STRING, "address", "server ip address", true));
        setDescription("Get information about a minecraft server");
    }

    @SneakyThrows
    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        String ip = e.getOption("address").getAsString();
        JSONObject json = heart.apiMgr().MinecraftServerLookup(ip);
        if (json == null) {
            data.reply(EmbedMaker.builder().content("Cannot find target server!").colorHexCode(heart.failureColor()).build());
            return;
        }

        boolean online = (boolean) json.get("online");
        StringBuilder msg = new StringBuilder();
        msg.append("__**Server Information**__").append("\n");
        msg.append("Online Status: `").append(online ? "Online" : "Offline").append("`").append("\n");
        msg.append("Numerical IP: `").append(json.get("ip")).append("`").append("\n");
        msg.append("Port: `").append(json.get("port")).append("`").append("\n");
        if (online) {
            StringBuilder motd = new StringBuilder();
            JSONObject players = ((JSONObject) json.get("players"));
            ((ArrayList<String>) ((JSONObject) json.get("motd")).get("clean")).forEach(s -> motd.append(s).append("\n"));

            msg.append("Online Players: `").append(players.get("online")).append(" / ").append(players.get("max")).append("`").append("\n");
            msg.append("Supported Versions: `").append(json.get("version")).append("`").append("\n");
            msg.append("\n__**Message Of The Day**__\n").append(motd).append("\n");
        }


        data.reply(EmbedMaker.builder().thumbnail("https://api.mcsrvstat.us/icon/" + ip).title("Looking up server: " + ip).colorHexCode(online ? heart.successColor() : heart.failureColor()).content(msg.toString()).build());
    }
}
