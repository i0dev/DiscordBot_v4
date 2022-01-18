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

package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractManager;
import lombok.SneakyThrows;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class InviteManager extends AbstractManager {
    public InviteManager(Heart heart) {
        super(heart);
    }

    @SneakyThrows
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (!heart.genMgr().isAllowedGuild(e.getGuild())) return;
        AtomicBoolean found = new AtomicBoolean(false);
        DiscordUser joined = heart.genMgr().getDiscordUser(e.getMember());

        e.getGuild().retrieveInvites().queue(retrievedInvites -> {
            for (final Invite retrievedInvite : retrievedInvites) {
                final String code = retrievedInvite.getCode();
                final InviteData cachedInvite = inviteCache.get(code);
                if (cachedInvite == null || retrievedInvite.getUses() == cachedInvite.getUses() || retrievedInvite.getInviter() == null)
                    continue;
                cachedInvite.incrementUses();

                DiscordUser inviter = heart.genMgr().getDiscordUser(retrievedInvite.getInviter());
                inviter.setDiscordInvites(inviter.getDiscordInvites() + 1);

                joined.setInvitedByID(inviter.getId());


                inviter.save();
                joined.save();
                found.set(true);
                break;
            }
        });

        if (!found.get()) {
            if (e.getGuild().getVanityCode() != null) {
                joined.setInvitedByID(-1);
                joined.save();
            }
        }

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

    @SneakyThrows
    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {
        final String code = event.getCode();
        final InviteData inviteData = new InviteData(event.getInvite());
        inviteCache.put(code, inviteData);
    }

    @SneakyThrows
    @Override
    public void onGuildInviteDelete(GuildInviteDeleteEvent event) {
        final String code = event.getCode();
        inviteCache.remove(code);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        final Guild guild = event.getGuild();
        attemptInviteCaching(guild);
    }

    @SneakyThrows
    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        long guildId = event.getGuild().getIdLong();
        inviteCache.entrySet().removeIf(entry -> entry.getValue().getGuildId() == guildId);
    }

    public static void attemptInviteCaching(final Guild guild) {
        if (guild == null) return;
        Member selfMember = guild.getSelfMember();
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




