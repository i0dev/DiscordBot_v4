/*
 * MIT License
 *
 * Copyright (c) i0dev
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
