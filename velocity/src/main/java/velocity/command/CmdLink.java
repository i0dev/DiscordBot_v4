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

package velocity.command;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.manager.LinkManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import velocity.object.AbstractCommand;

import java.awt.*;
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
    public void execute(CommandSource source, String @NonNull [] args) {
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("Only players can run this command", NamedTextColor.RED));
            return;
        }
        Player sender = (Player) source;
        if (args.length == 0) {
            source.sendMessage(Component.text("Usage: ", NamedTextColor.GRAY)
                    .append(Component.text("/link generate", NamedTextColor.RED)));
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("generate")) {
            LinkManager lm = heart.getManager(LinkManager.class);

            ResultSet resultSet = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE minecraftUUID = '" + sender.getUniqueId() + "'");

            if (resultSet.next()) {
                User user = heart.genMgr().retrieveUser(Long.parseLong(resultSet.getString("id")));
                source.sendMessage(Component.text("You are already linked to the Discord account:", NamedTextColor.RED)
                        .append(Component.text(user.getAsTag(), NamedTextColor.GRAY).append(Component.text(".", NamedTextColor.RED)))
                );
                return;
            }

            if (lm.isOnLinkList(sender.getUniqueId())) {
                source.sendMessage(Component.text("You have already generated a link code.", NamedTextColor.RED));


                AtomicReference<String> pastcode = new AtomicReference<>("");
                AtomicBoolean end = new AtomicBoolean(false);
                lm.pendingLinkMap.forEach((s, uuid) -> {
                    if (end.get()) return;
                    if (uuid.equals(sender.getUniqueId())) {
                        pastcode.set(s);
                        end.set(true);
                    }
                });
                source.sendMessage(Component.text("Your code is: ", NamedTextColor.RED)
                        .append(Component.text(pastcode.get(), NamedTextColor.GRAY))
                );
                return;
            }
            String code = lm.generateCode((int) heart.cnf().getLinkCodeLength());
            lm.addToLinkList(code, sender.getUniqueId());

            source.sendMessage(Component.text("You have generated a link code!", NamedTextColor.RED));
            source.sendMessage(Component.text("Your code is: ", NamedTextColor.RED)
                    .append(Component.text(code, NamedTextColor.GRAY)));
            source.sendMessage(Component.text("Type the command ", NamedTextColor.RED)
                    .append(Component.text("/link code " + code, NamedTextColor.GRAY))
                    .append(Component.text(" in discord.", NamedTextColor.RED))
            );

            source.sendMessage(Component.text("(Click to get command)").decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE)
                    .clickEvent(ClickEvent.suggestCommand("/link code " + code))

            );

            return;
        }

        source.sendMessage(Component.text("Usage: ").append(Component.text("/link generate")));
    }


    @Override
    public List<String> suggest(@NonNull CommandSource sender, String[] args) {
        if (args.length == 1) return tabCompleteHelper(args[0], Collections.singletonList("generate"));
        return blank;
    }
}
