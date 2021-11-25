package com.i0dev.discordbot.object.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Suggestion {
    long messageID, channelID, userID;
    String suggestion, gamemode;

}
