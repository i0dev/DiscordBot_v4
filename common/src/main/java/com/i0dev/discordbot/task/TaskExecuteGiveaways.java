//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.i0dev.discordbot.task;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.storage.GiveawayStorage;
import com.i0dev.discordbot.object.abs.AbstractTask;
import com.i0dev.discordbot.object.command.Giveaway;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TaskExecuteGiveaways extends AbstractTask {
    public TaskExecuteGiveaways(Heart heart) {
        super(heart);
    }

    public void initialize() {
        this.setInterval(1L);
        this.setInitialDelay(1L);
        this.setTimeUnit(TimeUnit.MINUTES);
    }

    public void execute() {
        heart.getConfig(GiveawayStorage.class).getGiveaways().stream().filter(Giveaway::isNotEnded).collect(Collectors.toList()).forEach((giveaway) -> {
            if (giveaway.getEndTime() < System.currentTimeMillis()) giveaway.end(this.heart, false);
        });
    }
}
