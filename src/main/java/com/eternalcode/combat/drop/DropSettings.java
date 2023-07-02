package com.eternalcode.combat.drop;

import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;

@Contextual
public class DropSettings {

    @Description({
        "# UNCHANGED - The default way of item drop defined by the engine",
        "# PERCENT - Drops a fixed percentage of items",
    })
    public DropType dropType = DropType.UNCHANGED;

    @Description("# What percentage of items should drop from the player? (Only if Drop Type is set to PERCENT)")
    public int dropItemPercent = 100;

    @Description("# Does the drop modification affect the experience drop?")
    public boolean affectExperience = false;
}
