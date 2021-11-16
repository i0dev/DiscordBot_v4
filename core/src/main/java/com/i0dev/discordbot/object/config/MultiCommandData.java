package com.i0dev.discordbot.object.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MultiCommandData extends CommandData {
    private List<NamedCommandData> subCommands;

    public MultiCommandData(List<NamedCommandData> subCommands, boolean requirePermission) {
        super(requirePermission);
        this.subCommands = subCommands;
        this.requirePermission = requirePermission;
    }

    public MultiCommandData(List<NamedCommandData> subCommands, boolean requirePermission, List<Long> allowedOverride) {
        super(requirePermission, allowedOverride);
        this.subCommands = subCommands;
        this.requirePermission = requirePermission;
        this.allowedOverride = allowedOverride;
    }

    public MultiCommandData(List<NamedCommandData> subCommands, boolean requirePermission, List<Long> allowedOverride, List<Long> blockedOverride) {
        super(requirePermission, allowedOverride, blockedOverride);
        this.subCommands = subCommands;
        this.requirePermission = requirePermission;
        this.allowedOverride = allowedOverride;
        this.blockedOverride = blockedOverride;
    }

    public MultiCommandData(List<NamedCommandData> subCommands, boolean requirePermission, List<Long> allowedOverride, List<Long> blockedOverride, Map<String, String> messages) {
        super(requirePermission, allowedOverride, blockedOverride, messages);
        this.subCommands = subCommands;
        this.requirePermission = requirePermission;
        this.allowedOverride = allowedOverride;
        this.blockedOverride = blockedOverride;
        this.messages = messages;
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
