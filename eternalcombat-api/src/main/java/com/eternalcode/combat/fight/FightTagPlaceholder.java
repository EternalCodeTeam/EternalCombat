package com.eternalcode.combat.fight;

import com.eternalcode.combat.util.DurationUtil;
import com.eternalcode.commons.time.DurationParser;
import java.time.Duration;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FightTagPlaceholder extends PlaceholderExpansion {

    private final FightManager fightManager;
    private final Server server;

    public FightTagPlaceholder(FightManager fightManager, Server server) {
        this.fightManager = fightManager;
        this.server = server;
    }

    @Override
    public boolean canRegister() {
        return true;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "eternalcombat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "EternalCode";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (identifier.equals("remaining_seconds")) {

            FightTag fightTag = this.getFightTag(player);

            if (fightTag == null) {
                return "";
            }

            Duration duration = fightTag.getRemainingDuration();

            return DurationUtil.format(duration);

        }
        if (identifier.equals("remaining_millis")) {

            FightTag fightTag = this.getFightTag(player);

            if (fightTag == null) {
                return "";
            }

            Duration duration = fightTag.getRemainingDuration();

            return DurationParser.TIME_UNITS.format(duration);

        }
        if (identifier.equals("opponent")) {

            FightTag fightTag = this.getFightTag(player);

            if (fightTag == null) {
                return "";
            }

            Player opponent = this.server.getPlayer(fightTag.getTagger());

            if (opponent == null) {
                return "";
            }

            return opponent.getName();

        }
        if (identifier.equals("opponent_health")) {

            FightTag fightTag = this.getFightTag(player);

            if (fightTag == null) {
                return "";
            }

            Player opponent = this.server.getPlayer(fightTag.getTagger());

            if (opponent == null) {
                return "";
            }

            return String.valueOf(opponent.getHealth());

        }
        return null;
    }

    private FightTag getFightTag(OfflinePlayer player) {
        if (player.isOnline()) {
            Player onlinePlayer = player.getPlayer();
            assert onlinePlayer != null;

            if (!this.fightManager.isInCombat(onlinePlayer.getUniqueId())) {
                return null;
            }
            return this.fightManager.getTag(onlinePlayer.getUniqueId());
        }
        return null;
    }
}
