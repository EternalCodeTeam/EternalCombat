package com.eternalcode.combat.config.implementation;

public enum InventoryAccessMode {
    /**
     * Allow access to all inventories during combat
     */
    ALLOW_ALL,

    /**
     * Block access to all inventories during combat
     */
    BLOCK_ALL,

    /**
     * Only block inventories in the restricted list
     */
    BLACKLIST,

    /**
     * Only allow inventories in the allowed list
     */
    WHITELIST;

    /**
     * Determines if an inventory type should be blocked based on the mode and whether it's in the list
     *
     * @param isInList whether the inventory type is in the configured list
     * @return true if the inventory should be blocked
     */
    public boolean shouldBlock(boolean isInList) {
        return switch (this) {
            case ALLOW_ALL -> false;
            case BLOCK_ALL -> true;
            case BLACKLIST -> isInList;
            case WHITELIST -> !isInList;
        };
    }
}
