package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import lombok.Getter;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Getter
public abstract class AbstractManager extends ListenerAdapter {

    protected Heart heart;

    public AbstractManager(Heart heart) {
        this.heart = heart;
    }

    public void initialize() {

    }
    public void deinitialize() {

    }

}
