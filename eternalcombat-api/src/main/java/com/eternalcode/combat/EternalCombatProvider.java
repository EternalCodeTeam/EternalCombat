package com.eternalcode.combat;

public final class EternalCombatProvider {

    private static EternalCombatApi ETERNAL_COMBAT_API;

    public static EternalCombatApi provide() {
        if (ETERNAL_COMBAT_API == null) {
            throw new IllegalStateException("EternalCombatApi has not been initialized yet!");
        }

        return ETERNAL_COMBAT_API;
    }

    static void initialize(EternalCombatApi eternalCombatApi) {
        if (ETERNAL_COMBAT_API != null) {
            throw new IllegalStateException("EternalCombatApi has already been initialized!");
        }

        ETERNAL_COMBAT_API = eternalCombatApi;
    }

    static void deinitialize() {
        if (ETERNAL_COMBAT_API == null) {
            throw new IllegalStateException("EternalCombatApi has not been initialized yet!");
        }

        ETERNAL_COMBAT_API = null;
    }

}
