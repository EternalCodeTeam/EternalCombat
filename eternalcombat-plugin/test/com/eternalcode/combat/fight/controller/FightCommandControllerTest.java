package com.eternalcode.combat.fight.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class FightCommandControllerTest {

    @Test
    void normalizeCommand_stripsSlashAndNamespace_collapsesWhitespace() {
        assertEquals("tp Steve", FightCommandController.normalizeCommand("/minecraft:tp Steve"));
        assertEquals("home set 1", FightCommandController.normalizeCommand("/home   set   1"));
        assertEquals("spawn", FightCommandController.normalizeCommand("/spawn"));
        assertEquals("", FightCommandController.normalizeCommand("/   "));
        assertEquals("", FightCommandController.normalizeCommand(null));
    }

    @Test
    void matches_exactAndPrefixMatch_caseInsensitive() {
        List<String> restricted = List.of("home set", "tp", "  ", "/minecraft:msg admin");

        assertTrue(FightCommandController.matches("home set", restricted));
        assertTrue(FightCommandController.matches("home set 1", restricted));

        assertTrue(FightCommandController.matches("TP", restricted));
        assertTrue(FightCommandController.matches("tp Steve", restricted));

        assertTrue(FightCommandController.matches("msg admin", restricted));
        assertTrue(FightCommandController.matches("msg admin hello", restricted));

        assertFalse(FightCommandController.matches("home setup", restricted));
        assertFalse(FightCommandController.matches("tpper", restricted));
        assertFalse(FightCommandController.matches("", restricted));
    }
}
