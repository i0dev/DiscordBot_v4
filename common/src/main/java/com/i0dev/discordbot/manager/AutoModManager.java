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
