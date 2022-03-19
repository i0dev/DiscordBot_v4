package com.i0dev.discordbot.bukkit.hook;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import lombok.SneakyThrows;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

public class PAPIHook extends PlaceholderExpansion {

    Heart heart;

    public PAPIHook(Heart heart) {
        this.heart = heart;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "discordbot";
    }

    @Override
    public @NotNull String getAuthor() {
        return "i01";
    }

    @Override
    public @NotNull String getVersion() {
        return Heart.VERSION;
    }

    @SneakyThrows
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        DiscordUser discordUser = null;
        User user = null;
        ResultSet resultSet = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE minecraftUUID = '" + player.getUniqueId() + "'");

        if (resultSet.next()) {
            String id = resultSet.getString("id");
            discordUser = heart.genMgr().getDiscordUser(id);
            user = heart.getJda().retrieveUserById(id).complete();
        }

        // User Specific
        if (params.equalsIgnoreCase("is_linked")) {
            return discordUser == null ? "false" : "true";
        }

        if (params.equalsIgnoreCase("is_boosting")) {
            boolean isBoosting = false;
            if (discordUser != null && user != null) {
                for (Guild allowedGuild : heart.getAllowedGuilds()) {
                    Member member = allowedGuild.getMember(user);
                    if (member == null) continue;
                    if (member.isBoosting()) {
                        isBoosting = true;
                        break;
                    }
                }
            }
            return isBoosting ? "true" : "false";
        }

        if (params.equalsIgnoreCase("linked_tag")) {
            if (discordUser == null || user == null) return "";
            return user.getAsTag();
        }


        return null;
    }
}
