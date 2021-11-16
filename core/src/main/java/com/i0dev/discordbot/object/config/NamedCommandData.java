package com.i0dev.discordbot.object.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class NamedCommandData extends CommandData {

    String identifier_DontChange;

    public NamedCommandData(String identifier_DontChange, boolean requirePermission, List<Long> allowedOverride, List<Long> blockedOverride, Map<String, String> messages, Map<String, Object> options) {
        super(requirePermission, allowedOverride, blockedOverride, messages, options);
        this.identifier_DontChange = identifier_DontChange;
    }

    public NamedCommandData(String identifier_DontChange, boolean requirePermission) {
        super(requirePermission);
        this.identifier_DontChange = identifier_DontChange;
    }

    public NamedCommandData(String identifier_DontChange, boolean requirePermission, List<Long> allowedOverride) {
        super(requirePermission, allowedOverride);
        this.identifier_DontChange = identifier_DontChange;
    }

    public NamedCommandData(String identifier_DontChange, boolean requirePermission, List<Long> allowedOverride, List<Long> blockedOverride) {
        super(requirePermission, allowedOverride, blockedOverride);
        this.identifier_DontChange = identifier_DontChange;
    }

    public NamedCommandData(String identifier_DontChange, boolean requirePermission, List<Long> allowedOverride, List<Long> blockedOverride, Map<String, String> messages) {
        super(requirePermission, allowedOverride, blockedOverride, messages);
        this.identifier_DontChange = identifier_DontChange;
    }
}
