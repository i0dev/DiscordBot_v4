package com.i0dev.discordbot.bukkit.object;

import com.i0dev.discordbot.Heart;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractCommand implements CommandExecutor, TabExecutor {

    protected Heart heart;

    // For tab completion
    protected List<String> blank = new ArrayList<>();
    protected List<String> players = null;

    String command;

    public AbstractCommand(Heart heart, String command) {
        this.heart = heart;
        this.command = command;
    }

    public AbstractCommand(Heart heart) {
        this.heart = heart;
        String name = getClass().getSimpleName().toLowerCase();
        this.command = name.substring(2);
    }

    public abstract void execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equals(this.command)) return false;
        execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equals(this.command)) return null;
        return tabComplete(sender, args);
    }

    public List<String> tabCompleteHelper(String arg, Collection<String> options) {
        if (arg.equalsIgnoreCase("") || arg.equalsIgnoreCase(" "))
            return new ArrayList<>(options);
        else
            return options.stream().filter(s -> s.toLowerCase().startsWith(arg.toLowerCase())).collect(Collectors.toList());
    }
}