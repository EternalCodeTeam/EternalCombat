package com.eternalcode.combat;

import com.eternalcode.combat.fight.drop.DropKeepInventoryManager;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.drop.DropService;
import com.eternalcode.combat.fight.effect.FightEffectService;
import com.eternalcode.combat.fight.pearl.FightPearlService;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.combat.fight.tagout.FightTagOutService;

public interface EternalCombatApi {

    FightManager getFightManager();

    RegionProvider getRegionProvider();

    FightPearlService getFightPearlManager();

    FightTagOutService getFightTagOutService();

    FightEffectService getFightEffectService();

    DropService getDropService();

    DropKeepInventoryManager getDropKeepInventoryManager();

}
