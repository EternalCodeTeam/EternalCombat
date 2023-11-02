package com.eternalcode.combat.notification.implementation.title;

import com.eternalcode.combat.notification.Notification;
import com.eternalcode.combat.notification.NotificationType;
import net.kyori.adventure.title.Title;

public record SubTitleNotification(Title.Times times, String message) implements Notification {

    @Override
    public NotificationType type() {
        return NotificationType.SUB_TITLE;
    }
}
