package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InviteManager extends AbstractManager {
    public InviteManager(Heart heart) {
        super(heart);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (!heart.genMgr().isAllowedGuild(e.getGuild())) return;
        e.getGuild().retrieveInvites().queue(retrievedInvites -> {
            for (final Invite retrievedInvite : retrievedInvites) {
                final String code = retrievedInvite.getCode();
                final InviteData cachedInvite = inviteCache.get(code);
                if (cachedInvite == null || retrievedInvite.getUses() == cachedInvite.getUses() || retrievedInvite.getInviter() == null)
                    continue;
                cachedInvite.incrementUses();

                DiscordUser inviter = heart.genMgr().getDiscordUser(retrievedInvite.getInviter());
                inviter.setDiscordInvites(inviter.getDiscordInvites() + 1);

                DiscordUser joined = heart.genMgr().getDiscordUser(e.getMember());
                joined.setInvitedByID(inviter.getId());

                inviter.save();
                joined.save();
                break;
            }
        });
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
        if (!heart.genMgr().isAllowedGuild(e.getGuild())) return;
        DiscordUser left = heart.genMgr().getDiscordUser(e.getMember());

        if (left.getInvitedByID() == 0) return;

        DiscordUser inviter = heart.genMgr().getDiscordUser(left.getInvitedByID());
        inviter.setDiscordInvites(inviter.getDiscordInvites() - 1);

        left.setInvitedByID(0);

        inviter.save();
        left.save();
    }


    public static final Map<String, InviteData> inviteCache = new ConcurrentHashMap<>();

    @Override
    public void onGuildInviteCreate(final GuildInviteCreateEvent event) {
        final String code = event.getCode();
        final InviteData inviteData = new InviteData(event.getInvite());
        inviteCache.put(code, inviteData);
    }

    @Override
    public void onGuildInviteDelete(final GuildInviteDeleteEvent event) {
        final String code = event.getCode();
        inviteCache.remove(code);
    }

    @Override
    public void onGuildReady(final GuildReadyEvent event) {
        final Guild guild = event.getGuild();
        attemptInviteCaching(guild);
    }

    @Override
    public void onGuildLeave(final GuildLeaveEvent event) {
        final long guildId = event.getGuild().getIdLong();
        inviteCache.entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
    }

    public static void attemptInviteCaching(final Guild guild) {
        if (guild == null) return;
        final Member selfMember = guild.getSelfMember();
        if (!selfMember.hasPermission(Permission.MANAGE_SERVER))
            return;

        guild.retrieveInvites().queue(retrievedInvites -> retrievedInvites.forEach(retrievedInvite -> inviteCache.put(retrievedInvite.getCode(), new InviteData(retrievedInvite))));
    }

}

class InviteData {
    private final long guildId;
    private int uses;

    public InviteData(final Invite invite) {
        this.guildId = invite.getGuild().getIdLong();
        this.uses = invite.getUses();
    }

    public long getGuildId() {
        return guildId;
    }

    public int getUses() {
        return uses;
    }

    public void incrementUses() {
        this.uses++;
    }
}




