package com.i0dev.discordbot.task;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.LogObject;
import com.i0dev.discordbot.object.abs.AbstractTask;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class TaskRunTicketLogQueue extends AbstractTask {
    public TaskRunTicketLogQueue(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        setInterval(10);
        setInitialDelay(10);
        setTimeUnit(TimeUnit.SECONDS);
        toLog = new LinkedList<>();
    }

    @Override
    public void deinitialize() {
        toLog = null;
    }

    @Getter
    List<LogObject> toLog;

    @Override
    public void execute() {
        if (toLog.isEmpty()) return;
        List<LogObject> cache = new LinkedList<>(toLog);
        for (LogObject object : cache) {
            try {
                FileWriter fileWriter = new FileWriter(object.getFile(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);

                printWriter.println(object.getContent());

                bufferedWriter.close();
                printWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        toLog.removeAll(cache);
        cache.clear();
    }
}
