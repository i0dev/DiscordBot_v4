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
import com.i0dev.discordbot.config.AutoModConfig;
import com.i0dev.discordbot.object.abs.AbstractManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class AutoModManager extends AbstractManager {
    public AutoModManager(Heart heart) {
        super(heart);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMember() != null && e.getMember().getPermissions().contains(Permission.ADMINISTRATOR) && heart.getConfig(AutoModConfig.class).isAdminsBypassAutoMod())
            return;
        if (heart.getConfig(AutoModConfig.class).getChannelsToDeleteSentMessageIn().contains(e.getChannel().getIdLong())) {
            e.getMessage().delete().queue();
            return;
        }
        if (!(e.getChannel() instanceof GuildChannel))return;
        if (isInChannelWhereShouldEffect((GuildChannel) e.getChannel())) {


        }
    }

    public boolean isInChannelWhereShouldEffect(GuildChannel channel) {
        AutoModConfig amc = heart.getConfig(AutoModConfig.class);
        if (amc.isEffectedChannelsWhitelistMode())
            return amc.getAutoModEffectedChannels().contains(channel.getIdLong());
        else
            return !amc.getAutoModEffectedChannels().contains(channel.getIdLong());
    }
}
