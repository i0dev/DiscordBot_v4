package com.i0dev.discordbot.bukkit.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.bukkit.object.AbstractCommand;
import com.i0dev.discordbot.bukkit.util.MsgUtil;
import com.i0dev.discordbot.manager.LinkManager;
import com.i0dev.discordbot.object.DiscordUser;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class CmdLink extends AbstractCommand {

    public CmdLink(Heart heart, String command) {
        super(heart, command);
    }

    @SneakyThrows
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            MsgUtil.msg(sender, "&7Usage: &c/link generate");
            return;
        }
        if (!(sender instanceof Player)) {
            MsgUtil.msg(sender, "&cYou must be a player to use this command.");
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("generate")) {
            LinkManager lm = heart.getManager(LinkManager.class);

            ResultSet resultSet = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE minecraftUUID = '" + ((Player) sender).getUniqueId() + "'");

            if (resultSet.next()) {
                User user = heart.genMgr().retrieveUser(Long.parseLong(resultSet.getString("id")));
                MsgUtil.msg(sender, "&cYou are already linked to the Discord account: &7" + user.getAsTag() + "&c.");
                return;
            }

            if (lm.isOnLinkList(((Player) sender).getUniqueId())) {
                MsgUtil.msg(sender, "&cYou are already on the link list.");

                AtomicReference<String> pastcode = new AtomicReference<>("");
                AtomicBoolean end = new AtomicBoolean(false);
                lm.pendingLinkMap.forEach((s, uuid) -> {
                    if (end.get()) return;
                    if (uuid.equals(((Player) sender).getUniqueId())) {
                        pastcode.set(s);
                        end.set(true);
                    }
                });
                MsgUtil.msg(sender, "&cYour link code is: " + pastcode.get());
                return;
            }

            String code = lm.generateCode((int) heart.cnf().getLinkCodeLength());

            lm.addToLinkList(code, ((Player) sender).getUniqueId());
            MsgUtil.msg(sender, "&cYou have generated a link code!");
            MsgUtil.msg(sender, "&cYour link code is: &7" + code);
            MsgUtil.msg(sender, "&cType the command &7/link code " + code + " &cin discord.");
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return tabCompleteHelper(args[0], Collections.singletonList("generate"));
        return blank;
    }
}
