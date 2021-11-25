package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.CommandConfig;
import com.i0dev.discordbot.manager.ConfigManager;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class CmdMute extends DiscordCommand {

    public CmdMute(Heart heart, CommandData configSection) {
        super(heart, configSection);
    }

    Role muteRole;

    @Override
    public void initialize() {
        muteRole = heart.getJda().getRoleById(getConfigOption("muteRole").getAsLong());
    }

    @Override
    public void deinitialize() {
        muteRole = null;
    }

    @Override
    protected void setupCommand() {
        setCommand("mute");
        setDescription("The mute module.");
        addSubcommand(new SubcommandData("add", "Mutes a user.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to mute.", true))
                .addOptions(new OptionData(OptionType.STRING, "reason", "The reason for the mute", false))
                .addOptions(new OptionData(OptionType.STRING, "length", "How long to mute for. (3w2d5h, 5h10m20s).", false))
        );
        addSubcommand(new SubcommandData("remove", "Removes the mute from a user.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to unmute.", true)));
        addSubcommand(new SubcommandData("list", "Lists muted users."));
        addSubcommand(new SubcommandData("clear", "Clears all muted users"));
        addSubcommand(new SubcommandData("create_role", "Creates a muted role and set permissions."));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("add".equals(e.getSubcommandName())) add(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("list".equals(e.getSubcommandName())) list(e, data);
        if ("clear".equals(e.getSubcommandName())) clear(e, data);
        if ("create".equals(e.getSubcommandName())) create(e, data);
    }

    public void add(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        String reason = "Nothing provided";
        String length = "";
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        if (muteRole == null) {
            data.reply(EmbedMaker.builder()
                    .content("The muted role has not yet been created. You can either make one yourself and set it in config, or use `/mute create` to automaticlaly generate one.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }

        if (discordUser.isMuted()) {
            data.reply(EmbedMaker.builder()
                    .user(user)
                    .content("This user is already muted.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }
        if (e.getOption("reason") != null) reason = e.getOption("reason").getAsString();
        if (e.getOption("length") != null) reason = e.getOption("length").getAsString();

        String formatTime;
        if ("".equalsIgnoreCase(length)) {
            discordUser.setUnmuteAtTime(-1);
            formatTime = "Forever";
        } else {
            long len = heart.genMgr().deserializeStringToMilliseconds(length);
            if (len == -1) {
                data.reply(EmbedMaker.builder()
                        .content("Invalid time format. Enter in this format: `1w4m`, `1d4m2s` etc.")
                        .colorHexCode(heart.failureColor())
                        .build());
            }
            discordUser.setUnmuteAtTime(System.currentTimeMillis() + len);
            formatTime = "<t:" + ((len + System.currentTimeMillis()) / 1000) + ":R>";
        }

        discordUser.addRole(muteRole);
        discordUser.setMuted(true);
        discordUser.save();
        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have muted {tag}\nReason: {reason}\nLength: {length}"
                        .replace("{reason}", reason)
                        .replace("{length}", formatTime)
                )
                .colorHexCode(heart.successColor())
                .build());
    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        if (!discordUser.isMuted()) {
            data.reply(EmbedMaker.builder()
                    .user(user)
                    .author(e.getUser())
                    .content("This user is not muted.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }
        discordUser.setMuted(false);
        discordUser.setUnmuteAtTime(0);
        discordUser.removeRole(muteRole);
        discordUser.save();
        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have un-muted {tag}.")
                .colorHexCode(heart.successColor())
                .build());
    }

    @SneakyThrows
    public void list(SlashCommandEvent e, CommandEventData data) {
        List<String> list = new ArrayList<>();
        ResultSet result = heart.sqlMgr().runQueryWithResult("select * from DiscordUser where muted = 1;");
        while (result.next()) {
            long id = result.getLong("id");
            User user = heart.getJda().getUserById(id);
            if (user == null) continue;
            list.add(user.getAsTag());
        }

        if (list.size() == 0) {
            data.reply(EmbedMaker.builder()
                    .user(e.getUser())
                    .content("There are no muted users.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content(heart.genMgr().formatStringList(list, "\n", false))
                .title("Muted Users")
                .colorHexCode(heart.successColor())
                .build());
    }

    public void clear(SlashCommandEvent e, CommandEventData data) {
        heart.sqlMgr().runQuery("update DiscordUser set muted = 0;");

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have cleared all muted users.")
                .colorHexCode(heart.successColor())
                .build());
    }

    public void create(SlashCommandEvent e, CommandEventData data) {
        Role role = e.getGuild().createRole().setName("Muted").setColor(java.awt.Color.darkGray).complete();
        for (TextChannel channel : e.getGuild().getTextChannels()) {
            channel.putPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queueAfter(5, TimeUnit.SECONDS);
        }
        for (VoiceChannel channel : e.getGuild().getVoiceChannels()) {
            channel.putPermissionOverride(role).setDeny(Permission.VOICE_SPEAK).queueAfter(5, TimeUnit.SECONDS);
        }
        setMuteRole(role);
        CommandData sec = getConfigSection();
        sec.getOptions().addProperty("muteRole", role.getId());
        setConfigSection(sec);
        heart.cnfMgr().save(heart.getConfig(CommandConfig.class), heart.getConfig(CommandConfig.class).getPath());
        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have auto-generated a muted role.\nID: `{id}`".replace("{id}", role.getId()))
                .colorHexCode(heart.successColor())
                .build());
    }
}
