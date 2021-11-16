package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.config.CommandData;
import com.i0dev.discordbot.object.config.MultiCommandData;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CmdInvite extends DiscordCommand {

    public CmdInvite(Heart heart, CommandData configSection) {
        super(heart, configSection);
    }

    @Override
    protected void setupCommand() {
        setCommand("invite");
        setDescription("The invite module.");
        addSubcommand(new SubcommandData("invites", "Check a users invites")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to check invites of.", false)));
        addSubcommand(new SubcommandData("leaderboard", "Check the current invite leaderboard."));
        addSubcommand(new SubcommandData("add", "Add invites to a user.")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to add invites to.", true),
                        new OptionData(OptionType.INTEGER, "amount", "The amount of invites to add.", true)
                                .setRequiredRange(0, 100000000)
                )
        );
        addSubcommand(new SubcommandData("remove", "Remove invites from a user.")
                .addOptions(
                        new OptionData(OptionType.USER, "user", "The user to remove invites from.", true),
                        new OptionData(OptionType.INTEGER, "amount", "The amount of invites to remove.", true)
                                .setRequiredRange(0, 100000000)
                )
        );
        addSubcommand(new SubcommandData("clear", "Clear all invite data."));

    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("invites".equals(e.getSubcommandName())) invites(e, data);
        if ("leaderboard".equals(e.getSubcommandName())) leaderboard(e, data);
        if ("add".equals(e.getSubcommandName())) add(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("clear".equals(e.getSubcommandName())) clear(e, data);
    }

    public void invites(SlashCommandEvent e, CommandEventData data) {
        User user = e.getUser();
        if (e.getOption("user") != null) user = e.getOption("user").getAsUser();

        data.reply(EmbedMaker.builder()
                .user(user)
                .content("{tag}'s discord Invites: `{invites}`")
                .colorHexCode(heart.successColor())
                .build());
    }

    @SneakyThrows
    public void leaderboard(SlashCommandEvent e, CommandEventData data) {
        List<String> list = new ArrayList<>();
        MultiCommandData cnf = (MultiCommandData) getConfigSection();
        ResultSet result = heart.sqlMgr().runQueryWithResult("select * from DiscordUser order by discordInvites desc limit " + cnf.getSubCommandConfigOption("leaderboard", "limit"));
        int place = 1;
        while (result.next()) {
            long id = result.getLong("id");
            long invites = result.getLong("discordInvites");
            if (invites == 0) continue;
            User user = heart.getJda().getUserById(id);
            if (user == null) continue;

            list.add("**#" + place + ".** " + user.getAsTag() + ": `" + invites + " invites`");
            place++;
        }

        if (list.size() == 0) {
            data.reply(EmbedMaker.builder()
                    .user(e.getUser())
                    .content("There is currently not any invite data.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }
        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .title("Invited Users Leaderboard")
                .content(heart.genMgr().formatStringList(list, "\n", false))
                .colorHexCode(heart.successColor())
                .build());

    }

    public void add(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        long amount = e.getOption("amount").getAsLong();
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        discordUser.setDiscordInvites(discordUser.getDiscordInvites() + amount);
        discordUser.save();

        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have added `{amt}` invites to {tag}, they now have a total of: {invites} invites.".replace("{amt}", amount + ""))
                .colorHexCode(heart.successColor())
                .build());
    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        long amount = e.getOption("amount").getAsLong();
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        discordUser.setDiscordInvites(discordUser.getDiscordInvites() - amount);
        discordUser.save();

        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have removed `{amt}` invites from {tag}, they now have a total of: {invites} invites.".replace("{amt}", amount + ""))
                .colorHexCode(heart.successColor())
                .build());
    }


    public void clear(SlashCommandEvent e, CommandEventData data) {
        heart.sqlMgr().runQuery("update DiscordUser set discordInvites = 0;");

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have cleared invite statistics.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
