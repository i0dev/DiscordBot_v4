package com.i0dev.discordbot.object.abs;

import com.i0dev.discordbot.Heart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractConfiguration {

    public transient Heart heart = null;
    public transient String path = "";


    public void initialize() {

    }
    public void deinitialize() {

    }
}
