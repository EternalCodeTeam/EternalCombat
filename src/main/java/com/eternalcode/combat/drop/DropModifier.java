package com.eternalcode.combat.drop;

public interface DropModifier {

    DropType getDropType();

    DropResult modifyDrop(Drop drop);

}
