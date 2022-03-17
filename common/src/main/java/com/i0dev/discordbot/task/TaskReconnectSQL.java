package com.i0dev.discordbot.task;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractTask;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class TaskReconnectSQL extends AbstractTask {

    public TaskReconnectSQL(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        setInterval(14);
        setInitialDelay(heart.cnf().getDatabase().getTaskReconnectConnectionTimeoutMilliseconds());
        setTimeUnit(TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    @Override
    public void execute() {
        if (heart.sqlMgr().getConnection() == null) return;
        heart.logSpecial("Reconnecting to SQL database.");
        heart.sqlMgr().connect();
    }
}
