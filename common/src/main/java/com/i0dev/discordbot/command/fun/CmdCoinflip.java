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

package com.i0dev.discordbot.command.fun;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.util.Utility;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CmdCoinflip extends DiscordCommand {
    public CmdCoinflip(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("coinflip");
        setDescription("Flip a coin!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content("You flipped a coin and you got: `{flip}`".replace("{flip}", Utility.randomNumber(2) == 1 ? "Heads" : "Tails"))
                .colorHexCode(heart.successColor())
                .build());
    }
}
