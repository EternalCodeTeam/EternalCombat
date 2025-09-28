package com.eternalcode.combat.bridge.placeholder;

import com.eternalcode.combat.config.implementation.PlaceholderSettings;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.FightTag;
import com.eternalcode.combat.util.DurationUtil;
import com.eternalcode.commons.time.DurationParser;
import java.util.Optional;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class FightTagPlaceholder extends PlaceholderExpansion {

    private static final String IDENTIFIER = "eternalcombat";

    private final PlaceholderSettings placeholderSettings;
    private final FightManager fightManager;
    private final Server server;
    private final Plugin plugin;

    public FightTagPlaceholder(PluginConfig pluginConfig, FightManager fightManager, Server server, Plugin plugin) {
        this.placeholderSettings = pluginConfig.placeholders;
        this.fightManager = fightManager;
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        return switch (identifier) {
            case "remaining_millis" -> this.handleRemainingMillis(player);
            case "remaining_seconds" -> this.handleRemainingSeconds(player);
            case "opponent" -> this.handleOpponent(player);
            case "opponent_health" -> this.handleOpponentHealth(player);
            case "isInCombat" -> this.handleIsInCombat(player);
            case "isInCombat_formatted" -> this.handleIsInCombatFormatted(player);
            default -> null;
        };
    }

    private String handleRemainingMillis(OfflinePlayer player) {
        return this.getFightTag(player)
            .map(tag -> DurationParser.TIME_UNITS.format(tag.getRemainingDuration()))
            .orElse("");
    }

    private String handleRemainingSeconds(OfflinePlayer player) {
        return this.getFightTag(player)
            .map(tag -> DurationUtil.format(tag.getRemainingDuration()))
            .orElse("");
    }

    private String handleOpponent(OfflinePlayer player) {
        return this.getTagger(player)
            .map(Player::getName)
            .orElse("");
    }

    private String handleOpponentHealth(OfflinePlayer player) {
        return this.getTagger(player)
            .map(tagger -> String.format("%.2f", tagger.getHealth()))
            .orElse("");
    }

    private String handleIsInCombat(OfflinePlayer player) {
        return this.isPlayerInCombat(player).toString();
    }

    private String handleIsInCombatFormatted(OfflinePlayer player) {
        return this.isPlayerInCombat(player)
            ? this.placeholderSettings.isInCombatFormattedTrue
            : this.placeholderSettings.isInCombatFormattedFalse;
    }

    private boolean isPlayerInCombat(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        return onlinePlayer != null && this.fightManager.isInCombat(onlinePlayer.getUniqueId());
    }

    private @NotNull Optional<Player> getTagger(OfflinePlayer player) {
        return this.getFightTag(player)
            .map(FightTag::getTagger)
            .map(this.server::getPlayer);
    }

    private Optional<FightTag> getFightTag(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();

        if (onlinePlayer == null || !this.fightManager.isInCombat(onlinePlayer.getUniqueId())) {
            return Optional.empty();
        }

        return Optional.of(this.fightManager.getTag(onlinePlayer.getUniqueId()));
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }
}
