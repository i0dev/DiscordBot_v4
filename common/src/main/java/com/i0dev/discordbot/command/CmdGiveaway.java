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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.storage.GiveawayStorage;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.command.Giveaway;
import com.i0dev.discordbot.util.ConfigUtil;
import com.i0dev.discordbot.util.Utility;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public class CmdGiveaway extends DiscordCommand {
    public CmdGiveaway(Heart heart) {
        super(heart);
    }

    protected void setupCommand() {
        this.setCommand("giveaway");
        this.setRegisterListener(true);
        this.setDescription("The giveaway module.");
        this.addSubcommand((new SubcommandData("create", "Creates a giveaway."))
                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel to post the giveaway in.", true),
                        new OptionData(OptionType.STRING, "prize", "The prize of the giveaway.", true),
                        new OptionData(OptionType.INTEGER, "winners", "The amount of winners.", true),
                        new OptionData(OptionType.STRING, "length", "The length of the giveaway (3w2d5h, 5h10m20s).", true)));
        this.addSubcommand((new SubcommandData("end", "Force ends a giveaway."))
                .addOptions(new OptionData(OptionType.STRING, "message", "The ID of the giveaway message.", true)));
        this.addSubcommand((new SubcommandData("info", "Lists users in the blacklist."))
                .addOptions(new OptionData(OptionType.STRING, "message", "The ID of the giveaway message.", true)));
        this.addSubcommand((new SubcommandData("reroll", "Clears the entire blacklist."))
                .addOptions(new OptionData(OptionType.STRING, "message", "The ID of the giveaway message.", true)));
    }

    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        if ("create".equals(e.getSubcommandName())) create(e, data);
        if ("end".equals(e.getSubcommandName())) end(e, data);
        if ("info".equals(e.getSubcommandName())) info(e, data);
        if ("reroll".equals(e.getSubcommandName())) reroll(e, data);
    }

    public void create(SlashCommandInteractionEvent e, CommandEventData data) {
        GuildMessageChannel channel = (GuildMessageChannel) e.getOption("channel").getAsGuildChannel();
        String prize = e.getOption("prize").getAsString();
        short winners = (short) (e.getOption("winners").getAsLong());
        String length = e.getOption("length").getAsString();
        long endTimeMillis = Utility.deserializeStringToMilliseconds(length) + System.currentTimeMillis();
        String content = this.heart.cnf().getNewGiveawayFormat().replace("{prize}", prize).replace("{winners}", String.valueOf(winners)).replace("{time}", "<t:" + endTimeMillis / 1000L + ":R>");
        Message message = channel.sendMessageEmbeds(this.heart.msgMgr().createMessageEmbed(EmbedMaker.builder().authorImg(this.heart.getJda().getSelfUser().getEffectiveAvatarUrl()).user(e.getUser()).authorName("New Giveaway!").content(content).build()), new MessageEmbed[0]).complete();
        message.addReaction(this.heart.cnf().getGiveawayEmoji()).complete();
        Giveaway giveaway = new Giveaway(message.getIdLong(), channel.getIdLong(), e.getUser().getIdLong(), winners, prize, endTimeMillis, false);
        heart.getConfig(GiveawayStorage.class).getGiveaways().add(giveaway);
        ConfigUtil.save(this.heart.getConfig(GiveawayStorage.class));
        data.replySuccess("Giveaway successfully created!");
    }

    public void end(SlashCommandInteractionEvent e, CommandEventData data) {
        long message = Long.parseLong(e.getOption("message").getAsString());
        Giveaway giveaway = this.getGiveaway(message);
        if (this.getGiveaway(message) == null) {
            data.replyFailure("That message is not a giveaway.");
        } else {
            giveaway.end(this.heart, false);
            data.replySuccess("Giveaway ended!");
        }
    }

    @SneakyThrows
    public void info(SlashCommandInteractionEvent e, CommandEventData data) {
        long message = Long.parseLong(e.getOption("message").getAsString());
        Giveaway giveaway = this.getGiveaway(message);
        if (this.getGiveaway(message) == null) {
            data.replyFailure("That message is not a giveaway.");
        } else {
            User host = this.heart.getJda().getUserById(giveaway.getHostID());
            String content = this.heart.cnf().getGiveawayInfoFormat().replace("{channel}", giveaway.getChannel(this.heart).getAsMention()).replace("{id}", String.valueOf(giveaway.getMessageID())).replace("{prize}", giveaway.getPrize()).replace("{winners}", String.valueOf(giveaway.getWinners())).replace("{suffix}", giveaway.isEnded() ? "ed" : "ing").replace("{ended}", giveaway.isEnded() ? "Yes" : "No").replace("{time}", "<t:" + giveaway.getEndTime() / 1000L + ":R>");
            data.reply(EmbedMaker.builder().authorImg(this.heart.getJda().getSelfUser().getEffectiveAvatarUrl()).authorName("Giveaway Info").content(content).author(host).build());
        }
    }

    public void reroll(SlashCommandInteractionEvent e, CommandEventData data) {
        long message = Long.parseLong(e.getOption("message").getAsString());
        Giveaway giveaway = this.getGiveaway(message);
        if (this.getGiveaway(message) == null) {
            data.replyFailure("That message is not a giveaway.");
        } else {
            giveaway.end(this.heart, true);
            data.replySuccess("Giveaway rerolled!");
        }
    }

    /*
    Utilities
     */

    public Giveaway getGiveaway(long messageId) {
        return heart.getConfig(GiveawayStorage.class).getGiveaways().stream().filter((giveaway) -> giveaway.getMessageID() == messageId).findFirst().orElse(null);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (!heart.cnf().isRequireLinkToJoinGiveaways()) return;
        if (e.getUser().isBot()) return;
        Giveaway giveaway = this.getGiveaway(e.getMessageIdLong());
        if (giveaway == null) return;

        DiscordUser user = heart.genMgr().getDiscordUser(e.getUserIdLong());

        if (!user.isLinked()) {
            e.getReaction().removeReaction(e.getUser()).queue();
            e.getUser().openPrivateChannel().complete().sendMessage("You must link your account to enter giveaways!").queue();
        }

    }
}
