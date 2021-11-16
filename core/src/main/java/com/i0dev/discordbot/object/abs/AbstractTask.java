package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@ToString
public abstract class AbstractTask extends AbstractManager implements Runnable {
    public AbstractTask(Heart heart) {
        super(heart);
    }

    long interval = 10000;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    long initialDelay = 3000;

    @Override
    public void run() {
        execute();
    }

    public abstract void execute();

}
