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

package com.i0dev.discordbot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.i0dev.discordbot.command.*;
import com.i0dev.discordbot.command.aliases.CmdClose;
import com.i0dev.discordbot.command.aliases.CmdRename;
import com.i0dev.discordbot.command.fun.CmdCoinflip;
import com.i0dev.discordbot.command.gamemode.CmdFactions;
import com.i0dev.discordbot.command.gamemode.CmdPrison;
import com.i0dev.discordbot.command.gamemode.CmdSkyblock;
import com.i0dev.discordbot.command.general.*;
import com.i0dev.discordbot.command.moderation.*;
import com.i0dev.discordbot.config.*;
import com.i0dev.discordbot.config.configs.MovementConfig;
import com.i0dev.discordbot.config.configs.PermissionConfig;
import com.i0dev.discordbot.config.configs.TebexConfig;
import com.i0dev.discordbot.config.configs.TicketConfig;
import com.i0dev.discordbot.config.storage.CommandDataCacheStorage;
import com.i0dev.discordbot.config.storage.GiveawayStorage;
import com.i0dev.discordbot.config.storage.SuggestionStorage;
import com.i0dev.discordbot.config.storage.TicketStorage;
import com.i0dev.discordbot.manager.*;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Pair;
import com.i0dev.discordbot.object.StartupTag;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.abs.AbstractTask;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.object.builder.EmbedMaker;
import com.i0dev.discordbot.task.*;
import com.i0dev.discordbot.util.ConfigUtil;
import com.i0dev.discordbot.util.ConsoleColors;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class Heart {

    public static final String VERSION = "4.0.8";
    public boolean firstStart = false;
    public Object pluginInstance;
    JDA jda;
    Logger logger;
    List<DiscordCommand> commands = new ArrayList<>();
    List<AbstractManager> managers = new ArrayList<>();
    List<AbstractConfiguration> configs = new ArrayList<>();
    List<AbstractTask> tasks = new ArrayList<>();
    ScheduledExecutorService executorService;

    @SneakyThrows
    public Heart(List<StartupTag> tags, Logger logger, Object pluginInstance) {
        Class.forName("org.slf4j.Logger");
        this.tags = tags;
        this.logger = logger;
        this.pluginInstance = pluginInstance;
        logger.log(Level.INFO, "");
        logger.log(Level.INFO, ConsoleColors.WHITE_BOLD + "==============================" + ConsoleColors.RESET);
        logger.log(Level.INFO, ConsoleColors.WHITE_BOLD + "|                            |" + ConsoleColors.RESET);
        logger.log(Level.INFO, ConsoleColors.WHITE_BOLD + "|     " + ConsoleColors.PURPLE_BOLD + "i0dev Discord Bot" + ConsoleColors.WHITE_BOLD + "      |" + ConsoleColors.RESET);
        logger.log(Level.INFO, ConsoleColors.WHITE_BOLD + "|       " + ConsoleColors.GREEN_BOLD + "Version " + VERSION + ConsoleColors.WHITE_BOLD + "        |" + ConsoleColors.RESET);
        logger.log(Level.INFO, ConsoleColors.WHITE_BOLD + "|         " + ConsoleColors.BLUE_BOLD + "Loading..." + ConsoleColors.WHITE_BOLD + "         |" + ConsoleColors.RESET);
        logger.log(Level.INFO, ConsoleColors.WHITE_BOLD + "|                            |" + ConsoleColors.RESET);
        logger.log(Level.INFO, ConsoleColors.WHITE_BOLD + "==============================" + ConsoleColors.RESET);
        logger.log(Level.INFO, "");
        startup();
    }

    @SneakyThrows
    public void startup() {
        getDataFolder().mkdir();
        new File(getDataFolder() + "/storage/").mkdir();
        new File(getDataFolder() + "/configs/").mkdir();


        managers.addAll(Arrays.asList(
                new DiscordCommandManager(this),
                new GeneralManager(this),
                new APIManager(this),
                new SQLManager(this),
                new MessageManager(this),
                new LinkManager(this),
                new InviteManager(this),
                new AutoModManager(this)
        ));
        configs.addAll(Arrays.asList(
                new GeneralConfig(this, getDataFolder() + "/config.json"),

                new CommandDataCacheStorage(this, getDataFolder() + "/storage/cmdCache.json"),
                new SuggestionStorage(this, getDataFolder() + "/storage/suggestions.json"),
                new TicketStorage(this, getDataFolder() + "/storage/tickets.json"),
                new GiveawayStorage(this, getDataFolder() + "/storage/giveaways.json"),

                new PermissionConfig(this, getDataFolder() + "/configs/permissionConfig.json"),
                new MovementConfig(this, getDataFolder() + "/configs/movementConfig.json"),
                new AutoModConfig(this, getDataFolder() + "/configs/autoModConfig.json"),
                new TicketConfig(this, getDataFolder() + "/configs/ticketConfig.json"),
                new TebexConfig(this, getDataFolder() + "/configs/tebexConfig.json")

        ));
        managers.forEach(AbstractManager::initialize);
        registerConfigs();

        if (cnf().getBotToken().equalsIgnoreCase("Your Token Here")) {
            logSpecial("No token found in config.json. Please add your token to the file and restart the bot.");
            shutdown();
            return;
        }

        sqlMgr().connect();
        createJDA();
        managers.forEach(jda::addEventListener);
        addCommands();
        tasks.addAll(Arrays.asList(
                new TaskExecuteRoleQueue(this),
                new TaskExecuteNicknameQueue(this),
                new TaskUpdateDiscordActivity(this),
                new TaskRunTicketLogQueue(this),
                new TaskExecuteGiveaways(this),
                new TaskAutoGiveRoles(this),
                new TaskUpdateMemberCounter(this),
                new TaskUpdateUsersNickname(this),
                new TaskVerifyAuthentication(this),
                new TaskReconnectSQL(this)

        ));
        executorService = Executors.newScheduledThreadPool((int) (tasks.size() / 1.333333));
        tasks.forEach(AbstractTask::initialize);
        startTasks();
        sqlMgr().makeTable(DiscordUser.class);
        sqlMgr().absenceCheck(DiscordUser.class);
        logger.log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + "i0dev DiscordBot " + ConsoleColors.GREEN_BOLD + "Successfully" + ConsoleColors.WHITE_BOLD + " Loaded!" + ConsoleColors.RESET);
        if (!isVerifiedBot()) {
            shutdownBotNotVerified();
            return;
        }
        logSpecial("Successfully verified bot with authentication servers.");
    }

    public void shutdownBotNotVerified() {
        logger.log(Level.SEVERE, ConsoleColors.RED + "DiscordBot failed to authenticate with i0dev api. Shutting down..." + ConsoleColors.RESET);
        shutdown();
    }

    public boolean isVerifiedBot() {
        JSONObject access = apiMgr().getAuthentication(jda.getSelfUser().getId());
        return (boolean) access.get("access");
    }


    public void addCommands() {
        commands.addAll(Arrays.asList(
                new CmdHelp(this),
                new CmdMembers(this),
                new CmdRoleInfo(this),
                new CmdCoinflip(this),
                new CmdProfile(this),
                new CmdRoles(this),
                new CmdBan(this),
                new CmdAnnounce(this),
                new CmdTicket(this),
                new CmdKick(this),
                new CmdReload(this),
                new CmdBotInfo(this),
                new CmdChangelog(this),
                new CmdDirectMessage(this),
                new CmdVerifyPanel(this),
                new CmdPrune(this),
                new CmdServerInfo(this),
                new CmdServerLookup(this),
                new CmdBlacklist(this),
                new CmdSuggestion(this),
                new CmdInvite(this),
                new CmdMute(this),
                new CmdAvatar(this),
                new CmdLink(this),
                new CmdGiveaway(this),
                new CmdFactions(this),
                new CmdCommandInfo(this),
                new CmdMovement(this),
                new CmdClose(this),
                new CmdRename(this),
                new CmdPrison(this),
                new CmdSkyblock(this),
                new CmdTebex(this),
                new CmdIP(this)

        ));
        commands.forEach(command -> {
            command.initialize();
            /* TEMPORARY */
            getAllowedGuilds().forEach(guild -> guild.upsertCommand(command.toData()).queue());
            if (command.isRegisterListener()) jda.addEventListener(command);
        });
        ConfigUtil.save(cmdCacheStrg(), cmdCacheStrg().path);
        //  upsertCommands();
        //  updateCommands();
    }

    public void startTasks() {
        for (AbstractTask task : getTasks()) {
            executorService.scheduleAtFixedRate(task, task.getInitialDelay(), task.getInterval(), task.getTimeUnit());
        }
    }

    public void updateCommands() {
        List<CommandData> toUpdate = new ArrayList<>();
        for (DiscordCommand discordCommand : commands) {
            if (discordCommand == null) return;
            if (!ConfigUtil.ObjectToJson(discordCommand.toData(), false).equalsIgnoreCase(cmdCacheStrg().getCmdData(discordCommand.getCommand()))) {
                toUpdate.add(discordCommand.toData());
                cmdCacheStrg().removeCmdDataByID(discordCommand.getCommand());
                cmdCacheStrg().getCache().add(ConfigUtil.ObjectToJson(discordCommand.toData(), false));
                ConfigUtil.save(cmdCacheStrg(), cmdCacheStrg().path);
                logger.log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + "Updated command: " + ConsoleColors.PURPLE_BOLD + discordCommand.getCommand() + ConsoleColors.RESET);
            }
        }
        jda.updateCommands().addCommands(toUpdate).queue();
    }

    public void upsertCommands() {
        for (DiscordCommand discordCommand : commands) {
            if (cmdCacheStrg().getCmdData(discordCommand.getCommand()) != null) continue;
            jda.upsertCommand(discordCommand.toData()).queue();
            logger.log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + "Sent a request to add a new command: " + ConsoleColors.PURPLE_BOLD + discordCommand.getCommand() + ConsoleColors.RESET);
            cmdCacheStrg().getCache().add(ConfigUtil.ObjectToJson(discordCommand.toData(), false));
            ConfigUtil.save(cmdCacheStrg(), cmdCacheStrg().path);
        }
    }

    public void shutdown() {
        shutdown(true);
    }

    @SneakyThrows
    public void shutdown(boolean exit) {
        configs.forEach(AbstractConfiguration::deinitialize);
        managers.forEach(AbstractManager::deinitialize);
        commands.forEach(DiscordCommand::deinitialize);
        tasks.forEach(AbstractTask::deinitialize);
        commands.clear();
        managers.clear();
        configs.clear();
        tasks.clear();
        if (jda != null) jda.shutdown();
        if (executorService != null) executorService.shutdown();
        if (sqlMgr() != null && sqlMgr().getConnection() != null) sqlMgr().getConnection().close();
        logger.log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + "i0dev DiscordBot " + ConsoleColors.GREEN_BOLD + "Successfully" + ConsoleColors.WHITE_BOLD + " Shutdown." + ConsoleColors.RESET);
        if (tags.contains(StartupTag.STANDALONE) && exit) System.exit(0);
    }

    @SneakyThrows
    public void createJDA() {
        jda = JDABuilder.create(cnf().getBotToken(), EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.fromKey(cnf().getBotStatus()))
                .setContextEnabled(true)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .build()
                .awaitReady();

    }

    public void registerConfigs() {
        ArrayList<Pair<AbstractConfiguration, AbstractConfiguration>> toReplace = new ArrayList<>();
        configs.forEach(abstractConfiguration -> toReplace.add(new Pair<>(abstractConfiguration, ConfigUtil.load(abstractConfiguration, this))));
        toReplace.forEach(pairs -> {
            configs.remove(pairs.getKey());
            configs.add(pairs.getValue());
        });
    }

    // Utilities
    public List<String> getAllCommandIDS() {
        List<String> ret = new ArrayList<>();
        commands.forEach(command -> {
            if (command.getSubCommands().isEmpty()) ret.add(command.getCommand());
            else
                command.getSubCommands().forEach(subCommand -> ret.add(command.getCommand() + "_" + subCommand.getName()));
        });
        return ret;
    }


    public File getDataFolder() {
        if ((isBungee() || isBukkit()) && isPlugin()) return new File("plugins/DiscordBot");
        if (isStandalone()) return new File("DiscordBot");
        return null;
    }

    public String getGlobalImageUrl() {
        return jda.getSelfUser().getEffectiveAvatarUrl();
    }

    public List<Guild> getAllowedGuilds() {
        List<Guild> ret = new ArrayList<>();
        cnf().getVerifiedGuilds().forEach(guildId -> {
            Guild guild = jda.getGuildById(guildId);
            if (guild != null) ret.add(guild);
        });
        return ret;
    }

    public void logSpecial(String message) {
        logger.log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + message + ConsoleColors.RESET);
    }

    public void logDebug(String message) {
        logger.log(Level.INFO, ConsoleColors.YELLOW + "-> " + ConsoleColors.WHITE_BOLD + message + ConsoleColors.RESET);
    }

    public void logDiscord(EmbedMaker embedMaker) {
        TextChannel channel = jda.getTextChannelById(cnf().getLogsChannel());
        if (channel == null) return;
        channel.sendMessageEmbeds(msgMgr().createMessageEmbed(embedMaker)).queue();
    }

    public JsonArray listToJsonArr(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonArray.class);
    }

    // Color Shortcuts
    public String normalColor() {
        return cnf().getNormalColor();
    }

    public String failureColor() {
        return cnf().getFailureColor();
    }

    public String successColor() {
        return cnf().getSuccessColor();
    }

    // START Tags
    List<StartupTag> tags;

    public boolean isBungee() {
        return tags.contains(StartupTag.BUNGEE);
    }

    public boolean isBukkit() {
        return tags.contains(StartupTag.BUKKIT);
    }

    public boolean isPlugin() {
        return tags.contains(StartupTag.PLUGIN);
    }

    public boolean isStandalone() {
        return tags.contains(StartupTag.STANDALONE);
    }
    // END Tags

    public <T> T getManager(Class<T> clazz) {
        return (T) managers.stream().filter(manager -> manager.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T> T getCommand(Class<T> clazz) {
        return (T) commands.stream().filter(command -> command.getClass().equals(clazz)).findFirst().orElse(null);
    }


    public <T> T getConfig(Class<T> clazz) {
        return (T) configs.stream().filter(config -> config.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T> T getTask(Class<T> clazz) {
        return (T) tasks.stream().filter(config -> config.getClass().equals(clazz)).findFirst().orElse(null);
    }

    // Shortcuts for getting managers

    public GeneralConfig cnf() {
        return getConfig(GeneralConfig.class);
    }

    public CommandDataCacheStorage cmdCacheStrg() {
        return getConfig(CommandDataCacheStorage.class);
    }

    public DiscordCommandManager dscCmdMgr() {
        return getManager(DiscordCommandManager.class);
    }

    public APIManager apiMgr() {
        return getManager(APIManager.class);
    }


    public GeneralManager genMgr() {
        return getManager(GeneralManager.class);
    }

    public SQLManager sqlMgr() {
        return getManager(SQLManager.class);
    }

    public MessageManager msgMgr() {
        return getManager(MessageManager.class);
    }

}
