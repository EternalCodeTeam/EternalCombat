package com.eternalcode.combat.border.animation.particle;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;

public class ParticleColorTransformer extends BidirectionalTransformer<String, ParticleColor> {

    @Override
    public GenericsPair<String, ParticleColor> getPair() {
        return this.genericsPair(String.class, ParticleColor.class);
    }

    @Override
    public ParticleColor leftToRight(String data, SerdesContext serdesContext) {
        return ParticleColor.fromName(data);
    }

    @Override
    public String rightToLeft(ParticleColor data, SerdesContext serdesContext) {
        return data.getName();
    }

}
