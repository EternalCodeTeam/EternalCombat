package com.eternalcode.combat.border.animation.particle;

import com.github.retrooper.packetevents.protocol.particle.type.ParticleType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.util.Locale;

public class ParticleTypeTransformer extends BidirectionalTransformer<String, ParticleType<?>> {

    @Override
    public GenericsPair<String, ParticleType<?>> getPair() {
        return this.generics(GenericsDeclaration.of(String.class), GenericsDeclaration.of(ParticleType.class));
    }

    @Override
    public ParticleType<?> leftToRight(String data, SerdesContext serdesContext) {
        if (!data.contains(":")) {
            data = ResourceLocation.normString(data);
        }

        ParticleType<?> type = ParticleTypes.getByName(data.toLowerCase(Locale.ROOT));
        if (type == null) {
            throw new IllegalArgumentException("Unknown particle type: " + data);
        }

        return type;
    }

    @Override
    public String rightToLeft(ParticleType<?> data, SerdesContext serdesContext) {
        ResourceLocation location = data.getName();
        if (location.getNamespace().equals(ResourceLocation.VANILLA_NAMESPACE)) {
            return location.getKey().toUpperCase(Locale.ROOT);
        }

        return location.toString();
    }

}
