package com.eternalcode.combat.border;

import java.util.Optional;
import org.bukkit.Location;

public interface BorderService {

    Optional<BorderResult> resolveBorder(Location location);

}
