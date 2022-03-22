/*
 * MIT License
 *
 * Copyright (c) i0dev
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.DatabaseInformation;
import lombok.*;

import java.util.*;

@Getter
@ToString
@Setter
@NoArgsConstructor
public class GeneralConfig extends AbstractConfiguration {

    public GeneralConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    String botToken = "Your Token Here";
    String activity = "i0dev Bot v4";
    String activityType = "watching";
    String botStatus = "online";
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
    long welcomeMessageChannel = 0L;
    long memberCounterChannel = 0L;

    long factionsConfirmedChannel = 0L;
    long skyblockConfirmedChannel = 0L;
    long prisonsConfirmedChannel = 0L;

    // Roles

    long factionsLeaderRole = 0L;
    long skyblockLeaderRole = 0L;
    long prisonLeaderRole = 0L;


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
    boolean suggsetionRequireGamemode = true;
    Map<String, String> suggestionGamemodesColorMap = Collections.singletonMap("Factions", "#ff6666");

    String newGiveawayFormat = "Prize: `{prize}`\nHost: `{tag}`\nWinners: `{winners}`\nEnding: {time}\n\nReact with the emoji below to enter the giveaway!";
    String giveawayEmoji = "U+1F389";
    String endGiveawayFormat = "Prize: `{prize}`\nHost: `{tag}`\nEntries: `{entries}`\nWinner{s}: {winners}\nEnded: {time}";
    String wonGiveawayFormat = "You won `{prize}`!\nHost: `{tag}`\nEnded: {time}";
    String giveawayInfoFormat = "Channel: {channel}\nIdentifier: `{id}`\nPrize: `{prize}`\nHost: `{tag}`\nWinners: {winners}\nEnded: `{ended}`\nTime End{suffix}: {time}";

    boolean welcomeMessagesEnabled = true;
    boolean welcomePingUser = true;
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
    boolean verifyUserOnLink = true;
    boolean requireLinkToJoinGiveaways = true;

    boolean forceNicknameForLinkedUsers = true;
    Map<Long, String> roleToPrefixMap = Collections.singletonMap(766183472749871114L, "[Manager] ");
    String forceNicknameFormat = "{prefix}{ign}";

    boolean memberCounterEnabled = true;
    String memberCounterFormat = "Member Count: `{guildMemberCount}`";

    long helpRowsPerPage = 10L;

    String factionsConfirmFormat = "Faction: `{faction}`\nLeader: `{tag}`\nMembers: `{size}`";
    String factionsConfirmTitle = "Faction Confirmed";
    String skyblockConfirmFormat = "Island: `{team}`\nLeader: `{tag}`\nMembers: `{size}`";
    String skyblockConfirmTitle = "Island Confirmed";
    String prisonConfirmFormat = "Gang: `{team}`\nLeader: `{tag}`\nMembers: `{size}`";
    String prisonConfirmTitle = "Gang confirmed";

    String cmdIpContent = "Server ip: `play.MCRivals.com`";
    String helpPageSymbol = "•";
}
