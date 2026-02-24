package com.eternalcode.combat.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class RegionContainsWorldCheckTest {

    @Test
    void shouldNotContainLocationFromDifferentWorld() {
        World regionWorld = mock(World.class);
        World otherWorld = mock(World.class);

        Region region = new Region() {
            @Override
            public Point getCenter() {
                return new Point(regionWorld, 5.0, 5.0);
            }

            @Override
            public Location getMin() {
                return new Location(regionWorld, 0.0, 0.0, 0.0);
            }

            @Override
            public Location getMax() {
                return new Location(regionWorld, 10.0, 10.0, 10.0);
            }
        };

        Location locationInOtherWorld = new Location(otherWorld, 5.0, 5.0, 5.0);

        assertFalse(region.contains(locationInOtherWorld));
    }
}
