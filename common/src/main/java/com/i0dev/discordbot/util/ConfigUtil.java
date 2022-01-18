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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.i0dev.discordbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.logging.Level;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

public class ConfigUtil {

    public static String ObjectToJson(AbstractConfiguration object) {
        return (new GsonBuilder()).setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().toJson((new JsonParser()).parse((new Gson()).fromJson((new Gson()).toJson(object), JsonObject.class).toString()));
    }

    public static String ObjectToJson(Object object, boolean pretty) {
        if (pretty)
            return new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create().toJson(new JsonParser().parse(new Gson().fromJson(new Gson().toJson(object), JsonObject.class).toString()));
    }

    public static JsonObject ObjectToJsonObj(Object object) {
        return (new Gson()).fromJson((new Gson()).toJson(object), JsonObject.class);
    }

    public static JsonArray ObjectToJsonArr(Object object) {
        return (new Gson()).fromJson((new Gson()).toJson(object), JsonArray.class);
    }

    public static Object JsonToObject(JsonElement json, Class<?> clazz) {
        return (new Gson()).fromJson((new Gson()).toJson(json), clazz);
    }

    @SneakyThrows
    public static void save(AbstractConfiguration object, String path) {
        Files.write(Paths.get(path), ObjectToJson(object).getBytes());
    }

    @SneakyThrows
    public static void save(AbstractConfiguration object) {
        Files.write(Paths.get(object.getPath()), ObjectToJson(object).getBytes());
    }

    @SneakyThrows
    public static JsonObject getJsonObject(String path) {
        return (new Gson()).fromJson(Files.newBufferedReader(Paths.get(path)), JsonObject.class);
    }

    public static JsonElement getObjectFromInternalPath(String path, JsonObject json) {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return json.get(paths[0]);
        } else {
            JsonObject finalProduct = new JsonObject();

            for (int i = 0; i < paths.length - 1; ++i) {
                if (i == 0) {
                    finalProduct = json.get(paths[i]).getAsJsonObject();
                } else {
                    finalProduct = finalProduct.get(paths[i]).getAsJsonObject();
                }
            }

            return finalProduct.get(paths[paths.length - 1]);
        }
    }

    @SneakyThrows
    public static AbstractConfiguration load(AbstractConfiguration object, Heart heart) {
        String path = object.getPath();
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }

        JsonObject savedObject = getJsonObject(path);
        if (!heart.getDataFolder().exists()) {
            heart.getDataFolder().mkdir();
        }

        String configString = IOUtils.toString(Files.newBufferedReader(Paths.get(path)));
        if ("".equals(configString)) {
            save(object, path);
            return load(object, heart);
        } else {
            AbstractConfiguration config = (new Gson()).fromJson(savedObject, object.getClass());
            if (config == null) {
                throw new IOException("The config file: [" + path + "] is not in valid json format.");
            } else {
                save(config, path);
                config.setHeart(heart);
                config.setPath(path);
                heart.getLogger().log(Level.INFO, "\u001b[1;32m-> \u001b[1;37mLoaded configuration: \u001b[1;35m" + object.getClass().getSimpleName() + "\u001b[1;37m" + " from storage." + "\u001b[0m");
                return config;
            }
        }
    }
}
