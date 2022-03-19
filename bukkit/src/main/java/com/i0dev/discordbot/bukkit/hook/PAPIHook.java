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
    public @NotNull
    String getIdentifier() {
        return "discordbot";
    }

    @Override
    public @NotNull
    String getAuthor() {
        return "i01";
    }

    @Override
    public @NotNull
    String getVersion() {
        return Heart.VERSION;
    }

    @SneakyThrows
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        DiscordUser user = null;
        ResultSet resultSet = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE minecraftUUID = '" + player.getUniqueId() + "'");
        if (resultSet.next()) user = heart.genMgr().getDiscordUser(resultSet.getString("id"));

        // User Specific
        if (params.equalsIgnoreCase("is_linked"))
            return user == null ? "false" : "true";
        else if (params.equalsIgnoreCase("is_boosting")) {
            if (user != null) {
                for (Guild guild : heart.getAllowedGuilds()) {
                    Member member = guild.getMember(user.getAsUser());
                    if (member == null) continue;
                    if (member.isBoosting()) return "true";
                }
            }
            return "false";
        } else if (params.equalsIgnoreCase("linked_tag"))
            return user == null ? "" : user.getAsUser().getAsTag();




        return null;
    }
}
