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

package com.i0dev.discordbot.util;

import net.dv8tion.jda.api.entities.Role;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Utility {


    public static String capitalizeFirst(String a) {
        return a.substring(0, 1).toUpperCase() + a.substring(1).toLowerCase();
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static long deserializeStringToMilliseconds(String input) {
        input = input.toLowerCase();
        if (input.isEmpty()) return -1;
        int time = 0;
        StringBuilder number = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (isInt(String.valueOf(c))) {
                number.append(c);
                continue;
            }
            if (number.toString().isEmpty()) return -1;
            int add = Integer.parseInt(number.toString());
            switch (c) {
                case 'w':
                    add *= 7;
                case 'd':
                    add *= 24;
                case 'h':
                    add *= 60;
                case 'm':
                    add *= 60;
                case 's':
                    time += add;
                    number.setLength(0);
                    break;
                default:
                    return -1;
            }
        }
        return time * 1000L;
    }

    public static String formatDate(Long instant) {
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(instant), ZoneId.of("America/New_York"));
        String Month = time.getMonth().getValue() + "";
        String Day = time.getDayOfMonth() + "";
        String Year = time.getYear() + "";
        String Hour = time.getHour() + "";
        String Minute = time.getMinute() + "";
        String Second = time.getSecond() + "";

        return "[" + Month + "/" + Day + "/" + Year + " " + Hour + ":" + Minute + ":" + Second + "]";
    }

    public static int randomNumber(int max) {
        int num = Math.abs((int) (Math.random() * max));
        if (num == 0)
            randomNumber(max);
        return num;
    }


    public static String formatRolesList(List<Role> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>();
        for (Role s : list) {
            Stripped.add(capitalizeFirst(s.getAsMention()));
        }
        for (int i = 0; i < Stripped.size(); i++) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String formatStringList(List<String> list, String determiner, boolean capitalizeFirst) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> stripped = new ArrayList<>();
        for (String s : list) {
            if (capitalizeFirst)
                stripped.add(capitalizeFirst(s));
            else stripped.add(s);
        }

        for (int i = 0; i < stripped.size(); i++) {
            sb.append(stripped.get(i));
            if (stripped.size() - 1 > i) {
                sb.append(determiner);
            }
        }
        return sb.toString();
    }

}