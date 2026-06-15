package com.eternalcode.combat.config.implementation;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class SignEditingSettingsTest {

    @Test
    void shouldDisableSignEditingBlockerByDefault() {
        SignEditingSettings signEditingSettings = new SignEditingSettings();

        assertFalse(signEditingSettings.disableSignEditingDuringCombat);
    }
}
