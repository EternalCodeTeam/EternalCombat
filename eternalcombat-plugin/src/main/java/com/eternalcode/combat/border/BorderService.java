package com.eternalcode.combat.border;

import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BorderService {

    void updateBorder(Player player, Location to);

    void clearBorder(Player player);

    Set<BorderPoint> getActiveBorder(Player player);

}
