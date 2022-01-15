

package com.i0dev.discordbot.object.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.storage.GiveawayStorage;
import com.i0dev.discordbot.manager.MessageManager;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.util.ConfigUtil;
import com.i0dev.discordbot.util.Utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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

    public TextChannel getChannel(Heart heart) {
        return heart.getJda().getTextChannelById(this.channelID);
    }

    public void end(Heart heart, boolean rerolled) {

        Message message = this.getChannel(heart).retrieveMessageById(this.messageID).complete();
        User host = heart.getJda().getUserById(this.hostID);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.copyFrom(message.getEmbeds().get(0));
        embedBuilder.setTitle("Giveaway ended!");
        message.editMessageEmbeds(new MessageEmbed[]{embedBuilder.build()}).queue();
        List<User> selectionPool = (message.getReactions().get(0)).retrieveUsers().stream().collect(Collectors.toList());
        selectionPool.removeIf(User::isBot);
        List<User> winners = new ArrayList();

        for (int i = 0; i < this.winners; ++i) {
            winners.add(selectionPool.get(ThreadLocalRandom.current().nextInt(selectionPool.size())));
        }

        this.getChannel(heart).sendMessageEmbeds(this.getGiveawayEndMessage(heart, rerolled, winners), new MessageEmbed[0]).queue();
        String winnersContent = heart.gCnf().getWonGiveawayFormat().replace("{prize}", this.prize).replace("{tag}", host.getAsTag()).replace("{time}", "<t:" + this.endTime / 1000L + ":R>");
        winners.forEach((user) -> (user.openPrivateChannel().complete()).sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder().authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl()).authorName("You won a giveaway!").authorURL("https://discordapp.com/channels/" + this.getChannel(heart).getGuild().getId() + "/" + this.channelID + "/" + this.messageID).content(winnersContent).colorHexCode(heart.successColor()).build()), new MessageEmbed[0]).queue());
        this.ended = true;
        ConfigUtil.save((AbstractConfiguration) heart.getConfig(GiveawayStorage.class));
    }

    private MessageEmbed getGiveawayEndMessage(Heart heart, boolean rerolled, List<User> winners) {
        User host = heart.getJda().getUserById(this.hostID);
        String content = "No one entered the giveaway!";
        if (winners.size() != 0) {
            content = heart.gCnf().getEndGiveawayFormat().replace("{prize}", this.prize).replace("{s}", winners.size() == 1 ? "" : "s").replace("{winners}", this.formatWinners(winners)).replace("{time}", "<t:" + this.endTime / 1000L + ":R>");
        }

        return ((MessageManager) heart.getManager(MessageManager.class)).createMessageEmbed(EmbedMaker.builder().authorName(rerolled ? "Giveaway rerolled!" : "Giveaway ended!").authorURL("https://discordapp.com/channels/" + this.getChannel(heart).getGuild().getId() + "/" + this.channelID + "/" + this.messageID).authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl()).user(host).content(content).build());
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
