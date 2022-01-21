package com.i0dev.discordbot.task;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractTask;

import java.util.concurrent.TimeUnit;

public class TaskVerifyAuthentication extends AbstractTask {
    public TaskVerifyAuthentication(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        setInitialDelay(10);
        setInterval(30);
        setTimeUnit(TimeUnit.MINUTES);
    }

    @Override
    public void execute() {
        if (heart.isVerifiedBot()) return;
        heart.shutdownBotNotVerified();
    }
}
