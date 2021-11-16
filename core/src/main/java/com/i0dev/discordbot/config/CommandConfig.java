package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.CommandData;
import com.i0dev.discordbot.object.config.MultiCommandData;
import com.i0dev.discordbot.object.config.NamedCommandData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@ToString
@NoArgsConstructor
@Setter
public class CommandConfig extends AbstractConfiguration {

    public CommandConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    //General
    CommandData help = new CommandData(false);
    CommandData avatar = new CommandData(false);
    CommandData members = new CommandData(false);
    CommandData reload = new CommandData(true);
    CommandData profile = new CommandData(false);
    CommandData roleInfo = new CommandData(false);
    CommandData roles = new CommandData(false);
    CommandData serverLookup = new CommandData(false);
    CommandData botInfo = new CommandData(false);
    CommandData serverInfo = new CommandData(false);

    // Moderation
    CommandData ban = new CommandData(true);
    CommandData kick = new CommandData(true);
    CommandData announce = new CommandData(true);
    CommandData changelog = new CommandData(true);
    CommandData directMessage = new CommandData(true);
    CommandData prune = new CommandData(true);
    CommandData verifyPanel = new CommandData(true)
            .addOption("buttonLabel", "Click to verify")
            .addOption("buttonEmoji", "U+2705")
            .addMessage("content", "To ensure a safe and mutually beneficial experience, all users are required to verify themselves as an actual human. It is your responsibility as a client to read all the rules. Once you agree to them you will be bound by them for as long as you are on this server. When you are done reading them, select the reaction at the Bottom to acknowledge and agree to these terms, which will grant you access to the rest of the server.")
            .addMessage("title", "Secure Verification");

    // Fun
    CommandData coinflip = new CommandData(false);

    // Multi Commands
    MultiCommandData blacklist = new MultiCommandData(Arrays.asList(
            new NamedCommandData("add", true),
            new NamedCommandData("remove", true),
            new NamedCommandData("list", true),
            new NamedCommandData("clear", true)
    ), true);

    MultiCommandData mute = (MultiCommandData) new MultiCommandData(Arrays.asList(
            new NamedCommandData("add", true),
            new NamedCommandData("remove", true),
            new NamedCommandData("list", true),
            new NamedCommandData("clear", true),
            new NamedCommandData("create", true)
    ), true).addOption("muteRole", 0L);

    MultiCommandData invite = new MultiCommandData(Arrays.asList(
            new NamedCommandData("invites", false),
            (NamedCommandData) new NamedCommandData("leaderboard", false).addOption("limit", 30),
            new NamedCommandData("add", true),
            new NamedCommandData("remove", true),
            new NamedCommandData("clear", true)
    ), false);

}