package com.i0dev.discordbot.object.config;

import com.google.gson.JsonElement;
import com.i0dev.discordbot.Heart;
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


    public NamedCommandData(Heart heart, String identifier_DontChange, boolean requirePermission) {
        super(heart, requirePermission);
        this.identifier_DontChange = identifier_DontChange;
    }
}
