package com.eternalcode.combat.drop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DropInfo {

    private final List<ItemStack> droppedItems;
    private final Player player;
    private final Player killer;

    private DropInfo(List<ItemStack> droppedItems, Player player, Player killer) {
        this.droppedItems = droppedItems;
        this.player = player;
        this.killer = killer;
    }

    public List<ItemStack> getDroppedItems() {
        return new ArrayList<>(this.droppedItems);
    }

    public Player getPlayer() {
        return this.player;
    }

    public @Nullable Player getKiller() {
        return this.killer;
    }

    public boolean hasKiller() {
        return this.killer != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<ItemStack> droppedItems;
        private Player player;
        private Player killer;

        public Builder droppedItems(@NotNull List<ItemStack> droppedItems) {
            this.droppedItems = droppedItems;
            return this;
        }

        public Builder player(@NotNull Player player) {
            this.player = player;
            return this;
        }

        public Builder killer(@Nullable Player killer) {
            this.killer = killer;
            return this;
        }

        public DropInfo build() {
            return new DropInfo(
                this.droppedItems,
                this.player,
                this.killer
            );
        }
    }
}
