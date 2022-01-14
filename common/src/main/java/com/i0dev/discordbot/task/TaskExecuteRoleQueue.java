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
