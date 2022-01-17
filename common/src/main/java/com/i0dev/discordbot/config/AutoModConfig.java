package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.PermissionNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class AutoModConfig extends AbstractConfiguration {
    public AutoModConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    boolean logEverything = true;
    long autoModLogChannelId = 0;

    boolean adminsBypassAutoMod = true;

    List<Long> channelsToDeleteSentMessageIn = new ArrayList<>();

    List<String> deleteMessageIfContains = Arrays.asList(
            "nicecar",
            "discord.gg"
    );

    List<Long> autoModEffectedChannels = new ArrayList<>();
    boolean effectedChannelsWhitelistMode = false;


}
