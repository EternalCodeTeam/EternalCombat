package com.eternalcode.combat.updater;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.commons.concurrent.FutureHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdaterNotificationController implements Listener {

    private static final String NEW_VERSION_AVAILABLE = "<b><gradient:#8a1212:#fc6b03>EternalCombat:</gradient></b> <color:#fce303>New version of EternalCombat is available, please update!";

    private final UpdaterService updaterService;
    private final PluginConfig pluginConfig;
    private final MiniMessage miniMessage;

    public UpdaterNotificationController(UpdaterService updaterService, PluginConfig pluginConfig, MiniMessage miniMessage) {
        this.updaterService = updaterService;
        this.pluginConfig = pluginConfig;
        this.miniMessage = miniMessage;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!shouldNotify(player)) {
            return;
        }

        this.updaterService.checkForUpdate()
            .thenAccept(result -> {
                if (result.isUpdateAvailable()) {
                    player.sendMessage(this.miniMessage.deserialize(NEW_VERSION_AVAILABLE));
                }
            })
            .exceptionally(FutureHandler::handleException);
    }

    private boolean shouldNotify(Player player) {
        return player.hasPermission("eternalcombat.receiveupdates") && this.pluginConfig.settings.notifyAboutUpdates;
    }

}
