//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.i0dev.discordbot.config.storage;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.command.Giveaway;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class GiveawayStorage extends AbstractConfiguration {

    public GiveawayStorage(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    List<Giveaway> giveaways = new ArrayList();

}
