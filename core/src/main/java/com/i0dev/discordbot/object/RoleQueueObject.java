package com.i0dev.discordbot.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoleQueueObject {

    Long userID;
    Long roleID;
    boolean add;
}