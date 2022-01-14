package com.i0dev.discordbot.manager;

import com.google.gson.*;
import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.util.ConsoleColors;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class ConfigManager extends AbstractManager {
    public ConfigManager(Heart heart) {
        super(heart);
    }

    public String ObjectToJson(Object object, boolean pretty) {
        if (pretty)
            return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
    }

    public JsonObject ObjectToJsonObj(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonObject.class);
    }

    public JsonArray ObjectToJsonArr(Object object) {
        return new Gson().fromJson(new Gson().toJson(object), JsonArray.class);
    }

    public Object JsonToObject(JsonElement json, Class<?> clazz) {
        return new Gson().fromJson(new Gson().toJson(json), clazz);
    }

    @SneakyThrows
    public void save(AbstractConfiguration object, String path) {
        Files.write(Paths.get(path), ObjectToJson(object, true).getBytes());
    }

    @SneakyThrows
    public void save(AbstractConfiguration object) {
        Files.write(Paths.get(object.getPath()), ObjectToJson(object, true).getBytes());
    }

    public JsonObject getJsonObject(String path) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }

    public JsonElement getObjectFromInternalPath(String path, JsonObject json) {
        String[] paths = path.split("\\.");
        if (paths.length == 1)
            return json.get(paths[0]);
        JsonObject finalProduct = new JsonObject();
        for (int i = 0; i < paths.length - 1; i++) {
            if (i == 0) finalProduct = json.get(paths[i]).getAsJsonObject();
            else finalProduct = finalProduct.get(paths[i]).getAsJsonObject();
        }
        return finalProduct.get(paths[paths.length - 1]);
    }

    @SneakyThrows
    public AbstractConfiguration load(AbstractConfiguration object, Heart heart) {
        String path = object.getPath();
        if (!heart.getDataFolder().exists()) heart.getDataFolder().mkdir();
        File file = new File(path);
        JsonObject savedObject = getJsonObject(path);
        if (!file.exists()) file.createNewFile();
        String configString = IOUtils.toString(Files.newBufferedReader(Paths.get(path)));
        if ("".equals(configString)) {
            if (file.getName().equals("general.json") || file.getName().equals("commands.json"))
                heart.setFirstStart(true);
            save(object, path);
            return load(object, heart);
        }
        AbstractConfiguration config = new GsonBuilder().serializeNulls().create().fromJson(savedObject, object.getClass());
        if (config == null) throw new IOException("The config file: [" + path + "] is not in valid json format.");
        save(config, path);
        config.setHeart(heart);
        config.setPath(path);
        heart.getLogger().log(Level.INFO, ConsoleColors.GREEN_BOLD + "-> " + ConsoleColors.WHITE_BOLD + "Loaded configuration: " + ConsoleColors.PURPLE_BOLD + object.getClass().getSimpleName() + ConsoleColors.WHITE_BOLD + " from storage." + ConsoleColors.RESET);
        return config;
    }


}
