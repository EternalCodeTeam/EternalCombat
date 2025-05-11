package com.eternalcode.combat.head;

import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Random;

public class PlayerHeadServiceImpl implements PlayerHeadService {

    private final PluginConfig config;
    private final Random random;

    public PlayerHeadServiceImpl(PluginConfig config) {
        this.config = config;
        this.random = new Random();
    }

    @Override
    public boolean tryDropHead(Player victim, Player killer) {
        if (!this.config.playerHead.enabled) {
            return false;
        }

        if (this.config.playerHead.onlyOnPvPDeath && killer == null) {
            return false;
        }

        if (this.random.nextDouble() * 100 > this.config.playerHead.dropChance) {
            return false;
        }

        Player headOwner = this.config.playerHead.dropKillerHead ? killer : victim;
        if (headOwner == null) {
            return false;
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(headOwner);
            head.setItemMeta(meta);
        }

        victim.getWorld().dropItemNaturally(victim.getLocation(), head);
        return true;
    }
} 