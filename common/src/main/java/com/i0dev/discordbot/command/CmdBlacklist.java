package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CmdBlacklist extends DiscordCommand {

    public CmdBlacklist(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("blacklist");
        setDescription("The blacklist module.");
        addSubcommand(new SubcommandData("add", "Adds a user to the blacklist.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to the blacklist.", true)));
        addSubcommand(new SubcommandData("remove", "Removes a blacklisted user.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to remove from the blacklist.", true)));
        addSubcommand(new SubcommandData("list", "Lists users in the blacklist."));
        addSubcommand(new SubcommandData("clear", "Clears the entire blacklist."));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("add".equals(e.getSubcommandName())) add(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("list".equals(e.getSubcommandName())) list(e, data);
        if ("clear".equals(e.getSubcommandName())) clear(e, data);
    }

    public void add(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        if (discordUser.isBlacklisted()) {
            data.reply(EmbedMaker.builder()
                    .user(user)
                    .author(e.getUser())
                    .content("This user is already blacklisted.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }
        discordUser.setBlacklisted(true);
        discordUser.save();
        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have blacklisted {tag} from using all bot commands")
                .colorHexCode(heart.successColor())
                .build());
    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        DiscordUser discordUser = heart.genMgr().getDiscordUser(user.getIdLong());
        if (!discordUser.isBlacklisted()) {
            data.reply(EmbedMaker.builder()
                    .user(user)
                    .author(e.getUser())
                    .content("This user is not blacklisted.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }
        discordUser.setBlacklisted(false);
        discordUser.save();
        data.reply(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .content("You have un-blacklisted {tag} from using all bot commands.")
                .colorHexCode(heart.successColor())
                .build());
    }

    @SneakyThrows
    public void list(SlashCommandEvent e, CommandEventData data) {
        List<String> list = new ArrayList<>();
        ResultSet result = heart.sqlMgr().runQueryWithResult("select * from DiscordUser where blacklisted = 1;");
        while (result.next()) {
            long id = result.getLong("id");
            User user = heart.getJda().getUserById(id);
            if (user == null) continue;
            list.add(user.getAsTag());
        }

        if (list.size() == 0) {
            data.reply(EmbedMaker.builder()
                    .user(e.getUser())
                    .content("There are no blacklisted users.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content(heart.genMgr().formatStringList(list, "\n", false))
                .title("Blacklisted Users")
                .colorHexCode(heart.successColor())
                .build());
    }

    public void clear(SlashCommandEvent e, CommandEventData data) {
        heart.sqlMgr().runQuery("update DiscordUser set blacklisted = 0;");

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have cleared the entire blacklist.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
