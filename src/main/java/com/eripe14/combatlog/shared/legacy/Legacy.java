package com.eripe14.combatlog.shared.legacy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;
import java.util.stream.Collectors;

public final class Legacy {

    private static final LegacyComponentSerializer AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static Component component(String text) {
        return AMPERSAND_SERIALIZER.deserialize(text);
    }

    public static List<Component> component(List<String> texts) {
        return texts.stream()
                .map(Legacy::component)
                .collect(Collectors.toList());
    }

}
