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
