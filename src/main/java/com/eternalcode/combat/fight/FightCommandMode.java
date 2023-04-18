package com.eternalcode.combat.fight;

public enum FightCommandMode {
    WHITELIST,
    BLACKLIST;

    public boolean shouldBlock(boolean isInList) {
        return switch (this) {
            case WHITELIST -> !isInList;
            case BLACKLIST -> isInList;
        };
    }
}
