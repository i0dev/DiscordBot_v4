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
import com.i0dev.discordbot.object.NicknameQueueObject;
import com.i0dev.discordbot.object.RoleQueueObject;
import com.i0dev.discordbot.object.abs.AbstractTask;
import com.i0dev.discordbot.util.ConsoleColors;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class TaskExecuteNicknameQueue extends AbstractTask {
    public TaskExecuteNicknameQueue(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        nicknameQueueList = new ArrayList<>();
        setInterval(2);
        setInitialDelay(10);
        setTimeUnit(TimeUnit.SECONDS);
    }

    @Override
    public void deinitialize() {
        nicknameQueueList.clear();
        nicknameQueueList = null;
    }

    private ArrayList<NicknameQueueObject> nicknameQueueList;

    public void add(NicknameQueueObject object) {
        nicknameQueueList.add(object);
    }

    @SneakyThrows
    @Override
    public void execute() {
        if (nicknameQueueList.isEmpty()) return;
        NicknameQueueObject queueObject = nicknameQueueList.get(0);
        nicknameQueueList.remove(queueObject);

        Guild guild = heart.getJda().getGuildById(queueObject.getGuildID());
        if (guild == null) return;
        Member member = guild.getMemberById(queueObject.getUserID());
        if (member == null) return;

        if (member.getEffectiveName().equals(queueObject.getNickname())) return;

        member.modifyNickname(queueObject.getNickname()).submit().thenAccept(a -> {
            if (!member.getEffectiveName().equalsIgnoreCase(queueObject.getNickname()))
                add(queueObject);
            else
                heart.logDebug("Changed nickname of " + ConsoleColors.PURPLE + member.getUser().getAsTag() + ConsoleColors.WHITE + " to " + ConsoleColors.PURPLE + queueObject.getNickname() + ConsoleColors.WHITE + " in " + ConsoleColors.PURPLE + guild.getName() + ConsoleColors.WHITE + ".");
        });

    }
}
