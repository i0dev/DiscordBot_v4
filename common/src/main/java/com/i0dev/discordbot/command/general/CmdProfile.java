package com.i0dev.discordbot.command.general;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Requirement;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CmdProfile extends DiscordCommand {
    public CmdProfile(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("profile");
        setDescription("Sends the users profile.");
        addOption(new OptionData(OptionType.USER, "user", "get the profile of target user", false));
        addRequirement(Requirement.IN_GUILD);
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        User user;
        if (e.getOptions().size() == 0) user = e.getUser();
        else user = e.getOption("user").getAsUser();

        DiscordUser discordUser = data.getDiscordUser();
        Member member = e.getGuild().getMember(user);

        StringBuilder bot = new StringBuilder();
        bot.append("Tickets Closed: ").append("`{ticketsClosed}`").append("\n");
        bot.append("Discord Invites: ").append("`{invites}`").append("\n");
        bot.append("Warnings: ").append("`{warnings}`").append("\n");
        bot.append("Blacklisted: ").append("`{blacklisted}`").append("\n");
        bot.append("Muted: ").append("`{muted}`").append("\n");
        bot.append("Mute Ending: ").append("{muteExpiry}").append("\n");

        User invitedBy = null;
        if (discordUser.getInvitedByID() != 0)
            invitedBy = heart.genMgr().retrieveUser(user.getIdLong());
        bot.append("Invited By: `").append(invitedBy == null ? "Unknown" : invitedBy.getAsTag()).append("`\n");
        bot.append("Linked IGN: ").append("`{ign}`").append("\n");
        bot.append("Total Boosts: ").append("`{boosts}`").append("\n");

        StringBuilder general = new StringBuilder();
        general.append("DiscordID: ").append("`{id}`").append("\n");
        general.append("Account Creation: ").append("{timeCreated}").append("\n");
        general.append("Server Joined: ").append("<t:").append(member.getTimeJoined().toInstant().toEpochMilli() / 1000L).append(":R>").append("\n");
        general.append("Mention: ").append("{mention}").append("\n");
        general.append("Tag: ").append("`{tag}`").append("\n");
        general.append("Is Bot: ").append("`{isBot}`").append("\n");
        general.append("Online Status: ").append("`").append(member.getOnlineStatus().getKey()).append("`").append("\n");
        general.append("Currently Boosting: ").append("`{isBoosting}`").append("\n");

        data.reply(EmbedMaker.builder()
                .fields(new MessageEmbed.Field[]{
                        new MessageEmbed.Field("__General Info__", general.toString(), true),
                        new MessageEmbed.Field("__Activity Info__", bot.toString(), true),
                        new MessageEmbed.Field("__Server Roles__", e.getGuild().getMember(user) == null ? "`Not in discord.`" : heart.genMgr().formatRolesList(member.getRoles()), false)
                })
                .authorName("{tag}'s User Profile")
                .authorImg(user.getEffectiveAvatarUrl())
                .user(user)
                .guild(e.getGuild())
                .thumbnail(discordUser.getMinecraftSkinTexture())
                .build());
    }

}
