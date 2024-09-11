package com.eternalcode.combat.notification.delay;

import com.eternalcode.combat.notification.Notification;
import java.time.Duration;

public class Delay {

    private final SourceOfDelay source;
    private final Duration delayDuration;
    private final Notification notification;

    public Delay(SourceOfDelay source, Duration delayDuration, Notification notification) {
        this.source = source;
        this.delayDuration = delayDuration;
        this.notification = notification;
    }

    public SourceOfDelay getSource() {
        return source;
    }

    public Duration getDelayDuration() {
        return delayDuration;
    }

    public Notification getNotification() {
        return notification;
    }
}
