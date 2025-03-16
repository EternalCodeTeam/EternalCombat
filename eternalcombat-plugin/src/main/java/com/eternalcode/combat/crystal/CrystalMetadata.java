package com.eternalcode.combat.crystal;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CrystalMetadata extends MetadataValueAdapter {

    private UUID damager;

    protected CrystalMetadata(@NotNull Plugin owningPlugin, UUID damager) {
        super(owningPlugin);
        this.damager = damager;
    }

    Optional<UUID> getDamager() {
        return Optional.ofNullable(this.damager);
    }

    @Override
    public @Nullable UUID value() {
        return this.damager;
    }

    @Override
    public void invalidate() {
        this.damager = null;
    }

}
