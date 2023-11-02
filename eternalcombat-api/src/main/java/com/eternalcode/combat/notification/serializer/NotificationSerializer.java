package com.eternalcode.combat.notification.serializer;

import com.eternalcode.combat.notification.Notification;
import com.eternalcode.combat.notification.NotificationType;
import com.eternalcode.combat.notification.implementation.ActionBarNotification;
import com.eternalcode.combat.notification.implementation.BossBarNotification;
import com.eternalcode.combat.notification.implementation.ChatNotification;
import com.eternalcode.combat.notification.implementation.title.SubTitleNotification;
import com.eternalcode.combat.notification.implementation.title.TitleNotification;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.title.Title;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NotificationSerializer implements ObjectSerializer<Notification> {

    private static final String NOTIFICATION_RAW_FORMAT = "%s: %s";

    @Override
    public boolean supports(@NonNull Class<? super Notification> type) {
        return Notification.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Notification notification, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        NotificationType type = notification.type();
        String message = notification.message();

        if (type == NotificationType.CHAT || type == NotificationType.ACTION_BAR) {
            data.setValueRaw(NOTIFICATION_RAW_FORMAT.formatted(type, message));
            return;
        }

        if (type == NotificationType.TITLE || type == NotificationType.SUB_TITLE) {
            Title.Times times = this.getTitleTimes(notification);

            this.addBasicNotificationFields(data, notification);
            data.add("times", times, Title.Times.class);
            return;
        }

        if (notification instanceof BossBarNotification bossBarNotification) {
            this.addBasicNotificationFields(data, notification);
            data.add("progress", bossBarNotification.progress(), float.class);
            data.add("color", bossBarNotification.color(), BossBar.Color.class);
            data.add("overlay", bossBarNotification.overlay(), BossBar.Overlay.class);
        }
    }

    private void addBasicNotificationFields(SerializationData data, Notification notification) {
        data.add("type", notification.type(), NotificationType.class);
        data.add("message", notification.message(), String.class);
    }

    private Title.Times getTitleTimes(Notification notification) {
        if (notification instanceof TitleNotification titleNotification) {
            return titleNotification.times();
        }

        if (notification instanceof SubTitleNotification subTitleNotification) {
            return subTitleNotification.times();
        }

        throw new IllegalArgumentException("Notification doesn't have title times.");
    }

    @Override
    public Notification deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        if (!data.containsKey("type")) {
            return this.deserializeRaw(data);
        }

        NotificationType type = data.get("type", NotificationType.class);
        String message = data.get("message", String.class);

        return switch (type) {
            case TITLE -> {
                Title.Times times = data.get("times", Title.Times.class);

                yield new TitleNotification(times, message);
            }

            case SUB_TITLE -> {
                Title.Times times = data.get("times", Title.Times.class);

                yield new SubTitleNotification(times, message);
            }

            case BOSS_BAR -> {
                float progress = data.get("progress", float.class);
                BossBar.Color color = data.get("color", BossBar.Color.class);
                BossBar.Overlay overlay = data.get("overlay", BossBar.Overlay.class);

                yield new BossBarNotification(progress, color, overlay, message);
            }

            default -> throw new IllegalStateException("Unexpected notification type: " + type);
        };
    }

    private Notification deserializeRaw(DeserializationData data) {
        String[] arguments = data.getValueRaw().toString().split(":", 2);

        NotificationType type = NotificationType.valueOf(arguments[0]);
        String message = this.replaceFirstSpace(arguments[1]);

        return switch (type) {
            case CHAT -> new ChatNotification(message);
            case ACTION_BAR -> new ActionBarNotification(message);
            default -> throw new IllegalStateException("Unexpected notification type: " + type);
        };
    }

    private String replaceFirstSpace(String message) {
        if (message.startsWith(" ")) {
            return message.replaceFirst(" ", "");
        }

        return message;
    }
}
