package com.i0dev.discordbot.object.abs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.Requirement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public abstract class DiscordCommand extends ListenerAdapter {

    protected boolean registerListener = false;
    protected Heart heart;
    protected com.i0dev.discordbot.object.config.CommandData configSection;
    // Command Data
    protected String command, description;
    protected List<OptionData> options = new ArrayList<>();
    protected List<SubcommandGroupData> groups = new ArrayList<>();
    protected List<SubcommandData> subCommands = new ArrayList<>();
    protected List<Requirement> requirements = new ArrayList<>();

    public DiscordCommand(Heart heart, com.i0dev.discordbot.object.config.CommandData configSection) {
        this.heart = heart;
        this.configSection = configSection;
        setupCommand();
    }

    protected abstract void setupCommand();

    public abstract void execute(SlashCommandEvent e, CommandEventData data);

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
        CommandData data = new CommandData(this.command, this.description);
        if (!options.isEmpty()) data.addOptions(options);
        if (!groups.isEmpty()) data.addSubcommandGroups(groups);
        if (!subCommands.isEmpty()) data.addSubcommands(subCommands);
        data.setDefaultEnabled(true); // WILL CHANGE IN THE FUTURE WHEN IT IS MORE MATURED ON DISCORDS END.
        return data;
    }

    public void upsertPermission(Collection<CommandPrivilege> privileges) {
        heart.getAllowedGuilds().forEach(guild -> guild.updateCommandPrivilegesById(this.command, privileges).queue());
    }

    protected JsonElement getConfigOption(String key) {
        return getConfigSection().getOptions().get(key);
    }

    protected JsonArray getConfigOptionJsonArray(String key) {
        return getConfigSection().getOptions().get(key).getAsJsonArray();
    }

    protected String getConfigMessage(String key) {
        return getConfigSection().getMessages().get(key).getAsString();
    }

    public void initialize() {

    }

    public void deinitialize() {

    }

}
