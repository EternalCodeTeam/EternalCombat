package com.eternalcode.combat.drop;

public interface DropModifier {

    DropType getDropType();

    void modifyDrop(Drop drop);

}
