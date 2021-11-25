package com.i0dev.discordbot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.i0dev.discordbot.command.*;
import com.i0dev.discordbot.command.fun.CmdCoinflip;
import com.i0dev.discordbot.command.general.*;
import com.i0dev.discordbot.command.moderation.*;
import com.i0dev.discordbot.config.*;
import com.i0dev.discordbot.manager.*;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.Pair;
import com.i0dev.discordbot.object.StartupTag;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.abs.AbstractTask;
import com.i0dev.discordbot.object.abs.DiscordCommand;
import com.i0dev.discordbot.task.TaskExecuteRoleQueue;
import com.i0dev.discordbot.task.TaskUpdateDiscordActivity;
import com.i0dev.discordbot.util.ConsoleColors;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

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

    public static final String VERSION = "4.0.0";
    public boolean firstStart = false;
    public Object pluginInstance;
    JDA jda;
    Logger logger;
    List<DiscordCommand> commands = new ArrayList<>();
    List<AbstractManager> managers = new ArrayList<>();
    List<AbstractConfiguration> configs = new ArrayList<>();
    List<AbstractTask> tasks = new ArrayList<>();
    ScheduledExecutorService executorService;

    public Heart(List<StartupTag> tags, Logger logger, Object pluginInstance) {
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
        // Add to the lists.
        managers.addAll(Arrays.asList(
                new DiscordCommandManager(this),
                new ConfigManager(this),
                new GeneralManager(this),
                new APIManager(this),
                new SQLManager(this),
                new MessageManager(this)
        ));
        configs.addAll(Arrays.asList(
                new GeneralConfig(this, getDataFolder() + "/general.json"),
                new MiscConfig(this, getDataFolder() + "/misc.json"),
                new CommandDataCacheStorage(this, getDataFolder() + "/cmdCache.json"),
                new SuggestionStorage(this, getDataFolder() + "/suggestions.json"),
                new TicketStorage(this, getDataFolder() + "/tickets.json"),
                new CommandConfig(this, getDataFolder() + "/commands.json")
        ));
        managers.forEach(AbstractManager::initialize);
        registerConfigs();
        createJDA();
        managers.forEach(jda::addEventListener);
        addCommands();
        tasks.addAll(Arrays.asList(
                new TaskExecuteRoleQueue(this),
                new TaskUpdateDiscordActivity(this)

        ));
        executorService = Executors.newScheduledThreadPool((int) (tasks.size() / 1.333333));
        tasks.forEach(AbstractTask::initialize);
        startTasks();
        sqlMgr().makeTable(DiscordUser.class);
        sqlMgr().absenceCheck(DiscordUser.class);
        logger.log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + "i0dev DiscordBot " + ConsoleColors.GREEN_BOLD + "Successfully" + ConsoleColors.WHITE_BOLD + " Loaded!" + ConsoleColors.RESET);
    }


    public void addCommands() {
        commands.forEach(DiscordCommand::deinitialize);
        commands.clear();
        commands.addAll(Arrays.asList(
                new CmdHelp(this, cmdCnf().getHelp()),
                new CmdMembers(this, cmdCnf().getMembers()),
                new CmdRoleInfo(this, cmdCnf().getRoleInfo()),
                new CmdCoinflip(this, cmdCnf().getCoinflip()),
                new CmdProfile(this, cmdCnf().getProfile()),
                new CmdRoles(this, cmdCnf().getRoles()),
                new CmdBan(this, cmdCnf().getBan()),
                new CmdAnnounce(this, cmdCnf().getAnnounce()),
                new CmdTicket(this, cmdCnf().getTicket()),
                new CmdKick(this, cmdCnf().getKick()),
                new CmdReload(this, cmdCnf().getReload()),
                new CmdBotInfo(this, cmdCnf().getBotInfo()),
                new CmdChangelog(this, cmdCnf().getChangelog()),
                new CmdDirectMessage(this, cmdCnf().getDirectMessage()),
                new CmdVerifyPanel(this, cmdCnf().getVerifyPanel()),
                new CmdPrune(this, cmdCnf().getPrune()),
                new CmdServerInfo(this, cmdCnf().getServerInfo()),
                new CmdServerLookup(this, cmdCnf().getServerLookup()),
                new CmdBlacklist(this, cmdCnf().getBlacklist()),
                new CmdSuggestion(this, cmdCnf().getSuggestion()),
                new CmdInvite(this, cmdCnf().getInvite()),
                new CmdMute(this, cmdCnf().getMute()),
                new CmdAvatar(this, cmdCnf().getAvatar())
        ));
        jda.getRegisteredListeners().forEach(jda::removeEventListener);
        commands.forEach(command -> {
            command.initialize();
            /* TEMPORARY */
            jda.getGuilds().get(0).upsertCommand(command.toData()).queue();
            if (command.isRegisterListener()) jda.addEventListener(command);
        });
        cnfMgr().save(cmdCacheStrg(), cmdCacheStrg().path);
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
            if (!cnfMgr().ObjectToJson(discordCommand.toData(), false).equalsIgnoreCase(cmdCacheStrg().getCmdData(discordCommand.getCommand()))) {
                toUpdate.add(discordCommand.toData());
                cmdCacheStrg().removeCmdDataByID(discordCommand.getCommand());
                cmdCacheStrg().getCache().add(cnfMgr().ObjectToJson(discordCommand.toData(), false));
                cnfMgr().save(cmdCacheStrg(), cmdCacheStrg().path);
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
            cmdCacheStrg().getCache().add(cnfMgr().ObjectToJson(discordCommand.toData(), false));
            cnfMgr().save(cmdCacheStrg(), cmdCacheStrg().path);
        }
    }

    public void shutdown() {
        configs.forEach(AbstractConfiguration::deinitialize);
        managers.forEach(AbstractManager::deinitialize);
        commands.forEach(DiscordCommand::deinitialize);
        tasks.forEach(AbstractTask::deinitialize);
        jda.shutdown();
        executorService.shutdown();
        logger.log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + "i0dev DiscordBot " + ConsoleColors.GREEN_BOLD + "Successfully" + ConsoleColors.WHITE_BOLD + " Shutdown." + ConsoleColors.RESET);
    }

    @SneakyThrows
    public void createJDA() {
        jda = JDABuilder.create(gCnf().getBotToken(), EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setContextEnabled(true)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .build()
                .awaitReady();
    }

    public void registerConfigs() {
        ArrayList<Pair<AbstractConfiguration, AbstractConfiguration>> toReplace = new ArrayList<>();
        configs.forEach(abstractConfiguration -> toReplace.add(new Pair<>(abstractConfiguration, cnfMgr().load(abstractConfiguration, this))));
        toReplace.forEach(pairs -> {
            configs.remove(pairs.getKey());
            configs.add(pairs.getValue());
        });
    }

    // Utilities

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
        gCnf().getVerifiedGuilds().forEach(guildId -> {
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

    public JsonArray listToJsonArr(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonArray.class);
    }

    // Color Shortcuts
    public String normalColor() {
        return gCnf().getNormalColor();
    }

    public String failureColor() {
        return gCnf().getFailureColor();
    }

    public String successColor() {
        return gCnf().getSuccessColor();
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

    public <T> T getConfig(Class<T> clazz) {
        return (T) configs.stream().filter(config -> config.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T> T getTask(Class<T> clazz) {
        return (T) tasks.stream().filter(config -> config.getClass().equals(clazz)).findFirst().orElse(null);
    }

    // Shortcuts for getting managers

    public GeneralConfig gCnf() {
        return getConfig(GeneralConfig.class);
    }

    public MiscConfig mscCnf() {
        return getConfig(MiscConfig.class);
    }

    public CommandDataCacheStorage cmdCacheStrg() {
        return getConfig(CommandDataCacheStorage.class);
    }

    public ConfigManager cnfMgr() {
        return getManager(ConfigManager.class);
    }

    public DiscordCommandManager dscCmdMgr() {
        return getManager(DiscordCommandManager.class);
    }

    public APIManager apiMgr() {
        return getManager(APIManager.class);
    }

    public CommandConfig cmdCnf() {
        return getConfig(CommandConfig.class);
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
