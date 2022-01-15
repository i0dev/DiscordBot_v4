package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.DatabaseInformation;
import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@Setter
public class GeneralConfig extends AbstractConfiguration {

    public GeneralConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    String botToken = "NzQ4NzQ1NTA4ODMwNDQ1NTk4.X0h5mA.z2cVqID14C9wQvrO1gcQx7yRRYw";
    String activity = "i0dev Bot v4";
    String activityType = "watching";
    String activityStreamingUrl = "https://www.twitch.tv/i0dev";
    boolean administratorBypassPermissions = true;

    DatabaseInformation database = new DatabaseInformation();
    List<Long> verifiedGuilds = Collections.singletonList(879086812776239165L);

    String successColor = "#27ae5f";
    String failureColor = "#cd3939";
    String normalColor = "#2f3136";

    //Channels
    long logsChannel = 0L;
    long inGamePunishmentsChannel = 0L;
    long changelogChannel = 0L;
    long suggestionPendingChannel = 0L;
    long suggestionAcceptedChannel = 0L;
    long suggestionDeniedChannel = 0L;

    // Random

    List<Long> verifyRolesToGive = Collections.singletonList(0L);
    List<Long> verifyRolesToRemove = Collections.singletonList(0L);
    String verifyPanelButtonLabel = "Verify";
    String verifyPanelButtonEmoji = "U+2705";
    String verifyPanelDescription = "To ensure a safe and mutually beneficial experience, all users are required to verify themselves as an actual human. It is your responsibility as a client to read all the rules. Once you agree to them you will be bound by them for as long as you are on this server. When you are done reading them, select the reaction at the Bottom to acknowledge and agree to these terms, which will grant you access to the rest of the server.";
    String verifyPanelTitle = "User Verification";

    long inviteLeaderboardMaxDisplay = 30L;

    String suggestionUpvoteEmoji = "U+1F44D";
    String suggestionDownvoteEmoji = "U+1F44E";

    String newGiveawayFormat = "Prize: `{prize}`\nHost: `{tag}`\nWinners: `{winners}`\nEnding: {time}\n\nReact with the emoji below to enter the giveaway!";
    String giveawayEmoji = "U+1F389";
    String endGiveawayFormat = "Prize: `{prize}`\nHost: `{tag}`\nWinner{s}: {winners}\nEnded: {time}";
    String wonGiveawayFormat = "You won `{prize}`!\nHost: `{tag}`\nEnded: {time}";
    String giveawayInfoFormat = "Channel: {channel}\nIdentifier: `{id}`\nPrize: `{prize}`\nHost: `{tag}`\nWinners: {winners}\nEnded: `{ended}`\nTime End{suffix}: {time}";

}
