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

package com.i0dev.discordbot.object.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.storage.GiveawayStorage;
import com.i0dev.discordbot.manager.MessageManager;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.util.ConfigUtil;
import com.i0dev.discordbot.util.Utility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Giveaway {
    long messageID;
    long channelID;
    long hostID;
    short winners;
    String prize;
    long endTime;
    boolean ended;

    public boolean isNotEnded() {
        return !this.ended;
    }

    public GuildMessageChannel getChannel(Heart heart) {
        return (GuildMessageChannel) heart.getJda().getGuildChannelById(channelID);
    }

    public void end(Heart heart, boolean rerolled) {

        Message message = this.getChannel(heart).retrieveMessageById(this.messageID).complete();
        User host = heart.getJda().getUserById(this.hostID);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.copyFrom(message.getEmbeds().get(0));
        embedBuilder.setAuthor("Giveaway ended!", message.getEmbeds().get(0).getAuthor().getUrl(), message.getEmbeds().get(0).getAuthor().getIconUrl());
        message.editMessageEmbeds(new MessageEmbed[]{embedBuilder.build()}).queue();
        List<User> selectionPool = (message.getReactions().get(0)).retrieveUsers().stream().collect(Collectors.toList());
        if (heart.cnf().isRequireLinkToJoinGiveaways())
            selectionPool.removeIf(user -> {
                DiscordUser discordUser = heart.genMgr().getDiscordUser(user);
                return !discordUser.isLinked();
            });
        selectionPool.removeIf(User::isBot);
        List<User> selectedWinners = new ArrayList();
        int maxTries = 50;
        for (int i = 0, tries = 0; i < Math.min(winners, selectionPool.size()); i++) {
            User winner = selectionPool.get(ThreadLocalRandom.current().nextInt(selectionPool.size()));
            if (selectedWinners.contains(winner)) {
                tries++;
                i--;
                if (tries > maxTries) break;
                continue;
            }
            selectedWinners.add(winner);

        }
        this.getChannel(heart).sendMessageEmbeds(this.getGiveawayEndMessage(heart, rerolled, selectedWinners, selectionPool.size()), new MessageEmbed[0]).queue();
        String winnersContent = heart.cnf().getWonGiveawayFormat().replace("{prize}", this.prize).replace("{tag}", host.getAsTag()).replace("{time}", "<t:" + this.endTime / 1000L + ":R>");
        selectedWinners.forEach((user) -> (user.openPrivateChannel().complete()).sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder().authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl()).authorName("You won a giveaway!").authorURL("https://discordapp.com/channels/" + this.getChannel(heart).getGuild().getId() + "/" + this.channelID + "/" + this.messageID).content(winnersContent).colorHexCode(heart.successColor()).build()), new MessageEmbed[0]).queue());
        this.ended = true;
        ConfigUtil.save(heart.getConfig(GiveawayStorage.class));
    }

    private MessageEmbed getGiveawayEndMessage(Heart heart, boolean rerolled, List<User> winners, long entries) {
        User host = heart.getJda().getUserById(this.hostID);
        String content = "No one entered the giveaway!";
        if (winners.size() != 0) {
            content = heart.cnf().getEndGiveawayFormat()
                    .replace("{prize}", this.prize)
                    .replace("{s}", winners.size() == 1 ? "" : "s")
                    .replace("{entries}", String.valueOf(entries))
                    .replace("{winners}", this.formatWinners(winners))
                    .replace("{time}", "<t:" + this.endTime / 1000L + ":R>");
        }

        return heart.getManager(MessageManager.class).createMessageEmbed(EmbedMaker.builder().authorName(rerolled ? "Giveaway rerolled!" : "Giveaway ended!").authorURL("https://discordapp.com/channels/" + this.getChannel(heart).getGuild().getId() + "/" + this.channelID + "/" + this.messageID).authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl()).user(host).content(content).build());
    }

    private String formatWinners(List<User> list) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> Stripped = new ArrayList();
        Iterator var4 = list.iterator();

        while (var4.hasNext()) {
            User s = (User) var4.next();
            Stripped.add(Utility.capitalizeFirst(s.getAsMention()) + "`(" + s.getAsTag() + ")`");
        }

        for (int i = 0; i < Stripped.size(); ++i) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
