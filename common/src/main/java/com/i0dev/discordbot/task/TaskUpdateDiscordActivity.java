package com.i0dev.discordbot.task;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractTask;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.TimeUnit;


public class TaskUpdateDiscordActivity extends AbstractTask {
    public TaskUpdateDiscordActivity(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        setInterval(30);
        setInitialDelay(2);
        setTimeUnit(TimeUnit.SECONDS);
    }

    @Override
    public void execute() {
        String activity = heart.msgMgr().replacePlaceholders(heart.cnf().getActivity());
        switch (heart.cnf().getActivityType().toLowerCase()) {
            case "watching":
                heart.getJda().getPresence().setActivity(Activity.watching(activity));
                break;
            case "listening":
                heart.getJda().getPresence().setActivity(Activity.listening(activity));
                break;
            case "playing":
                heart.getJda().getPresence().setActivity(Activity.playing(activity));
                break;
        }
    }
}
