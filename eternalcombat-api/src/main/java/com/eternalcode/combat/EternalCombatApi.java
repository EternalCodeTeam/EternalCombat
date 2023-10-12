package com.eternalcode.combat;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.drop.DropKeepInventoryManager;
import com.eternalcode.combat.drop.DropManager;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.effect.FightEffectService;
import com.eternalcode.combat.fight.pearl.FightPearlManager;
import com.eternalcode.combat.fight.tagout.FightTagOutService;
import com.eternalcode.combat.region.RegionProvider;

public interface EternalCombatApi {

    FightManager getFightManager();

    RegionProvider getRegionProvider();

    FightPearlManager getFightPearlManager();

    FightTagOutService getFightTagOutService();

    FightEffectService getFightEffectService();

    DropManager getDropManager();

    DropKeepInventoryManager getDropKeepInventoryManager();

    PluginConfig getPluginConfig();

}
