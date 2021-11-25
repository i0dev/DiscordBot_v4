package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.DatabaseInformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@Getter
@ToString
@Setter
public class GeneralConfig extends AbstractConfiguration {

    public GeneralConfig(Heart heart, String path) {
        super(heart, path);
    }

    String botToken = "NzQ4NzQ1NTA4ODMwNDQ1NTk4.X0h5mA.z2cVqID14C9wQvrO1gcQx7yRRYw";
    String activity = "i0dev Bot v4";
    String activityType = "watching";
    String activityStreamingUrl = "https://www.twitch.tv/i0dev";
    boolean administratorBypassPermissions = true;

    DatabaseInformation database = new DatabaseInformation();
    List<Long> verifiedGuilds = Arrays.asList(0L, 0L);

    String successColor = "#27ae5f";
    String failureColor = "#cd3939";
    String normalColor = "#2f3136";

    //Channels
    long logsChannel = 0L;
    long inGamePunishmentsChannel = 0L;
    long changelogChannel = 0L;

}
