package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class GeneralManager extends AbstractManager {

    public GeneralManager(Heart heart) {
        super(heart);
    }

    public String capitalizeFirst(String a) {
        return a.substring(0, 1).toUpperCase() + a.substring(1).toLowerCase();
    }

    public User retrieveUser(long id) {
        return getHeart().getJda().retrieveUserById(id).complete();
    }

    public DiscordUser getDiscordUser(ISnowflake iSnowflake) {
        return getDiscordUser(iSnowflake.getIdLong());
    }

    public DiscordUser getDiscordUser(long id) {
        DiscordUser user = (DiscordUser) heart.sqlMgr().getObject("id", id, DiscordUser.class);
        if (user == null) {
            user = new DiscordUser(id, heart);
            user.save();
        }
        return user;
    }

    public String formatRolesList(List<Role> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>();
        for (Role s : list) {
            Stripped.add(capitalizeFirst(s.getAsMention()));
        }
        for (int i = 0; i < Stripped.size(); i++) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public String formatStringList(List<String> list, String determiner, boolean capitalizeFirst) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> stripped = new ArrayList<>();
        for (String s : list) {
            if (capitalizeFirst)
                stripped.add(capitalizeFirst(s));
            else stripped.add(s);
        }

        for (int i = 0; i < stripped.size(); i++) {
            sb.append(stripped.get(i));
            if (stripped.size() - 1 > i) {
                sb.append(determiner);
            }
        }
        return sb.toString();
    }

    public int randomNumber(int max) {
        int num = Math.abs((int) (Math.random() * max));
        if (num == 0)
            randomNumber(max);
        return num;
    }

    public boolean isAllowedGuild(Guild guild) {
        return heart.getAllowedGuilds().contains(guild);
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public long deserializeStringToMilliseconds(String input) {
        input = input.toLowerCase();
        if (input.isEmpty()) return -1;
        int time = 0;
        StringBuilder number = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (isInt(String.valueOf(c))) {
                number.append(c);
                continue;
            }
            if (number.toString().isEmpty()) return -1;
            int add = Integer.parseInt(number.toString());
            switch (c) {
                case 'w':
                    add *= 7;
                case 'd':
                    add *= 24;
                case 'h':
                    add *= 60;
                case 'm':
                    add *= 60;
                case 's':
                    time += add;
                    number.setLength(0);
                    break;
                default:
                    return -1;
            }
        }
        return time * 1000L;
    }

}
