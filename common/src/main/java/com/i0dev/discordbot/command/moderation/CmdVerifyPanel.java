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

package com.i0dev.discordbot.command.moderation;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
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
                        .title(heart.cnf().getVerifyPanelTitle())
                        .content(heart.cnf().getVerifyPanelDescription())
                        .colorHexCode(heart.normalColor())
                        .build()))
                .setActionRow(Button.success("BUTTON_VERIFY_PANEL", heart.cnf().getVerifyPanelButtonLabel()).withEmoji(Emoji.fromMarkdown(heart.cnf().getVerifyPanelButtonEmoji())))
                .complete();
        if (pin) message.pin().queue();
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

        if (heart.cnf().isRequireLinkToVerify() && !user.isLinked()) {
            e.reply(heart.cnf().getNotLinkedTryVerify()).setEphemeral(true).queue();
            return;
        }
        heart.genMgr().verifyMember(e.getMember());
        e.getInteraction().deferReply(true).setContent("You have successfully verified yourself!").queue();
    }


}
