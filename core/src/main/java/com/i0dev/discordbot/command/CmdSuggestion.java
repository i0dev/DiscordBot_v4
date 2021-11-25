package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.config.CommandConfig;
import com.i0dev.discordbot.config.SuggestionStorage;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.object.command.Suggestion;
import com.i0dev.discordbot.object.config.CommandData;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
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
public class CmdSuggestion extends DiscordCommand {

    public CmdSuggestion(Heart heart, CommandData configSection) {
        super(heart, configSection);
    }

    TextChannel pending, accepted, denied;

    SuggestionStorage storage;

    Emoji upvoteEmoji, downvoteEmoji;

    @Override
    public void initialize() {
        pending = heart.getJda().getTextChannelById(getConfigOption("pending").getAsLong());
        accepted = heart.getJda().getTextChannelById(getConfigOption("accepted").getAsLong());
        denied = heart.getJda().getTextChannelById(getConfigOption("denied").getAsLong());
        storage = heart.getConfig(SuggestionStorage.class);
        upvoteEmoji = Emoji.fromMarkdown(getConfigOption("upvoteEmoji").getAsString());
        downvoteEmoji = Emoji.fromMarkdown(getConfigOption("downvoteEmoji").getAsString());
    }

    @Override
    public void deinitialize() {
        pending = null;
        accepted = null;
        denied = null;
        storage = null;
        upvoteEmoji = null;
        downvoteEmoji = null;
    }

    @Override
    protected void setupCommand() {
        setCommand("suggestion");
        setDescription("The suggestion module.");
        addSubcommand(new SubcommandData("add", "Creates a suggestion to be added to the pending channel.")
                .addOptions(new OptionData(OptionType.STRING, "suggestion", "The suggestion you wish to make.", true))
                .addOptions(new OptionData(OptionType.STRING, "gamemode", "The game-mode that this suggestion refers to.", false)));
        addSubcommand(new SubcommandData("accept", "Removes the mute from a user.")
                .addOptions(new OptionData(OptionType.STRING, "message", "The ID of the message to accept.", true))
                .addOptions(new OptionData(OptionType.STRING, "note", "A note about the suggestion.", false)));
        addSubcommand(new SubcommandData("deny", "Lists muted users.")
                .addOptions(new OptionData(OptionType.STRING, "message", "The ID of the message to deny.", true))
                .addOptions(new OptionData(OptionType.STRING, "note", "A note about the suggestion.", false)));
    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("add".equals(e.getSubcommandName())) add(e, data);
        if ("accept".equals(e.getSubcommandName())) accept(e, data);
        if ("deny".equals(e.getSubcommandName())) deny(e, data);
    }

    public void add(SlashCommandEvent e, CommandEventData data) {
        String suggestion = e.getOption("suggestion").getAsString();
        String gamemode = e.getOption("gamemode") == null ? "Global" : e.getOption("gamemode").getAsString();

        Message msg = pending.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(
                EmbedMaker.builder()
                        .user(e.getUser())
                        .authorImg(e.getUser().getEffectiveAvatarUrl())
                        .authorName("Suggestion from: {tag}")
                        .content("`" + suggestion + "`")
                        .footer("Gamemode: " + gamemode)
                        .build()
        )).complete();

        Suggestion sugObj = new Suggestion(msg.getIdLong(), pending.getIdLong(), e.getUser().getIdLong(), suggestion, gamemode);
        storage.getSuggestions().add(sugObj);
        heart.cnfMgr().save(storage);

        msg.addReaction(upvoteEmoji.getAsMention()).queue();
        msg.addReaction(downvoteEmoji.getAsMention()).queue();

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .content("You have successfully submitted your suggestion")
                .colorHexCode(heart.successColor())
                .build());
    }

    public void accept(SlashCommandEvent e, CommandEventData data) {
        String id = e.getOption("message").getAsString();
        Suggestion suggestion = storage.getSuggestionById(id);

        if (suggestion == null) {
            data.replyFailure("Could not find the target suggestion.");
            return;
        }

        TextChannel channel = heart.getJda().getTextChannelById(suggestion.getChannelID());
        if (channel == null) {
            data.replyFailure("The channel no longer exists for that suggestion.");
            storage.getSuggestions().remove(suggestion);
            heart.cnfMgr().save(storage);
            return;
        }
        Message message = channel.retrieveMessageById(id).complete();
        message.delete().queue();
        User user = heart.getJda().retrieveUserById(suggestion.getUserID()).complete();

        MessageEmbed.Field[] fields = new MessageEmbed.Field[3];
        fields[0] = new MessageEmbed.Field("Suggestion for {gamemode}:".replace("{gamemode}", suggestion.getGamemode()), suggestion.getSuggestion(), false);
        if (e.getOption("note") != null)
            fields[1] = new MessageEmbed.Field("note from {authorTag}", e.getOption("note").getAsString(), false);
        else fields[1] = null;

        StringBuilder stats = new StringBuilder();
        stats.append("Up Votes: ").append(message.getReactions().stream().filter(messageReaction -> messageReaction.getReactionEmote().getEmoji().equals(upvoteEmoji.getName())).count());
        stats.append("\nDown Votes: ").append(message.getReactions().stream().filter(messageReaction -> messageReaction.getReactionEmote().getEmoji().equals(downvoteEmoji.getName())).count());

        fields[2] = new MessageEmbed.Field("Stats", stats.toString(), false);
        accepted.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(
                EmbedMaker.builder()
                        .user(user)
                        .authorName("Suggestion from {tag} accepted")
                        .fields(fields)
                        .author(e.getUser())
                        .authorImg(user.getEffectiveAvatarUrl())
                        .colorHexCode(heart.successColor())
                        .build()
        )).queue();

        storage.getSuggestions().remove(suggestion);
        heart.cnfMgr().save(storage);

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have successfully accepted that suggestion.")
                .colorHexCode(heart.successColor())
                .build());
    }

    @SneakyThrows
    public void deny(SlashCommandEvent e, CommandEventData data) {
        String id = e.getOption("message").getAsString();
        Suggestion suggestion = storage.getSuggestionById(id);

        if (suggestion == null) {
            data.replyFailure("Could not find the target suggestion.");
            return;
        }

        TextChannel channel = heart.getJda().getTextChannelById(suggestion.getChannelID());
        if (channel == null) {
            data.replyFailure("The channel no longer exists for that suggestion.");
            storage.getSuggestions().remove(suggestion);
            heart.cnfMgr().save(storage);
            return;
        }
        Message message = channel.retrieveMessageById(id).complete();
        message.delete().queue();
        User user = heart.getJda().retrieveUserById(suggestion.getUserID()).complete();

        MessageEmbed.Field[] fields = new MessageEmbed.Field[3];
        fields[0] = new MessageEmbed.Field("Suggestion for {gamemode}:".replace("{gamemode}", suggestion.getGamemode()), suggestion.getSuggestion(), false);
        if (e.getOption("note") != null)
            fields[1] = new MessageEmbed.Field("note from {authorTag}", e.getOption("note").getAsString(), false);
        else fields[1] = null;
        StringBuilder stats = new StringBuilder();
        stats.append("Up Votes: ").append(message.getReactions().stream().filter(messageReaction -> messageReaction.getReactionEmote().getEmoji().equals(upvoteEmoji.getName())).count());
        stats.append("\nDown Votes: ").append(message.getReactions().stream().filter(messageReaction -> messageReaction.getReactionEmote().getEmoji().equals(downvoteEmoji.getName())).count());

        fields[2] = new MessageEmbed.Field("Stats", stats.toString(), false);
        denied.sendMessageEmbeds(heart.msgMgr().createMessageEmbed(
                EmbedMaker.builder()
                        .user(user)
                        .authorName("Suggestion from {tag} denied")
                        .fields(fields)
                        .author(e.getUser())
                        .authorImg(user.getEffectiveAvatarUrl())
                        .colorHexCode(heart.failureColor())
                        .build()
        )).queue();

        storage.getSuggestions().remove(suggestion);
        heart.cnfMgr().save(storage);

        data.reply(EmbedMaker.builder()
                .user(e.getUser())
                .author(e.getUser())
                .content("You have successfully denied that suggestion.")
                .colorHexCode(heart.successColor())
                .build());
    }

}
