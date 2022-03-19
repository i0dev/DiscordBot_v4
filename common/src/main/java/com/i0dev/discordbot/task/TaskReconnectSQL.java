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
        setInterval(heart.cnf().getDatabase().getTaskReconnectConnectionTimeoutMilliseconds());
        setInitialDelay(heart.cnf().getDatabase().getTaskReconnectConnectionTimeoutMilliseconds());
        setTimeUnit(TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    @Override
    public void execute() {
        if (heart.sqlMgr().getConnection() == null) return;
        if (heart.sqlMgr().getConnection().isClosed()) heart.sqlMgr().getConnection().close();
        heart.sqlMgr().connect(false);
    }
}
