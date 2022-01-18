package com.i0dev.discordbot.config.configs;

import com.i0dev.discordbot.Heart;
import com.i0dev.discordbot.object.abs.AbstractConfiguration;
import com.i0dev.discordbot.object.config.MovementObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementConfig extends AbstractConfiguration {

    public MovementConfig(Heart heart, String path) {
        this.heart = heart;
        this.path = path;
    }

    long movementsChannelId = 0;

    List<Long> extraRolesToRemoveOnDemotion = Arrays.asList(
            850475223114842172L,
            851939282197020733L,
            853725140193771520L,
            766186192093839392L,
            795324267969249310L
    );

    List<MovementObject> movementOptions = Arrays.asList(
            new MovementObject(
                    766183472749871114L,
                    "Manager",
                    Arrays.asList(
                            766183473596989460L,
                            766186192093839392L,
                            795324267969249310L
                    )
            ),
            new MovementObject(
                    786977069422084116L,
                    "Senior-Admin",
                    Arrays.asList(
                            766183473596989460L,
                            766186192093839392L,
                            795324267969249310L
                    )
            ),
            new MovementObject(
                    786977110266347561L,
                    "Administrator",
                    Arrays.asList(
                            766183474251169814L,
                            766186192093839392L,
                            795324267969249310L
                    )
            ),
            new MovementObject(
                    766183475035373568L,
                    "Senior-Mod",
                    Arrays.asList(
                            766183474251169814L,
                            766186192093839392L,
                            795324267969249310L
                    )
            ),
            new MovementObject(
                    766183475761774603L,
                    "Moderator",
                    Arrays.asList(
                            766186192093839392L,
                            795324267969249310L
                    )
            ),
            new MovementObject(
                    766183476520943616L,
                    "Trial-Mod",
                    Arrays.asList(
                            766186192093839392L
                    )
            ),
            new MovementObject(
                    788087143096516634L,
                    "Operator",
                    Arrays.asList()
            ),
            new MovementObject(
                    766183900422209557L,
                    "Builder",
                    Arrays.asList()
            )
    );

    String promoteContent = "**{tag}** has been promoted to **{displayName}**.";
    String promoteTitle = "Staff Promotion";
    String demoteContent = "**{tag}** has been demoted to **{displayName}**.";
    String demoteTitle = "Staff Demotion";
    String removeContent = "**{tag}** has been removed from the staff team.";
    String removeTitle = "Staff Removal";
    String resignContent = "**{tag}** has resigned from the staff team.";
    String resignTitle = "Staff Resignation";

}
