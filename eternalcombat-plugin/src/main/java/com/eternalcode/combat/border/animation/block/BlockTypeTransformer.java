package com.eternalcode.combat.border.animation.block;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.Locale;

public class BlockTypeTransformer extends BidirectionalTransformer<String, BlockType> {

    @Override
    public GenericsPair<String, BlockType> getPair() {
        return this.genericsPair(String.class, BlockType.class);
    }

    @Override
    public BlockType leftToRight(String data, SerdesContext serdesContext) {
        BlockType blockType = BlockType.fromName(data);
        if (blockType == null) {
            throw new IllegalArgumentException("Unknown block type: " + data);
        }

        return blockType;
    }

    @Override
    public String rightToLeft(BlockType data, SerdesContext serdesContext) {
        return data.getName().toUpperCase(Locale.ROOT);
    }

}
