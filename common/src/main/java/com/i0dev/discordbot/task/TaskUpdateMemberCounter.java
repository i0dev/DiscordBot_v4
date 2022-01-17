package com.i0dev.discordbot.task;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractTask;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.util.concurrent.TimeUnit;

public class TaskUpdateMemberCounter extends AbstractTask {
    public TaskUpdateMemberCounter(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        setInitialDelay(1);
        setInterval(1);
        setTimeUnit(TimeUnit.MINUTES);
    }

    @Override
    public void execute() {
        if (!heart.cnf().isMemberCounterEnabled()) return;
        GuildChannel channel = heart.getJda().getGuildChannelById(heart.cnf().getMemberCounterChannel());
        if (channel == null) return;
        channel.getManager().setName(heart.msgMgr().replacePlaceholders(heart.cnf().getMemberCounterFormat())).queue();
    }
}
