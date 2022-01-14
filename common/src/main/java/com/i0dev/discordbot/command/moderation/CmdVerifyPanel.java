package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.Button;

public class CmdVerifyPanel extends DiscordCommand {
    public CmdVerifyPanel(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setRegisterListener(true);
        setCommand("verify_panel");
        setDescription("Sends the verification panel.");
        addRequirement(Requirement.IN_GUILD);
        addOption(new OptionData(OptionType.BOOLEAN, "pin", "Toggle to pin the verify panel, default is false.", false));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        boolean pin = e.getOption("pin") != null && e.getOption("pin").getAsBoolean();
        Message message = e.getTextChannel().sendMessageEmbeds(heart.msgMgr().createMessageEmbed(EmbedMaker.builder()
                        .user(e.getUser())
                        .title(heart.gCnf().getVerifyPanelTitle())
                        .content(heart.gCnf().getVerifyPanelDescription())
                        .colorHexCode(heart.normalColor())
                        .build()))
                .setActionRow(Button.success("BUTTON_VERIFY_PANEL", heart.gCnf().getVerifyPanelButtonLabel()).withEmoji(Emoji.fromMarkdown(heart.gCnf().getVerifyPanelButtonEmoji())))
                .complete();
        if (pin) message.pin();
        e.reply("Verify panel sent.").setEphemeral(true).queue();
    }

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getButton() == null) return;
        if (!"BUTTON_VERIFY_PANEL".equalsIgnoreCase(e.getButton().getId())) return;
        if (e.getUser().isBot()) return;
        if (!heart.genMgr().isAllowedGuild(e.getGuild())) return;
        DiscordUser user = heart.genMgr().getDiscordUser(e.getUser());
        if (user.isBlacklisted()) return;
        heart.gCnf().getVerifyRolesToGive().forEach(user::addRole);
        heart.gCnf().getVerifyRolesToRemove().forEach(user::removeRole);
        e.getInteraction().deferReply(true).setContent("You have successfully verified yourself!").queue();
    }
}
