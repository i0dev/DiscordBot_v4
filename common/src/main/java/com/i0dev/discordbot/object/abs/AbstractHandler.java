package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import lombok.Getter;

@Getter
public abstract class AbstractHandler extends AbstractManager {

    protected Heart heart;

    public AbstractHandler(Heart heart) {
        super(heart);
    }

    public void initialize() {

    }

    public void deinitialize() {

    }

}
