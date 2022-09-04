package com.eripe14.combatlog.bukkit.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ChatUtil {

    public static String fixColor(String text) {
        return text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> fixLore(List<String> lore) {
        return lore.stream().map(ChatUtil::fixColor).collect(Collectors.toList());
    }

    private ChatUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

}
