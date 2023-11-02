package com.eternalcode.combat.scheduler;

import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;

public class BukkitSchedulerImpl implements Scheduler {

    private final Plugin plugin;
    private final BukkitScheduler bukkitScheduler;

    public BukkitSchedulerImpl(Plugin plugin, BukkitScheduler bukkitScheduler) {
        this.plugin = plugin;
        this.bukkitScheduler = bukkitScheduler;
    }

    @Override
    public BukkitTask sync(Runnable task) {
        return this.bukkitScheduler.runTask(this.plugin, task);
    }

    @Override
    public BukkitTask async(Runnable task) {
        return this.bukkitScheduler.runTaskAsynchronously(this.plugin, task);
    }

    @Override
    public BukkitTask laterSync(Runnable task, Duration delay) {
        return this.bukkitScheduler.runTaskLater(this.plugin, task, DurationUtil.toTicks(delay));
    }

    @Override
    public BukkitTask laterAsync(Runnable task, Duration delay) {
        return this.bukkitScheduler.runTaskLaterAsynchronously(this.plugin, task, DurationUtil.toTicks(delay));
    }

    @Override
    public BukkitTask timerSync(Runnable task, Duration delay, Duration period) {
        return this.bukkitScheduler.runTaskTimer(this.plugin, task, DurationUtil.toTicks(delay), DurationUtil.toTicks(period));
    }

    @Override
    public BukkitTask timerAsync(Runnable task, Duration delay, Duration period) {
        return this.bukkitScheduler.runTaskTimerAsynchronously(this.plugin, task, DurationUtil.toTicks(delay), DurationUtil.toTicks(period));
    }
}
