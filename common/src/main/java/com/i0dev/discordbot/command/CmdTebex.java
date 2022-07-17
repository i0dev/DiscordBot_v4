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
import com.i0dev.discordbot.config.configs.TebexConfig;
import com.i0dev.discordbot.object.abs.CommandEventData;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.json.simple.JSONObject;

public class CmdTebex extends DiscordCommand {

    public CmdTebex(Heart heart) {
        super(heart);
    }


    String tebexSecret;
    TebexConfig cnf;

    @Override
    public void initialize() {
        cnf = getHeart().getConfig(TebexConfig.class);
        tebexSecret = cnf.getTebexSecret();
    }

    @Override
    public void deinitialize() {
        tebexSecret = null;
        cnf = null;
    }

    @Override
    protected void setupCommand() {
        setCommand("tebex");
        setDescription("The tebex module.");
        addSubcommand(new SubcommandData("info", "Gets general information regarding the tebex store."));
        addSubcommand(new SubcommandData("payment_get", "payment subcommand")
                .addOption(OptionType.STRING, "id", "The transaction id.", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent e, CommandEventData data) {
        if ("info".equals(e.getSubcommandName())) info(e, data);
        else if ("info".equals(e.getSubcommandName())) payment_get(e, data);
    }

    public void info(SlashCommandInteractionEvent e, CommandEventData data) {
        JSONObject response = getTebexRequest("GET", "information");
        JSONObject acnt = ((JSONObject) response.get("account"));
        JSONObject srv = ((JSONObject) response.get("server"));

        String account = cnf.getInfoCmdAccount()
                .replace("{id}", acnt.get("id").toString())
                .replace("{domain}", acnt.get("domain").toString())
                .replace("{name}", acnt.get("name").toString())
                .replace("{currencyISO}", ((JSONObject) acnt.get("currency")).get("iso_4217").toString())
                .replace("{currencySymbol}", ((JSONObject) acnt.get("currency")).get("symbol").toString())
                .replace("{onlineMode}", ((boolean) acnt.get("online_mode")) ? "Enabled" : "Disabled")
                .replace("{gameType}", acnt.get("game_type").toString())
                .replace("{logEvents}", ((boolean) acnt.get("log_events")) ? "Enabled" : "Disabled");

        String server = cnf.getInfoCmdServer()
                .replace("{id}", srv.get("id").toString())
                .replace("{name}", srv.get("name").toString());


        data.reply(
                EmbedMaker.builder()
                        .authorImg(heart.getGlobalImageUrl())
                        .authorName("Tebex Store Information")
                        .fields(
                                new MessageEmbed.Field[]{
                                        new MessageEmbed.Field("Account", account, true),
                                        new MessageEmbed.Field("Server", server, true)
                                }
                        )
                        .build()
        );
    }

    public void payment_get(SlashCommandInteractionEvent e, CommandEventData data) {
        String id = e.getOption("id").getAsString();
        String header = "payments/" + id;

        JSONObject response = getTebexRequest("GET", header);
        if (response == null) {
            data.replyFailure("No payment found with id: " + id);
            return;
        }


    }



    /*
    Utilities
    */

    @SneakyThrows
    private JSONObject getTebexRequest(String method, String param) {
        return heart.apiMgr().getGeneralRequest(method, "https://plugin.tebex.io/", param, "X-Tebex-Secret", tebexSecret);
    }


}
