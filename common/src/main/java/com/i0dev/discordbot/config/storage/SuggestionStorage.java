package com.i0dev.discordbot.config.storage;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.command.Suggestion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Setter
public class SuggestionStorage extends AbstractConfiguration {

    public SuggestionStorage(Heart heart, String path) {
        super(heart, path);
    }

    List<Suggestion> suggestions = new ArrayList<>();

    public Suggestion getSuggestionById(String ID) {
        return suggestions.stream().filter(suggestion -> (suggestion.getMessageID() + "").equalsIgnoreCase(ID)).findFirst().orElse(null);
    }


}
