package com.eternalcode.combat.crystalpvp;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class CrystalPvpSettings extends OkaeriConfig {

    @Comment({"# Should player be tagged when damaged from crystal explosion set by other player"})
    public boolean tagFromCrystals = true;


    @Comment({"#Should player be tagged when damaged from respawn anchor explosion set by other player"})
    public boolean tagFromRespawnAnchor = true;
}
