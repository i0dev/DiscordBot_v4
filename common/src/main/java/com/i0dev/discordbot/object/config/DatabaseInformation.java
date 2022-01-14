package com.i0dev.discordbot.object.config;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DatabaseInformation {

    boolean enabled;
    String name = "DiscordBot";
    String address = "localhost";
    long port = 3306;
    String username = "root";
    String password = "password";


}
