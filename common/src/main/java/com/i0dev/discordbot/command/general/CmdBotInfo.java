package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CmdBotInfo extends DiscordCommand {

    public CmdBotInfo(Heart heart) {
        super(heart);
    }

    private EmbedMaker message;

    @Override
    public void initialize() {
        String msg = "Bot Author: " + "`{botAuthor}`" + "\n" +
                "Bot Version: `" + "{version}" + "`\n" +
                "Plugin Mode: `" + "{pluginMode}" + "`\n";

        message = EmbedMaker.builder()
                .authorName("DiscordBot Information")
                .authorURL("https://i0dev.com/")
                .authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl())
                .footer("Bot created by i0dev.com")
                .footerImg("https://cdn.discordapp.com/attachments/687663938443542552/908087180306575432/2fd97023f121975bc18c967b3bf5418f.png")
                .content(msg)
                .build();
    }

    @Override
    protected void setupCommand() {
        setCommand("bot_info");
        setDescription("Get the bots information");
        setRegisterListener(true);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMessage().getContentRaw().startsWith("<@!" + heart.getJda().getSelfUser().getId() + ">"))
            e.getMessage().replyEmbeds(heart.msgMgr().createMessageEmbed(message)).mentionRepliedUser(false).queue();
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        data.reply(message);
    }

}
