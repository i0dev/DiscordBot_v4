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
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TaskUpdateUsersNickname extends AbstractTask {
    public TaskUpdateUsersNickname(Heart heart) {
        super(heart);
    }


    @Override
    public void initialize() {
        setInitialDelay(1);
        setInterval(30);
        setTimeUnit(TimeUnit.MINUTES);
    }

    @SneakyThrows
    @Override
    public void execute() {
        if (!heart.cnf().isForceNicknameForLinkedUsers()) return;
        ResultSet resultSet = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE linked = 1");
        while (resultSet.next()) {
            User user = heart.getJda().getUserById(resultSet.getString("id"));
            if (user == null) continue;
            updateUserInAllGuilds(user);
        }
    }


    public void updateMember(Member member) {
        if (member == null) return;
        if (member.getUser().isBot()) return;
        DiscordUser discordUser = heart.genMgr().getDiscordUser(member.getUser());

        String ign = discordUser.getMinecraftIGN().equals("") ? member.getUser().getName() : discordUser.getMinecraftIGN();
        String prefix = getPrefix(member);

        String newNickname = heart.cnf().getForceNicknameFormat()
                .replace("{prefix}", prefix)
                .replace("{ign}", ign)
                .replace("{name}", member.getUser().getName());

        if (member.getEffectiveName().equals(newNickname)) return;
        discordUser.modifyNickname(newNickname, member.getGuild());
    }

    public void updateUserInAllGuilds(User user) {
        user.getMutualGuilds().forEach(guild -> updateMember(guild.getMember(user)));
    }


    private String getPrefix(Member member) {
        AtomicReference<String> ret = new AtomicReference<>("");
        AtomicBoolean breakLoop = new AtomicBoolean(false);
        heart.cnf().getRoleToPrefixMap().forEach((roleID, prefix) -> {
            if (breakLoop.get()) return;
            if (member.getRoles().stream().map(Role::getIdLong).anyMatch(roleID::equals)) {
                ret.set(prefix);
                breakLoop.set(true);
            }
        });
        return ret.get();
    }
}
