package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.DatabaseInformation;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@Setter
@NoArgsConstructor
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
    long joinLeaveLogsChannel = 0L;

    // Random

    boolean requireLinkToVerify = true;
    String notLinkedTryVerify = "You need to link your account in order to verify!\nYou can do this by typing `/link generate` on our server on any realm.\nServer IP Address: `play.MCRivals.com`";
    List<Long> verifyRolesToGive = Collections.singletonList(0L);
    List<Long> verifyRolesToRemove = Collections.singletonList(0L);
    String verifyPanelButtonLabel = "Verify";
    String verifyPanelButtonEmoji = "U+2705";
    String verifyPanelDescription = "To ensure a safe and mutually beneficial experience, all users are required to verify themselves as an actual human. It is your responsibility as a client to read all the rules. Once you agree to them you will be bound by them for as long as you are on this server. When you are done reading them, select the reaction at the Bottom to acknowledge and agree to these terms, which will grant you access to the rest of the server.";
    String verifyPanelTitle = "User Verification";

    long inviteLeaderboardMaxDisplay = 30L;
    List<Long> rolesToAssureEveryoneHas = new ArrayList<>();

    String suggestionUpvoteEmoji = "U+1F44D";
    String suggestionDownvoteEmoji = "U+1F44E";

    String newGiveawayFormat = "Prize: `{prize}`\nHost: `{tag}`\nWinners: `{winners}`\nEnding: {time}\n\nReact with the emoji below to enter the giveaway!";
    String giveawayEmoji = "U+1F389";
    String endGiveawayFormat = "Prize: `{prize}`\nHost: `{tag}`\nEntries: `{entries}`\nWinner{s}: {winners}\nEnded: {time}";
    String wonGiveawayFormat = "You won `{prize}`!\nHost: `{tag}`\nEnded: {time}";
    String giveawayInfoFormat = "Channel: {channel}\nIdentifier: `{id}`\nPrize: `{prize}`\nHost: `{tag}`\nWinners: {winners}\nEnded: `{ended}`\nTime End{suffix}: {time}";

    boolean welcomeMessagesEnabled = true;
    boolean welcomePingUser = true;
    long welcomeMessageChannel = 0L;
    boolean welcomeUserMemberAvatarAsThumbnail = true;
    String welcomeEmbedImageUrl = "";
    String welcomeEmbedTitle = "Welcome {tag} to {guildName}!";
    String welcomeEmbedContent = "**For support go to:** <#766322425323192330>\n**Server IP:** `play.mcrivals.com`\n**Website:** [shop.mcrivals.com](https://shop.mcrivals.com)\n**MemberCount:** `{guildMemberCount}`";
    List<Long> welcomeRolesToGive = new ArrayList<>();

    boolean joinLogsEnabled = true;
    boolean leaveLogsEnabled = true;
    String joinLogsFormat = "**{tag}** has joined the server, invited by `{inviter}`";
    String leaveLogsFormat = "**{tag}** has left the server, invited by `{inviter}`";

    String linkInfoFormat = "Minecraft IGN: `{ign}`\nMinecraft UUID: `{uuid}`\nTime Linked: {linkTime}\nWas Forced: `{forced}`";
    String linkInfoCheckIgnFormat = "Minecraft IGN: `{ign}`\nMinecraft UUID: `{uuid}`\nTime Linked: {linkTime}\nWas Forced: `{forced}`\nTag: `{tag}`\nID: `{id}`";
    long linkCodeLength = 6L;

    boolean memberCounterEnabled = true;
    long memberCounterChannel = 0L;
    String memberCounterFormat = "Member Count: `{guildMemberCount}`";

}
