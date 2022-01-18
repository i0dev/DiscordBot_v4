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
