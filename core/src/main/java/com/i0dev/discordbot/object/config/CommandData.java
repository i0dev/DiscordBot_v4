package com.i0dev.discordbot.object.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
@AllArgsConstructor
public class CommandData {

    boolean requirePermission;
    List<Long> allowedOverride = new ArrayList<>();
    List<Long> blockedOverride = new ArrayList<>();

    Map<String, String> messages = null;
    Map<String, Object> options = null;

    public CommandData(boolean requirePermission) {
        this.requirePermission = requirePermission;
    }

    public CommandData(boolean requirePermission, List<Long> allowedOverride) {
        this.requirePermission = requirePermission;
        this.allowedOverride = allowedOverride;
    }

    public CommandData(boolean requirePermission, List<Long> allowedOverride, List<Long> blockedOverride) {
        this.requirePermission = requirePermission;
        this.allowedOverride = allowedOverride;
        this.blockedOverride = blockedOverride;
    }

    public CommandData(boolean requirePermission, List<Long> allowedOverride, List<Long> blockedOverride, Map<String, String> messages) {
        this.requirePermission = requirePermission;
        this.allowedOverride = allowedOverride;
        this.blockedOverride = blockedOverride;
        this.messages = messages;
    }

    public CommandData addMessage(String key, String message) {
        if (messages == null) messages = new HashMap<>();
        messages.put(key, message);
        return this;
    }

    public CommandData addOption(String key, Object value) {
        if (options == null) options = new HashMap<>();
        options.put(key, value);
        return this;
    }

    public <T> CommandData addOptionList(String key, List<T> value) {
        if (options == null) options = new HashMap<>();
        options.put(key, new ArrayList<>(value));
        return this;
    }

    public CommandData setRequirePermission(boolean requirePermission) {
        this.requirePermission = requirePermission;
        return this;
    }

    public CommandData setAllowedOverride(List<Long> allowedOverride) {
        this.allowedOverride = allowedOverride;
        return this;
    }

    public CommandData setBlockedOverride(List<Long> blockedOverride) {
        this.blockedOverride = blockedOverride;
        return this;
    }

    public CommandData setMessages(Map<String, String> messages) {
        this.messages = messages;
        return this;
    }

    public CommandData setOptions(Map<String, Object> options) {
        this.options = options;
        return this;
    }
}
