package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class CmdMute extends DiscordCommand {

    public CmdMute(Heart heart) {
        super(heart);
    }


    @Override
    protected void setupCommand() {
        setCommand("mute");
        setDescription("The mute/timeout module.");
        addSubcommand(new SubcommandData("add", "Times out a user.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to timeout.", true))
                .addOptions(new OptionData(OptionType.STRING, "reason", "The reason for the timeout", false))
                .addOptions(new OptionData(OptionType.STRING, "length", "How long to timeout for. (3w2d5h, 5h10m20s).", false))
        );
        addSubcommand(new SubcommandData("remove", "Removes the timeout from a user.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to remove the timeout from.", true)));
        addSubcommand(new SubcommandData("list", "Lists users that are timed out."));
        addSubcommand(new SubcommandData("clear", "Clears all timed out users"));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("add".equals(e.getSubcommandName())) add(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("list".equals(e.getSubcommandName())) list(e, data);
        if ("clear".equals(e.getSubcommandName())) clear(e, data);
    }

    public void add(SlashCommandEvent e, CommandEventData data) {
        Member member = e.getOption("user").getAsMember();
        String reason = "Nothing provided";
        String length = "";

        if (member.isTimedOut()) {
            data.reply(EmbedMaker.builder()
                    .user(member.getUser())
                    .content("This user is already muted.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }
        if (e.getOption("reason") != null) reason = e.getOption("reason").getAsString();
        if (e.getOption("length") != null) length = e.getOption("length").getAsString();

        String formatTime;
        if ("".equalsIgnoreCase(length)) {
            formatTime = "28 days";
            member.timeoutFor(Duration.ofDays(28)).queue();

        } else {
            long len = heart.genMgr().deserializeStringToMilliseconds(length);
            if (len == -1) {
                data.reply(EmbedMaker.builder()
                        .content("Invalid time format. Enter in this format: `1w4m`, `1d4m2s` etc.")
                        .colorHexCode(heart.failureColor())
                        .build());
            }
            formatTime = "<t:" + ((len + System.currentTimeMillis()) / 1000) + ":R>";
            member.timeoutFor(Duration.ofMillis(len)).queue();
        }

        data.reply(EmbedMaker.builder()
                .user(member.getUser())
                .author(e.getUser())
                .content("You have muted {tag}\nReason: {reason}\nLength: {length}"
                        .replace("{reason}", reason)
                        .replace("{length}", formatTime)
                )
                .colorHexCode(heart.successColor())
                .build());
    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        Member member = e.getOption("user").getAsMember();
        if (!member.isTimedOut()) {
            data.reply(EmbedMaker.builder()
                    .user(member.getUser())
                    .author(e.getUser())
                    .content("This user is not muted.")
                    .colorHexCode(heart.failureColor())
                    .build());
            return;
        }

        data.reply(EmbedMaker.builder()
                .user(member.getUser())
                .author(e.getUser())
                .content("You have un-muted {tag}.")
                .colorHexCode(heart.successColor())
                .build());
    }

    @SneakyThrows
    public void list(SlashCommandEvent e, CommandEventData data) {
        List<String> list = new ArrayList<>();
        e.getGuild().getMembers().stream().filter(Member::isTimedOut).forEach(m -> list.add(m.getUser().getAsTag()));

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
        e.getGuild().getMembers().stream().filter(Member::isTimedOut).forEach(member -> member.removeTimeout().queue());

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have cleared all muted users.")
                .colorHexCode(heart.successColor())
                .build());
    }
}
