package com.i0dev.discordbot.object.config;


import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MovementObject {
    long mainRole;
    String displayName;
    List<Long> extraRoles;
}