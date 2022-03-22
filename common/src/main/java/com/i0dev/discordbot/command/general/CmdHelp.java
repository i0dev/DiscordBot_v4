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

package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmdHelp extends DiscordCommand {

    public CmdHelp(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("help");
        addOption(new OptionData(OptionType.INTEGER, "page", "page number", true));
        setDescription("Gets the help page.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        long page = e.getOption("page").getAsLong();
        List<DiscordCommand> basicCommands = heart.getCommands().stream().filter(cmd -> cmd.getSubCommands().isEmpty()).collect(Collectors.toList());
        List<DiscordCommand> multiCommands = heart.getCommands().stream().filter(cmd -> !cmd.getSubCommands().isEmpty()).collect(Collectors.toList());

        List<String> helpRows = new ArrayList<>();
        basicCommands.forEach(command1 -> helpRows.add("`/" + command1.getCommand() + "` - " + command1.getDescription() + "\n"));
        multiCommands.forEach(cmd -> {
            StringBuilder sb = new StringBuilder();
            sb.append("**/").append(cmd.getCommand()).append("**\n");
            cmd.getSubCommands().forEach(subCmd -> sb.append("** ").append(heart.cnf().getHelpPageSymbol()).append(" **`").append(subCmd.getName()).append("` - ").append(subCmd.getDescription()).append("\n"));
            helpRows.add(sb.toString());
        });

        long maxPages = ((long) Math.ceil(helpRows.size() / (double) heart.cnf().getHelpRowsPerPage()) - 1);

        if (page > maxPages) {
            data.replyFailure("Page number is too high. Max page is " + maxPages);
            return;
        }

        int startingRow = page == 1 ? 0 : (int) (page * heart.cnf().getHelpRowsPerPage());
        List<String> currentRows = helpRows.subList(startingRow, (int) Math.min(startingRow + heart.cnf().getHelpRowsPerPage(), helpRows.size()));

        StringBuilder sb = new StringBuilder();
        currentRows.forEach(sb::append);

        data.reply(EmbedMaker.builder()
                .authorName("DiscordBot Help - Page " + page + "/" + maxPages)
                .user(e.getUser())
                .authorImg(heart.getJda().getSelfUser().getEffectiveAvatarUrl())
                .content(sb.toString())
                .build());

    }
}
