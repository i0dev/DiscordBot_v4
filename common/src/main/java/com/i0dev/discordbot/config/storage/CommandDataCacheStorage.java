package com.i0dev.discordbot.config.storage;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommandDataCacheStorage extends AbstractConfiguration {

    public CommandDataCacheStorage(Heart heart, String path) {
        super(heart, path);
    }

    List<String> cache = new ArrayList<>();


    public String getCmdData(String id) {
        return cache.stream().filter(s -> s.contains("\"name\":\"" + id + "\",")).findAny().orElse(null);
    }

    public void removeCmdDataByID(String id) {
        cache.remove(getCmdData(id));
        heart.cnfMgr().save(this, path);
    }

}
