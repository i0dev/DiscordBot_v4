package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

public class APIManager extends AbstractManager {
    public APIManager(Heart heart) {
        super(heart);
    }

    public JSONObject getAuthentication(String id) {
        return getGeneralRequest("GET", "https://api.i0dev.com/auth/", id, "secret", "temp");
    }

    private JSONObject getGeneralRequest(String method, String url, String param, String HeaderKey, String HeaderValue) {
        try {
            StringBuilder result = new StringBuilder();
            HttpURLConnection conn = (HttpURLConnection) new URL(url + param).openConnection();
            conn.setRequestMethod(method);
            if (!HeaderKey.equals("") && !HeaderValue.equals("")) {
                conn.setRequestProperty(HeaderKey, HeaderValue);
            }
            if (conn.getResponseCode() == 403) {
                return new JSONObject();
            }
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            conn.disconnect();
            if (result.toString().contains("�")) return null;
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (ParseException | IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private JSONObject getGeneralRequest(String method, String url, String param) {
        return getGeneralRequest(method, url, param, "", "");
    }

    private JSONObject getTebexRequest(String method, String param) {
        return getGeneralRequest(method, "https://plugin.tebex.io/", param, "X-Tebex-Secret", "");
    }

    public JSONObject lookupTransaction(String transID) {
        return getTebexRequest("GET", "payments/" + transID);
    }

    public JSONObject lookupPackage(String packageID) {
        return getTebexRequest("GET", "package/" + packageID);
    }

    public JSONObject MinecraftServerLookup(String ipAddress) throws IOException {
        return getGeneralRequest("GET", "https://api.mcsrvstat.us/2/", ipAddress);
    }

    public JSONObject getInformation() {
        return getTebexRequest("GET", "information");
    }

    private JSONObject convertToJSON(HttpURLConnection connection) {
        try {
            StringBuilder result = new StringBuilder();
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject convertToJSON(InputStream stream) {
        try {
            StringBuilder result = new StringBuilder();
            String line;
            BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
            while ((line = rd.readLine()) != null) result.append(line);
            rd.close();
            return (JSONObject) new JSONParser().parse(result.toString());
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public JSONObject createGiftcard(String amt, String note) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://plugin.tebex.io/gift-cards").openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("X-Tebex-Secret", "");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            String o;
            if (note.equals("")) {
                o = "{\"amount\": \"" + amt + "\"}";
            } else {
                o = "{\"amount\": " + amt + ",\"note\":\"" + note + "\"}";
            }
            conn.getOutputStream().write(o.getBytes());
            if (conn.getResponseCode() == 400) {
                JSONObject ob = new JSONObject();
                ob.put("error", "2");
                return ob;
            }
            return convertToJSON(conn);

        } catch (MalformedURLException | ProtocolException ignored) {
            ignored.printStackTrace();
            return new JSONObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public JSONObject lookupUser(String UUID) {
        return getTebexRequest("GET", "user/" + UUID);
    }

    public UUID getUUIDFromIGN(String ign) {
        JSONObject req = getGeneralRequest("GET", "https://api.mojang.com/users/profiles/minecraft/", ign);
        if (req == null) return null;
        return convertUUID(req.get("id").toString());
    }

    public String getIGNFromUUID(String uuid) {
        try {
            return getGeneralRequest("GET", "https://sessionserver.mojang.com/session/minecraft/profile/", uuid).get("name").toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    public void refreshAPICache(String uuid) {
        getGeneralRequest("GET", "https://crafatar.com/renders/body/", uuid);
    }

    private final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public UUID convertUUID(String uuid) {
        return UUID.fromString(UUID_FIX.matcher(uuid.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }


}
