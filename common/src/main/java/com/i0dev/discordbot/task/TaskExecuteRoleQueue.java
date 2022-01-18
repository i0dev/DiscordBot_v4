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
import com.i0dev.discordbot.object.RoleQueueObject;
import com.i0dev.discordbot.object.abs.AbstractTask;
import com.i0dev.discordbot.util.ConsoleColors;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class TaskExecuteRoleQueue extends AbstractTask {
    public TaskExecuteRoleQueue(Heart heart) {
        super(heart);
    }

    @Override
    public void initialize() {
        roleQueueList = new ArrayList<>();
        setInterval(2);
        setInitialDelay(5);
        setTimeUnit(TimeUnit.SECONDS);
    }

    @Override
    public void deinitialize() {
        roleQueueList.clear();
        roleQueueList = null;
    }

    private ArrayList<RoleQueueObject> roleQueueList;

    public void add(RoleQueueObject object) {
        roleQueueList.add(object);
    }

    @Override
    public void execute() {
        if (roleQueueList.isEmpty()) return;
        RoleQueueObject queueObject = roleQueueList.get(0);
        roleQueueList.remove(queueObject);
        User user = heart.getJda().getUserById(queueObject.getUserID());
        Role role = heart.getJda().getRoleById(queueObject.getRoleID());
        if (user == null || role == null) execute();
        Guild guild = role.getGuild();
        Member member = guild.getMemberById(user.getId());
        if (member == null) execute();
        if (queueObject.isAdd()) {
            if (member.getRoles().contains(role)) execute();
            guild.addRoleToMember(user.getId(), role).queue();
            heart.logDebug("Applied the role " + ConsoleColors.PURPLE + role.getName() + ConsoleColors.WHITE + " to the user: " + ConsoleColors.PURPLE + member.getEffectiveName());
        } else {
            if (!member.getRoles().contains(role)) execute();
            guild.removeRoleFromMember(user.getId(), role).queue();
            heart.logDebug("Removed the role " + ConsoleColors.PURPLE + role.getName() + ConsoleColors.WHITE + " from the user: " + ConsoleColors.PURPLE + member.getEffectiveName());
        }
    }
}
