package com.eternalcode.combat.fight.blocker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class SignEditingBlockerTest {

    @Test
    void shouldRecognizeStandingSignMaterials() {
        assertTrue(SignEditingBlocker.isSign(Material.OAK_SIGN));
        assertTrue(SignEditingBlocker.isSign(Material.CRIMSON_SIGN));
    }

    @Test
    void shouldRecognizeWallSignMaterials() {
        assertTrue(SignEditingBlocker.isSign(Material.OAK_WALL_SIGN));
        assertTrue(SignEditingBlocker.isSign(Material.WARPED_WALL_SIGN));
    }

    @Test
    void shouldIgnoreNonSignMaterials() {
        assertFalse(SignEditingBlocker.isSign(Material.CHEST));
        assertFalse(SignEditingBlocker.isSign(Material.ENDER_PEARL));
    }

    @Test
    void shouldRecognizeEnderPearlMaterial() {
        assertTrue(SignEditingBlocker.isEnderPearl(Material.ENDER_PEARL));
        assertFalse(SignEditingBlocker.isEnderPearl(Material.OAK_SIGN));
    }
}
