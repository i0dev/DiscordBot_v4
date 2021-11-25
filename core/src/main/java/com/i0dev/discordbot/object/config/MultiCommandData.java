package com.i0dev.discordbot.object.config;

import com.google.gson.JsonElement;
import com.i0dev.discordbot.Heart;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MultiCommandData extends CommandData {
    private List<NamedCommandData> subCommands;

    public MultiCommandData(Heart heart, List<NamedCommandData> subCommands, boolean requirePermission) {
        super(heart, requirePermission);
        this.subCommands = subCommands;
    }

    public MultiCommandData addSubCommand(NamedCommandData subCommand) {
        subCommands.add(subCommand);
        return this;
    }

    public NamedCommandData getCommandDataBySubCommandName(String subCommandName) {
        return subCommands.stream().filter(commandData -> subCommandName.equalsIgnoreCase(commandData.getIdentifier_DontChange())).findFirst().orElse(null);
    }

    public Object getSubCommandConfigOption(String subCommand, String key) {
        NamedCommandData data = subCommands.stream().filter(namedCommandData -> namedCommandData.getIdentifier_DontChange().equalsIgnoreCase(subCommand)).findFirst().orElse(null);
        return data.getOptions().get(key);
    }

    public Object getSubCommandConfigMessage(String subCommand, String key) {
        NamedCommandData data = subCommands.stream().filter(namedCommandData -> namedCommandData.getIdentifier_DontChange().equalsIgnoreCase(subCommand)).findFirst().orElse(null);
        return data.getMessages().get(key);
    }


}
