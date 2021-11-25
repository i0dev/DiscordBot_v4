package com.i0dev.discordbot.object.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.i0dev.discordbot.Heart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@Getter
@ToString
public class CommandData {

    transient Heart heart;

    boolean requirePermission;

    List<JsonElement> allowedOverride = new ArrayList<>();
    List<JsonElement> blockedOverride = new ArrayList<>();

    JsonObject options = null;
    JsonObject messages = null;

    public CommandData(Heart heart, boolean requirePermission) {
        this.heart = heart;
        this.requirePermission = requirePermission;
    }

    public CommandData addMessage(String key, String defaultValue) {
        if (messages == null) messages = new JsonObject();
        getMessages().addProperty(key, defaultValue);
        return this;
    }

    public CommandData addOption(String key, Object value) {
        if (options == null) options = new JsonObject();
        if (value instanceof String)
            getOptions().addProperty(key, ((String) value));
        else if (value instanceof Boolean)
            getOptions().addProperty(key, ((Boolean) value));
        else if (value instanceof Long || value instanceof Integer || value instanceof Double)
            getOptions().addProperty(key, ((Number) value));
        else if (value instanceof Collection)
            getOptions().add(key, heart.listToJsonArr(value));
        else
            getOptions().add(key, ((JsonElement) value));
        return this;
    }

    public CommandData setRequirePermission(boolean requirePermission) {
        this.requirePermission = requirePermission;
        return this;
    }

    public CommandData setAllowedOverride(List<JsonElement> allowedOverride) {
        this.allowedOverride = allowedOverride;
        return this;
    }

    public CommandData setBlockedOverride(List<JsonElement> blockedOverride) {
        this.blockedOverride = blockedOverride;
        return this;
    }
}
