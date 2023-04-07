package com.eternalcode.combat.config.composer;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import panda.std.Result;

public class PotionEffectComposer implements SimpleComposer<PotionEffect> {

    private static final String SERIALIZE_FORMAT = "%s:%d:%d";
    private static final int DEFAULT_DURATION = 0;
    private static final int DEFAULT_AMPLIFIER = 0;

    @Override
    public Result<PotionEffect, Exception> deserialize(String source) {
        String[] parts = source.split(":");

        if (parts.length < 1) {
            return Result.error(new IllegalArgumentException("Invalid source format"));
        }

        PotionEffectType type = PotionEffectType.getByName(parts[0]);

        if (type == null) {
            return Result.error(new IllegalArgumentException("Invalid potion effect type: " + parts[0]));
        }

        int duration = parts.length > 1 ? Integer.parseInt(parts[1]) : DEFAULT_DURATION;
        int amplifier = parts.length > 2 ? Integer.parseInt(parts[2]) : DEFAULT_AMPLIFIER;

        return Result.ok(new PotionEffect(type, duration, amplifier));
    }

    @Override
    public Result<String, Exception> serialize(PotionEffect effect) {
        return Result.ok(SERIALIZE_FORMAT.formatted(effect.getType().getName(), effect.getDuration(), effect.getAmplifier()));
    }
}
