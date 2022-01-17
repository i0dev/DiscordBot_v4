package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdPrune extends DiscordCommand {
    public CmdPrune(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("prune");
        setDescription("Delete a certain amount of past messages.");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.INTEGER, "amount", "The amount of messages to prune.", true)
                .setRequiredRange(0, 99));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        long amount = e.getOption("amount").getAsLong();
        e.getChannel().purgeMessages(e.getChannel().getHistory().retrievePast(((int) amount)).complete());
        e.replyEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                .user(e.getUser())
                .content("You pruned {amt} messages in this channel".replace("{amt}", amount + ""))
                .colorHexCode(heart.successColor())
                .build())).setEphemeral(true).queue();

        heart.logDiscord(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .authorImg(e.getUser().getEffectiveAvatarUrl())
                .authorName("Moderation Log")
                .content("{tag} pruned {amt} messages in {channel}".replace("{channel}", e.getChannel().getAsMention()).replace("{amt}", amount + ""))
                .build()
        );
    }
}
