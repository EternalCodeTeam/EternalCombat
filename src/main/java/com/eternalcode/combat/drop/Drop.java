package com.eternalcode.combat.drop;

import com.eternalcode.combat.fight.FightDeathCause;
import com.eternalcode.combat.fight.FightTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Drop {

    private final Player player;
    private final Player killer;
    private final FightTag fightTag;
    private final FightDeathCause deathCause;

    private final List<ItemStack> droppedItems;
    private final int droppedExp;

    private Drop(Player player, Player killer, FightTag fightTag, FightDeathCause deathCause, List<ItemStack> droppedItems, int droppedExp) {
        this.player = player;
        this.killer = killer;
        this.fightTag = fightTag;
        this.deathCause = deathCause;

        this.droppedItems = droppedItems;
        this.droppedExp = droppedExp;
    }

    public List<ItemStack> getDroppedItems() {
        return Collections.unmodifiableList(this.droppedItems);
    }

    public Player getPlayer() {
        return this.player;
    }

    public @Nullable Player getKiller() {
        return this.killer;
    }

    public FightTag getFightTag() {
        return this.fightTag;
    }

    public FightDeathCause getDeathCause() {
        return this.deathCause;
    }

    public boolean hasKiller() {
        return this.killer != null;
    }

    public int getDroppedExp() {
        return this.droppedExp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Player player;
        private Player killer;
        private FightTag fightTag;
        private FightDeathCause deathCause;
        private List<ItemStack> droppedItems;
        private int droppedExp;

        public Builder player(@NotNull Player player) {
            this.player = player;
            return this;
        }

        public Builder killer(@Nullable Player killer) {
            this.killer = killer;
            return this;
        }

        public Builder fightTag(@NotNull FightTag fightTag) {
            this.fightTag = fightTag;
            return this;
        }

        public Builder deathCause(@NotNull FightDeathCause deathCause) {
            this.deathCause = deathCause;
            return this;
        }

        public Builder droppedItems(@NotNull List<ItemStack> droppedItems) {
            this.droppedItems = new ArrayList<>(droppedItems);
            return this;
        }

        public Builder droppedExp(int droppedExp) {
            this.droppedExp = droppedExp;
            return this;
        }

        public Drop build() {
            return new Drop(
              this.player,
              this.killer,
              this.fightTag,
              this.deathCause,
              this.droppedItems,
              this.droppedExp
            );
        }
    }
}
