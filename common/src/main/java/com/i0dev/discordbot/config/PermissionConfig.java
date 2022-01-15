package com.i0dev.discordbot.config;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.PermissionNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class PermissionConfig extends AbstractConfiguration {
    public PermissionConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;    }

    List<PermissionNode> permissions = Arrays.asList(
            new PermissionNode(
                    "avatar",
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            )
    );

}
