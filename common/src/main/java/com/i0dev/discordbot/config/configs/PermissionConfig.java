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

package com.i0dev.discordbot.config.configs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.PermissionGroup;
import com.i0dev.discordbot.object.config.PermissionNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class PermissionConfig extends AbstractConfiguration {
    public PermissionConfig(Heart heart, String path) {
        this.path = path;
        this.heart = heart;
    }

    List<PermissionGroup> permissionGroups = Arrays.asList(
            new PermissionGroup(
                    "Administrator",
                    Arrays.asList(
                            786977110266347561L,
                            786977069422084116L,
                            766183472749871114L
                    ),
                    Arrays.asList(177197491396018176L),
                    Arrays.asList("Moderator"),
                    false
            ),
            new PermissionGroup(
                    "Moderator",
                    Arrays.asList(
                            766183475035373568L,
                            766183475761774603L),
                    Collections.emptyList(),
                    Collections.singletonList(
                            "Helper"
                    ),
                    false
            ),
            new PermissionGroup(
                    "Helper",
                    Arrays.asList(766183476520943616L),
                    Arrays.asList(),
                    Arrays.asList("Member"),
                    false
            ),
            new PermissionGroup(
                    "Operator",
                    Arrays.asList(788087143096516634L),
                    Arrays.asList(),
                    Arrays.asList("Member"),
                    false
            ),
            new PermissionGroup(
                    "Member",
                    Arrays.asList(766183896202346516L),
                    Arrays.asList(),
                    Arrays.asList(),
                    true
            )

    );

    List<PermissionNode> permissions = Arrays.asList(
            new PermissionNode(
                    "help",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "members",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "role_info",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "coinflip",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "profile",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "roles",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ban",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "announce",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_add",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_admin_only",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_close",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_info",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_leaderboard",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_panel",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_rename",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_remove",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "ticket_manual",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "kick",
                    Arrays.asList("Moderator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "reload",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "bot_info",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "changelog",
                    Arrays.asList("Administrator", "Operator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "direct_message",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "verify_panel",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "prune",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "server_info",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "server_lookup",
                    Arrays.asList("Moderator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "blacklist_add",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "blacklist_remove",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "blacklist_list",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "blacklist_clear",
                    Arrays.asList(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    true
            ),
            new PermissionNode(
                    "suggestion_add",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "suggsetion_accept",
                    Arrays.asList("Moderator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "suggsetion_deny",
                    Arrays.asList("Moderator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "invite_invites",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "invite_leaderboard",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "invite_add",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "invite_remove",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "invite_clear",
                    Arrays.asList(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    true
            ),
            new PermissionNode(
                    "mute_add",
                    Arrays.asList("Moderator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "mute_remove",
                    Arrays.asList("Moderator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "mute_list",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "mute_clear",
                    Arrays.asList(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    true
            ),
            new PermissionNode(
                    "avatar",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "link_code",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "link_force",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "link_info",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "link_remove",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "link_check_ign",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "giveaway_create",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "giveaway_end",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "giveaway_info",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "giveaway_reroll",
                    Arrays.asList("Administrator"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "factions_leader",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "factions_confirm",
                    Arrays.asList("Helper"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            ),
            new PermissionNode(
                    "command_info",
                    Arrays.asList("Member"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    false,
                    false
            )
    );

}
