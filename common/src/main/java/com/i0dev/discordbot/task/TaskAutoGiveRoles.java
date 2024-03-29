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
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractTask;
import net.dv8tion.jda.api.entities.Member;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TaskAutoGiveRoles extends AbstractTask {
    public TaskAutoGiveRoles(Heart heart) {
        super(heart);
    }

    public void initialize() {
        setInterval(1L);
        setInitialDelay(1L);
        setTimeUnit(TimeUnit.MINUTES);
    }

    @Override
    public void execute() {
        heart.cnf().getRolesToAssureEveryoneHas().stream().filter(aLong -> heart.getJda().getRoleById(aLong) != null).map(aLong -> heart.getJda().getRoleById(aLong)).collect(Collectors.toList()).forEach(role -> {
            DiscordUser user = heart.genMgr().getDiscordUser(role.getGuild().getIdLong());
            Member member = role.getGuild().getMember(user.getAsUser());
            if (member == null) return;
            if (member.getRoles().contains(role)) return;
            user.addRole(role);
        });
    }
}
