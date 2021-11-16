package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.DatabaseInformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@Setter
public class MiscConfig extends AbstractConfiguration {

    public MiscConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }


     List<Long> verify_rolesToGive = Collections.singletonList(0L);
     List<Long> verify_rolesToRemove = Collections.singletonList(0L);

}
