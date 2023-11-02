package com.eternalcode.combat.scheduler;

import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public interface Scheduler {

    BukkitTask sync(Runnable task);

    BukkitTask async(Runnable task);

    BukkitTask laterSync(Runnable task, Duration delay);

    BukkitTask laterAsync(Runnable task, Duration delay);

    BukkitTask timerSync(Runnable task, Duration delay, Duration period);

    BukkitTask timerAsync(Runnable task, Duration delay, Duration period);
}
