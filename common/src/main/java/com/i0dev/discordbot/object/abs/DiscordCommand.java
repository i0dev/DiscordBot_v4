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

package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public abstract class DiscordCommand extends ListenerAdapter {

    protected boolean registerListener = false;
    protected String permissionOverride = "";
    protected Heart heart;
    // Command Data
    protected String command, description;
    protected List<OptionData> options = new ArrayList<>();
    protected List<SubcommandGroupData> groups = new ArrayList<>();
    protected List<SubcommandData> subCommands = new ArrayList<>();
    protected List<Requirement> requirements = new ArrayList<>();

    public DiscordCommand(Heart heart) {
        this.heart = heart;
        setupCommand();
    }

    protected abstract void setupCommand();

    public abstract void execute(SlashCommandInteractionEvent e, CommandEventData data);

    protected void addOption(OptionData optionData) {
        options.add(optionData);
    }

    protected void addSubcommandGroup(SubcommandGroupData subcommandGroupData) {
        groups.add(subcommandGroupData);
    }

    protected void addSubcommand(SubcommandData subcommandData) {
        subCommands.add(subcommandData);
    }

    protected void addRequirement(Requirement requirement) {
        requirements.add(requirement);
    }

    public CommandData toData() {
        SlashCommandData data = Commands.slash(this.command, this.description);
        if (!options.isEmpty()) data.addOptions(options);
        if (!groups.isEmpty()) data.addSubcommandGroups(groups);
        if (!subCommands.isEmpty()) data.addSubcommands(subCommands);
        data.setDefaultEnabled(true); // WILL CHANGE IN THE FUTURE WHEN IT IS MORE MATURED ON DISCORDS END.
        return data;
    }

    public void upsertPermission(Collection<CommandPrivilege> privileges) {
        heart.getAllowedGuilds().forEach(guild -> guild.updateCommandPrivilegesById(this.command, privileges).queue());
    }

    public void initialize() {

    }

    public void deinitialize() {

    }

}
