package com.eternalcode.combat.drop;

import com.eternalcode.combat.fight.FightDeathCasue;
import com.eternalcode.combat.fight.FightTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Drop {

    private final Player player;
    private final Player killer;
    private final FightTag fightTag;
    private final FightDeathCasue deathCasue;

    private List<ItemStack> droppedItems;
    private int droppedExp;

    private Drop(Player player, Player killer, FightTag fightTag, FightDeathCasue deathCasue, List<ItemStack> droppedItems, int droppedExp) {
        this.player = player;
        this.killer = killer;
        this.fightTag = fightTag;
        this.deathCasue = deathCasue;

        this.droppedItems = droppedItems;
        this.droppedExp = droppedExp;
    }

    public List<ItemStack> getDroppedItems() {
        return this.droppedItems;
    }

    public void setDroppedItems(List<ItemStack> droppedItems) {
        this.droppedItems = droppedItems;
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

    public FightDeathCasue getDeathCasue() {
        return this.deathCasue;
    }

    public boolean hasKiller() {
        return this.killer != null;
    }

    public int getDroppedExp() {
        return this.droppedExp;
    }

    public void setDroppedExp(int droppedExp) {
        this.droppedExp = droppedExp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Player player;
        private Player killer;
        private FightTag fightTag;
        private FightDeathCasue deathCasue;
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

        public Builder deathCasue(@NotNull FightDeathCasue deathCasue) {
            this.deathCasue = deathCasue;
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
              this.deathCasue,
              this.droppedItems,
              this.droppedExp
            );
        }
    }
}
