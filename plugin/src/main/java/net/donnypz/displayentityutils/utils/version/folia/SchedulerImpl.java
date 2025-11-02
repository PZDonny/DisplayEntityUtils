package net.donnypz.displayentityutils.utils.version.folia;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SchedulerImpl implements Scheduler{


    @Override
    public void run(Runnable runnable) {
        if (FoliaUtils.isFolia()){
            Bukkit.getGlobalRegionScheduler()
                    .execute(DisplayAPI.getPlugin(), runnable);
        }
        else{
            Bukkit.getScheduler().runTask(DisplayAPI.getPlugin(), runnable);
        }
    }

    @Override
    public void runAsync(Runnable runnable) {
        if (FoliaUtils.isFolia()){
            Bukkit.getGlobalRegionScheduler()
                    .execute(DisplayAPI.getPlugin(), runnable);
        }
        else{
            Bukkit.getScheduler().runTaskAsynchronously(DisplayAPI.getPlugin(), runnable);
        }
    }

    @Override
    public Task runLater(Runnable runnable, long delay) {
        Task task;
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        if (FoliaUtils.isFolia()){
            task = new Task(Bukkit.getGlobalRegionScheduler()
                    .runDelayed(DisplayAPI.getPlugin(), t -> runnable.run(), Math.max(delay, 1)));
        }
        else{
            task = new Task(Bukkit.getScheduler().runTaskLater(DisplayAPI.getPlugin(), runnable, delay));
        }
        schedulerRunnable.task = task;
        return task;
    }

    @Override
    public Task runLaterAsync(Runnable runnable, long delay) {
        Task task;
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        if (FoliaUtils.isFolia()){
            task = new Task(Bukkit.getGlobalRegionScheduler()
                    .runDelayed(DisplayAPI.getPlugin(), t -> runnable.run(), Math.max(delay, 1)));
        }
        else{
            task = new Task(Bukkit.getScheduler().runTaskLaterAsynchronously(DisplayAPI.getPlugin(), runnable, delay));
        }
        schedulerRunnable.task = task;
        return task;
    }

    @Override
    public Task runTimer(SchedulerRunnable runnable, long delay, long period) {
        Task task;
        if (FoliaUtils.isFolia()){
            task = new Task(Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(DisplayAPI.getPlugin(), t -> runnable.run(), Math.max(delay, 1), Math.max(delay, 1)));
        }
        else{
            task = new Task(Bukkit.getScheduler().runTaskTimer(DisplayAPI.getPlugin(), runnable, delay, period));
        }
        runnable.task = task;
        return task;
    }

    @Override
    public Task runTimerAsync(SchedulerRunnable runnable, long delay, long period) {
        Task task;
        if (FoliaUtils.isFolia()){
            task = new Task(Bukkit.getGlobalRegionScheduler()
                    .runAtFixedRate(DisplayAPI.getPlugin(), t -> runnable.run(), Math.max(delay, 1), Math.max(delay, 1)));
        }
        else{
            task = new Task(Bukkit.getScheduler().runTaskTimerAsynchronously(DisplayAPI.getPlugin(), runnable, delay, period));
        }
        runnable.task = task;
        return task;
    }

    @Override
    public void entityRun(@NotNull Entity entity, Runnable runnable) {
        if (FoliaUtils.isFolia()){
            entity.getScheduler().execute(DisplayAPI.getPlugin(), runnable, null, 0);
        }
        else{
            run(runnable);
        }
    }

    @Override
    public void entityRunAsync(@NotNull Entity entity, Runnable runnable) {
        if (FoliaUtils.isFolia()){
            entityRun(entity, runnable);
        }
        else{
            runAsync(runnable);
        }
    }

    @Override
    public Task entityRunLater(@NotNull Entity entity, Runnable runnable, long delay) {
        Task task;
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        if (FoliaUtils.isFolia()){
            task = new Task(entity.getScheduler().runDelayed(DisplayAPI.getPlugin(), t -> runnable.run(), null, Math.max(delay, 1)));
        }
        else{
            task = runLater(runnable, delay);
        }
        schedulerRunnable.task = task;
        return task;
    }

    @Override
    public Task entityRunLaterAsync(@NotNull Entity entity, Runnable runnable, long delay) {
        Task task;
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        if (FoliaUtils.isFolia()){
            task = new Task(entity.getScheduler().runDelayed(DisplayAPI.getPlugin(), t -> runnable.run(), null, Math.max(delay, 1)));
        }
        else{
            task = runLaterAsync(runnable, delay);
        }
        schedulerRunnable.task = task;
        return task;
    }

    @Override
    public Task entityRunTimer(@NotNull Entity entity, SchedulerRunnable runnable, long delay, long period) {
        Task task;
        if (FoliaUtils.isFolia()){
            task = new Task(entity.getScheduler()
                    .runAtFixedRate(DisplayAPI.getPlugin(), t -> runnable.run(), null, Math.max(delay, 1), Math.max(delay, 1)));
        }
        else{
            task = new Task(Bukkit.getScheduler().runTaskTimer(DisplayAPI.getPlugin(), runnable, delay, period));
        }
        runnable.task = task;
        return task;
    }

    @Override
    public Task entityRunTimerAsync(@NotNull Entity entity, SchedulerRunnable runnable, long delay, long period) {
        Task task;
        if (FoliaUtils.isFolia()){
            task = new Task(entity.getScheduler()
                    .runAtFixedRate(DisplayAPI.getPlugin(), t -> runnable.run(), null, Math.max(delay, 1), Math.max(delay, 1)));
        }
        else{
            task = new Task(Bukkit.getScheduler().runTaskTimerAsynchronously(DisplayAPI.getPlugin(), runnable, delay, period));
        }
        runnable.task = task;
        return task;
    }

    @Override
    public void partRun(@NotNull ActivePart part, Runnable runnable) {
        if (part instanceof SpawnedDisplayEntityPart sp){
            entityRun(sp.getEntity(), runnable);
        }
        else{
            run(runnable);
        }
    }

    @Override
    public void partRunAsync(@NotNull ActivePart part, Runnable runnable) {
        if (part instanceof SpawnedDisplayEntityPart sp){
            entityRunAsync(sp.getEntity(), runnable);
        }
        else{
            runAsync(runnable);
        }
    }

    @Override
    public Task partRunLater(@NotNull ActivePart part, Runnable runnable, long delay) {
        Task task;
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        if (part instanceof SpawnedDisplayEntityPart sp){
            task = entityRunLater(sp.getEntity(), runnable, delay);
        }
        else{
            task = runLater(runnable, delay);
        }
        schedulerRunnable.task = task;
        return task;
    }

    @Override
    public Task partRunLaterAsync(@NotNull ActivePart part, Runnable runnable, long delay) {
        Task task;
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        if (part instanceof SpawnedDisplayEntityPart sp){
            task = entityRunLaterAsync(sp.getEntity(), runnable, delay);
        }
        else{
            task = runLaterAsync(runnable, delay);
        }
        schedulerRunnable.task = task;
        return task;
    }

    @Override
    public Task partRunTimer(@NotNull ActivePart part, SchedulerRunnable runnable, long delay, long period) {
        if (part instanceof SpawnedDisplayEntityPart sp){
            return entityRunTimer(sp.getEntity(), runnable, delay, period);
        }
        else{
            return runTimer(runnable, delay, period);
        }
    }

    @Override
    public Task partRunTimerAsync(@NotNull ActivePart part, SchedulerRunnable runnable, long delay, long period) {
        if (part instanceof SpawnedDisplayEntityPart sp){
            return entityRunTimerAsync(sp.getEntity(), runnable, delay, period);
        }
        else{
            return runTimerAsync(runnable, delay, period);
        }
    }
}
