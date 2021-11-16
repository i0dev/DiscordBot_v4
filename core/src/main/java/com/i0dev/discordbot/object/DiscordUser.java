package com.i0dev.discordbot.object;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.task.TaskExecuteRoleQueue;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

@Data
@NoArgsConstructor
public class DiscordUser {

    public DiscordUser setHeart(Heart heart) {
        this.heart = heart;
        return this;
    }

    transient Heart heart;

    public DiscordUser(long discordID, Heart heart) {
        this.id = discordID;
        this.heart = heart;
    }

    // General Identifiers
    long id;
    long lastUpdatedFromMinecraftAPI = System.currentTimeMillis();

    // Boosting
    long lastBoostTime = 0;
    long totalBoostCount = 0;

    // Invites
    long invitedByID = 0;

    // Statistics
    long ticketsClosed = 0;
    long messagesSent = 0;
    long discordInvites = 0;
    long warnings = 0;

    // Linking Information
    String minecraftIGN = "";
    String minecraftUUID = "";
    boolean linked = false;
    long linkedTime = 0;
    String linkCode = "";

    // Status
    boolean blacklisted = false;
    boolean muted = false;
    boolean banned = false;

    // Times
    long unmuteAtTime = 0;
    long unbanAtTime = 0;

    public String getMinecraftSkinTexture() {
        // The UUID you see is just random, it does not belong to an actual player. It's just to get the Steve skin as a default.
        return "https://crafatar.com/renders/body/" + (minecraftUUID.equals("") ? "5a5ff914-6777-400e-f03a-e9f78fa8bda6" : minecraftUUID) + "?scale=7&default=MHF_Steve&overlay";
    }

    public void save() {
        heart.sqlMgr().updateTable(this, "id", this.id + "");
    }

    public User getAsUser() {
        return heart.getJda().retrieveUserById(this.id).complete();
    }

    public void addRole(long id) {
        addRole(heart.getJda().getRoleById(id));
    }

    public void addRole(Role role) {
        if (role == null) return;
        Guild guild = role.getGuild();
        Member member = guild.getMember(getAsUser());
        if (member == null) return;
        heart.getTask(TaskExecuteRoleQueue.class).add(new RoleQueueObject(id, role.getIdLong(), true));
    }

    public void removeRole(long id) {
        removeRole(heart.getJda().getRoleById(id));
    }

    public void removeRole(Role role) {
        if (role == null) return;
        Guild guild = role.getGuild();
        Member member = guild.getMember(getAsUser());
        if (member == null) return;
        heart.getTask(TaskExecuteRoleQueue.class).add(new RoleQueueObject(id, role.getIdLong(), false));
    }

}
