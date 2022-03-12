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
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import velocity.object.AbstractCommand;

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
            source.sendMessage(TextComponent.of("Only players can run this command").color(TextColor.RED));
            return;
        }
        Player sender = (Player) source;
        if (args.length == 0) {
            source.sendMessage(TextComponent.of("Usage: ").color(TextColor.GRAY).append(TextComponent.of("/link generate").color(TextColor.RED)));
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("generate")) {
            LinkManager lm = heart.getManager(LinkManager.class);

            ResultSet resultSet = heart.sqlMgr().runQueryWithResult("SELECT * FROM DiscordUser WHERE minecraftUUID = '" + sender.getUniqueId() + "'");

            if (resultSet.next()) {
                User user = heart.genMgr().retrieveUser(Long.parseLong(resultSet.getString("id")));
                source.sendMessage(TextComponent.of("You are already linked to the Discord account:").color(TextColor.RED)
                        .append(TextComponent.of(user.getAsTag()).color(TextColor.GRAY).append(TextComponent.of(".").color(TextColor.RED)))
                );
                return;
            }

            if (lm.isOnLinkList(sender.getUniqueId())) {
                source.sendMessage(TextComponent.of("You have already generated a link code.").color(TextColor.RED));


                AtomicReference<String> pastcode = new AtomicReference<>("");
                AtomicBoolean end = new AtomicBoolean(false);
                lm.pendingLinkMap.forEach((s, uuid) -> {
                    if (end.get()) return;
                    if (uuid.equals(sender.getUniqueId())) {
                        pastcode.set(s);
                        end.set(true);
                    }
                });
                source.sendMessage(TextComponent.of("Your code is: ").color(TextColor.RED)
                        .append(TextComponent.of(pastcode.get()).color(TextColor.GRAY))
                );
                return;
            }
            String code = lm.generateCode((int) heart.cnf().getLinkCodeLength());
            lm.addToLinkList(code, sender.getUniqueId());

            source.sendMessage(TextComponent.of("You have generated a link code!").color(TextColor.RED));
            source.sendMessage(TextComponent.of("Your code is: ").color(TextColor.RED)
                    .append(TextComponent.of(code).color(TextColor.GRAY)));
            source.sendMessage(TextComponent.of("Type the command ").color(TextColor.RED)
                    .append(TextComponent.of("/link code " + code)).color(TextColor.GRAY)
                    .append(TextComponent.of(" in discord.").color(TextColor.RED))
            );

            source.sendMessage(TextComponent.of("(Click to get command)").decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE)
                    .clickEvent(ClickEvent.suggestCommand("/link code " + code))

            );

            return;
        }

        source.sendMessage(TextComponent.of("Usage: ").color(TextColor.GRAY).append(TextComponent.of("/link generate").color(TextColor.RED)));
    }


    @Override
    public List<String> suggest(@NonNull CommandSource sender, String[] args) {
        if (args.length == 1) return tabCompleteHelper(args[0], Collections.singletonList("generate"));
        return blank;
    }
}
