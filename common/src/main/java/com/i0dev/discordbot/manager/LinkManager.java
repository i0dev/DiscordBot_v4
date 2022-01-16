package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractManager;
import net.dv8tion.jda.internal.utils.IOUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.UUID;

public class LinkManager extends AbstractManager {
    public LinkManager(Heart heart) {
        super(heart);
    }

   public HashMap<String, UUID> pendingLinkMap = new HashMap<>();

    public boolean isOnLinkList(String code) {
        return pendingLinkMap.containsKey(code);
    }
    public boolean isOnLinkList(UUID uuid) {
        return pendingLinkMap.containsValue(uuid);
    }

    public void addToLinkList(String code, UUID uuid) {
        pendingLinkMap.put(code, uuid);
    }

    public void removeFromLinkList(String code) {
        pendingLinkMap.remove(code);
    }

    public UUID getUUIDFromCode(String code) {
        return pendingLinkMap.get(code);
    }

    public String generateCode(int length) {
        String code = RandomStringUtils.random(length, true, true);
        if (pendingLinkMap.containsKey(code))
            return generateCode(length);
        return code;
    }


}
