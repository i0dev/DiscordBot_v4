package com.i0dev.discordbot.object.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TicketOption {
    List<String> questions;
    long category;
    String channelName, emoji, displayName, buttonLabel = "";
    boolean pingStaff, adminOnlyDefault;
    List<Long> rolesToPing;
    List<Long> rolesToSee;
}
