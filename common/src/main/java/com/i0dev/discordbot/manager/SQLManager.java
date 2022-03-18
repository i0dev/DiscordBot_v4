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

package com.i0dev.discordbot.manager;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.DiscordUser;
import com.i0dev.discordbot.object.abs.AbstractManager;
import com.i0dev.discordbot.object.config.DatabaseInformation;
import com.i0dev.discordbot.util.ConsoleColors;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class SQLManager extends AbstractManager {
    public SQLManager(Heart heart) {
        super(heart);
    }

    public Connection connection = null;

    @Override
    public void initialize() {
        if (connection != null) deinitialize();
    }

    @SneakyThrows
    @Override
    public void deinitialize() {
        if (connection != null) connection.close();
        connection = null;
    }

    @SneakyThrows
    public void runQuery(String sql) {
        connection.prepareStatement(sql).execute();
    }

    @SneakyThrows
    public ResultSet runQueryWithResult(String sql) {
        return connection.prepareStatement(sql).executeQuery();
    }

    @SneakyThrows
    public void connect() {
        Class.forName("org.sqlite.JDBC");
        DatabaseInformation db = heart.cnf().getDatabase();
        String database = db.getName();
        if (db.isEnabled()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + db.getAddress() + ":" + db.getPort() + "/" + database);
            config.setUsername(db.getUsername());
            config.setPassword(db.getPassword());
            config.setIdleTimeout(heart.cnf().getDatabase().getIdleTimeout());
            config.setMaxLifetime(heart.cnf().getDatabase().getMax_lifetime());
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            HikariDataSource ds = new HikariDataSource(config);
            connection = ds.getConnection();
            heart.logSpecial("Connected to Hikari MySQL database: " + ConsoleColors.PURPLE_BOLD + database);
        } else {
            database = heart.getDataFolder() + "/DiscordBot.db";
            String url = "jdbc:sqlite:" + database;
            connection = DriverManager.getConnection(url);
            heart.logSpecial("Connected to SQLite database: " + ConsoleColors.PURPLE_BOLD + "DiscordBot.db");
        }
    }

    @SneakyThrows
    public List<String> getColumns(String table) {
        if (heart.cnf().getDatabase().isEnabled()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SHOW COLUMNS FROM " + table + ";");
            ResultSet set = preparedStatement.executeQuery();
            List<String> columns = new ArrayList<>();
            while (set.next()) {
                columns.add(set.getString("field"));
            }
            preparedStatement.close();
            set.close();
            return columns;
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement("PRAGMA table_info(" + table + ");");
            ResultSet set = preparedStatement.executeQuery();
            List<String> columns = new ArrayList<>();
            while (set.next()) {
                columns.add(set.getString("name"));
            }
            preparedStatement.close();
            set.close();
            return columns;
        }
    }

    @SneakyThrows
    public void absenceCheck(Class<?> clazz) {
        String table = clazz.getSimpleName();
        List<String> columns = getColumns(table);

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            if (columns.contains(field.getName())) continue;
            String type;
            switch (field.getType().getName()) {
                case "java.lang.Long":
                case "long":
                    type = "BIGINT NOT NULL DEFAULT 0";
                    break;
                case "java.lang.Double":
                case "double":
                    type = "DOUBLE(16,10) NOT NULL DEFAULT 0";
                    break;
                case "java.lang.String":
                    type = "VARCHAR(300) NOT NULL DEFAULT 0";
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    type = "BIT NOT NULL DEFAULT 0";
                    break;
                default:
                    return;
            }
            heart.logSpecial("Creating column " + ConsoleColors.PURPLE_BOLD + field.getName() + ConsoleColors.WHITE_BOLD + " in table " + ConsoleColors.PURPLE_BOLD + table + ConsoleColors.WHITE_BOLD + " with type " + ConsoleColors.PURPLE_BOLD + type);
            String query = "ALTER TABLE " + table + " ADD COLUMN " + field.getName() + " " + type + ";";
            Statement statement = connection.createStatement();
            System.out.println("Query: " + query);
            statement.execute(query);
        }

        columns = getColumns(table);

        List<String> fields = new ArrayList<>();
        Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isTransient(field.getModifiers())).forEach(field -> fields.add(field.getName()));
        for (String column : columns) {
            if (fields.contains(column)) continue;
            heart.logSpecial("Removing column [" + ConsoleColors.PURPLE_BOLD + column + ConsoleColors.WHITE_BOLD + "] from " + ConsoleColors.PURPLE_BOLD + table + ConsoleColors.WHITE_BOLD + " during an absence check.");
            String query = "ALTER TABLE " + table + " DROP COLUMN " + column + ";";
            Statement statement = connection.createStatement();
            System.out.println("Query: " + query);
            statement.execute(query);
        }
        columns.clear();
        fields.clear();
    }


    public List<String> getColumnLines(Field field) {
        String type = field.getType().getTypeName();
        String name = field.getName();
        List<String> ret = new ArrayList<>();
        switch (type) {
            case "java.lang.Long":
            case "long":
                ret.add("`" + name + "` BIGINT NOT NULL DEFAULT 0,");
                break;
            case "java.lang.Double":
            case "double":
                ret.add("`" + name + "` DOUBLE(16,10) NOT NULL DEFAULT 0,");
                break;
            case "java.lang.String":
                ret.add("`" + name + "` VARCHAR(300) NOT NULL DEFAULT '',");
                break;
            case "java.lang.Boolean":
            case "boolean":
                ret.add("`" + name + "` BIT NOT NULL DEFAULT 0,");
                break;
        }
        return ret;
    }

    @SneakyThrows
    public boolean objectExists(String table, String key, String value) {
        String query = "SELECT * FROM " + table + " WHERE " + key + " = " + value;
        ResultSet resultSet = connection.prepareStatement(query).executeQuery();
        return resultSet.next();
    }

    @SneakyThrows
    public void insertToTable(Object object) {
        Class<?> clazz = object.getClass();
        StringBuilder toQ = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            String type = field.getType().getTypeName();
            switch (type) {
                case "java.lang.Long":
                case "long":
                    toQ.append(field.getLong(object)).append(",");
                    break;
                case "java.lang.Double":
                case "double":
                    toQ.append(field.getDouble(object)).append(",");
                    break;
                case "java.lang.String":
                    toQ.append("'").append(field.get(object)).append("',");
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    toQ.append((field.getBoolean(object) ? 1 : 0)).append(",");
                    break;
            }
        }
        connection.createStatement().execute("INSERT INTO " + clazz.getSimpleName() + " VALUES(" + toQ.substring(0, toQ.length() - 1) + ");");
    }

    @SneakyThrows
    public Object getObject(String key, Object value, Class<?> clazz) {
        String val;
        switch (value.getClass().getSimpleName()) {
            case "long":
            case "Long":
                val = ((Long) value).toString();
                break;
            case "String":
                val = "'" + value + "'";
                break;
            case "boolean":
                val = (Boolean) value ? "1" : "0";
                break;
            default:
                val = value.toString();
        }
        if (!objectExists(clazz.getSimpleName(), key, val)) return null;
        ResultSet result = connection.createStatement().executeQuery("select * from " + clazz.getSimpleName() + " where " + key + "=" + val + ";");
        result.next();
        Object ret = clazz.newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            String type = field.getType().getTypeName();
            switch (type) {
                case "java.lang.Long":
                case "long":
                    field.setLong(ret, result.getLong(field.getName()));
                    break;
                case "java.lang.String":
                    field.set(ret, result.getString(field.getName()));
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    field.setBoolean(ret, result.getBoolean(field.getName()));
                    break;
                case "java.lang.Double":
                case "double":
                    field.setDouble(ret, result.getDouble(field.getName()));
                    break;
            }
        }
        result.close();
        if (clazz.getSimpleName().equals("DiscordUser")) {
            return ((DiscordUser) ret).setHeart(heart);
        }
        return ret;
    }

    @SneakyThrows
    public void updateTable(Object object, String key, String value) {
        if (!objectExists(object.getClass().getSimpleName(), key, value)) {
            insertToTable(object);
            return;
        }

        Class<?> clazz = object.getClass();
        StringBuilder toQ = new StringBuilder();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            String type = field.getType().getTypeName();
            switch (type) {
                case "java.lang.Long":
                case "long":
                    toQ.append(field.getName()).append(" = ").append(field.getLong(object)).append(",");
                    break;
                case "java.lang.Double":
                case "double":
                    toQ.append(field.getName()).append(" = ").append(field.getDouble(object)).append(",");
                    break;
                case "java.lang.String":
                    toQ.append(field.getName()).append(" = ").append("'").append(field.get(object)).append("',");
                    break;
                case "java.lang.Boolean":
                case "boolean":
                    toQ.append(field.getName()).append(" = ").append((field.getBoolean(object) ? 1 : 0)).append(",");
                    break;
            }
        }
        String query = "UPDATE " + clazz.getSimpleName() + " SET " + toQ.substring(0, toQ.length() - 1) + " " +
                "WHERE " + key + " = " + value + ";";
        connection.prepareStatement(query).execute();

    }

    @SneakyThrows
    public void makeTable(Class<?> clazz) {
        String name = clazz.getSimpleName();
        List<String> list = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(declaredField.getModifiers())) continue;
            list.addAll(getColumnLines(declaredField));
        }
        int lastIndex = list.size() - 1;
        String lastItem = list.get(lastIndex);
        list.remove(lastIndex);
        list.add(lastItem.substring(0, lastItem.length() - 1));
        StringBuilder toQ = new StringBuilder();
        for (String s : list) {
            toQ.append(s);
        }
        String query = "CREATE TABLE IF NOT EXISTS " + name + " (" + toQ + ")";
        connection.prepareStatement(query).execute();
        absenceCheck(clazz);
        list.clear();
    }

}
