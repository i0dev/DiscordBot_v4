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

package com.i0dev.discordbot.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.manager.LinkManager;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.StartupTag;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.sql.ResultSet;
import java.util.UUID;

@Setter
@Getter
public class CmdLink extends DiscordCommand {

    public CmdLink(Heart heart) {
        super(heart);
    }

    @Override
    protected void setupCommand() {
        setCommand("link");
        setDescription("The link module.");
        addSubcommand(new SubcommandData("code", "Link yourself with a generated code.")
                .addOptions(new OptionData(OptionType.STRING, "code", "The generated code from in game.", true))
        );
        addSubcommand(new SubcommandData("force", "Force links a user to an IGN.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to link.", true))
                .addOptions(new OptionData(OptionType.STRING, "ign", "the users IGN to link.", true))
        );
        addSubcommand(new SubcommandData("info", "Get information about a users link status.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to check.", true))
        );
        addSubcommand(new SubcommandData("remove", "Remove a users link.")
                .addOptions(new OptionData(OptionType.USER, "user", "The user to remove link status.", true))
        );
        addSubcommand(new SubcommandData("check_ign", "check if an IGN is linked.")
                .addOptions(new OptionData(OptionType.STRING, "ign", "the IGN to check.", true))
        );

    }

    @Override
    public void execute(SlashCommandEvent e, CommandEventData data) {
        if ("code".equals(e.getSubcommandName())) code(e, data);
        if ("force".equals(e.getSubcommandName())) force(e, data);
        if ("info".equals(e.getSubcommandName())) info(e, data);
        if ("remove".equals(e.getSubcommandName())) remove(e, data);
        if ("check_ign".equals(e.getSubcommandName())) check_ign(e, data);
    }

    public void code(SlashCommandEvent e, CommandEventData data) {
        String code = e.getOption("code").getAsString();

        if (data.getDiscordUser().isLinked()){
            data.replyFailure("You are already linked to the ign {ign}!".replace("{ign}", data.getDiscordUser().getMinecraftIGN()));
            return;
        }

        LinkManager lm = getHeart().getManager(LinkManager.class);
        UUID uuid = lm.getUUIDFromCode(code);

        if (!lm.isOnLinkList(code) || uuid == null) {
            e.reply("The code ``" + code + "`` can not be found. Please check your code and try again.").setEphemeral(true);
            return;
        }


        DiscordUser discordUser = getHeart().genMgr().getDiscordUser(e.getUser());

        discordUser.setLinked(true);
        discordUser.setMinecraftIGN(heart.apiMgr().getIGNFromUUID(uuid.toString()));
        discordUser.setMinecraftUUID(uuid.toString());
        discordUser.setLinkCode(code);
        discordUser.setLinkedTime(System.currentTimeMillis());
        discordUser.save();

        e.reply("You have linked your account to **" + discordUser.getMinecraftIGN() + "**").setEphemeral(true).queue();
        lm.removeFromLinkList(code);

        if (heart.cnf().isVerifyUserOnLink()) {
            heart.genMgr().verifyMember(e.getMember());
        }

        heart.logDiscord(EmbedMaker.builder()
                .user(e.getUser())
                .colorHexCode(heart.normalColor())
                .content("**{tag}** has linked their account to **{ign}**")
                .build());
    }

    public void force(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        String ign = e.getOption("ign").getAsString();

        DiscordUser discordUser = getHeart().genMgr().getDiscordUser(user);

        discordUser.setLinked(true);
        discordUser.setMinecraftIGN(ign);
//        if (heart.getTags().contains(StartupTag.BUKKIT))
//            discordUser.setMinecraftUUID(org.bukkit.Bukkit.getOfflinePlayer(ign).getUniqueId().toString());
//        else
        discordUser.setMinecraftUUID(heart.apiMgr().getUUIDFromIGN(ign).toString());

        discordUser.setMinecraftUUID(heart.apiMgr().getUUIDFromIGN(ign).toString());
        discordUser.setLinkCode("FORCED");
        discordUser.setLinkedTime(System.currentTimeMillis());
        discordUser.save();

        data.replySuccess("Successfully linked **" + user.getAsTag() + "** to `" + ign + "`");

        heart.logDiscord(EmbedMaker.builder()
                .user(user)
                .author(e.getUser())
                .colorHexCode(heart.normalColor())
                .content("**{authorTag}** has force linked **{tag}** account to **{ign}**")
                .build());
    }

    public void info(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        DiscordUser discordUser = getHeart().genMgr().getDiscordUser(user);

        if (discordUser.isLinked()) {
            data.reply(EmbedMaker.builder()
                    .author(user).user(user)
                    .authorName("Link info for {tag}")
                    .authorImg(user.getEffectiveAvatarUrl())
                    .thumbnail(discordUser.getMinecraftSkinTexture())
                    .content(heart.cnf().getLinkInfoFormat()
                            .replace("{forced}", discordUser.getLinkCode().equals("FORCED") ? "Yes" : "No"))
                    .build());
        } else {
            data.replyFailure("User is not linked.");
        }

    }

    public void remove(SlashCommandEvent e, CommandEventData data) {
        User user = e.getOption("user").getAsUser();
        DiscordUser discordUser = getHeart().genMgr().getDiscordUser(user);

        discordUser.setLinked(false);
        discordUser.setMinecraftIGN("");
        discordUser.setMinecraftUUID("");
        discordUser.setLinkCode("");
        discordUser.setLinkedTime(0);
        discordUser.save();

        data.replySuccess("Successfully removed link for **" + user.getAsTag() + "**");
    }

    @SneakyThrows
    public void check_ign(SlashCommandEvent e, CommandEventData data) {
        String ign = e.getOption("ign").getAsString();

        ResultSet set = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE MinecraftIGN = '" + ign + "'");
        if (set.next()) {
            long id = set.getLong("id");
            DiscordUser discordUser = getHeart().genMgr().getDiscordUser(id);
            User user = getHeart().genMgr().retrieveUser(id);
            data.reply(EmbedMaker.builder()
                    .author(user)
                    .user(user)
                    .authorName("Link info for {ign}")
                    .authorImg(user.getEffectiveAvatarUrl())
                    .thumbnail(discordUser.getMinecraftSkinTexture())
                    .content(heart.cnf().getLinkInfoCheckIgnFormat()
                            .replace("{forced}", discordUser.getLinkCode().equals("FORCED") ? "Yes" : "No"))
                    .build());
        } else
            data.replyFailure("No user is linked to `" + ign + "`");
    }
}
