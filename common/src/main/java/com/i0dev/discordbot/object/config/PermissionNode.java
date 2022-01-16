package com.i0dev.discordbot.object.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PermissionNode {

    String commandID;

    List<Long> usersAllowed;
    List<Long> rolesAllowed;
    List<Long> usersDenied;
    List<Long> rolesDenied;

    boolean everyoneAllowed;
    boolean requireAdministrator;

}
