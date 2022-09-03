package com.eripe14.combatlog;

import com.eripe14.combatlog.config.ConfigLoader;
import com.eripe14.combatlog.config.MessageConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CombatLogPlugin extends JavaPlugin {

    private MessageConfig messageConfig;

    private final File messagePath = new File(this.getDataFolder(), "messages.yml");

    @Override
    public void onLoad() {
        this.messageConfig = new ConfigLoader(messagePath).loadMessageConfig();
    }

    @Override
    public void onEnable() {

    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }
}
