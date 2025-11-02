package net.donnypz.displayentityutils.utils.version.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public interface Scheduler {

    void run(Runnable runnable);

    void runAsync(Runnable runnable);

    Task runLater(Runnable runnable, long delay);

    Task runLaterAsync(Runnable runnable, long delay);

    Task runTimer(SchedulerRunnable runnable, long delay, long period);

    Task runTimerAsync(SchedulerRunnable runnable, long delay, long period);

    void entityRun(@NotNull Entity entity, Runnable runnable);

    void entityRunAsync(@NotNull Entity entity, Runnable runnable);

    Task entityRunLater(@NotNull Entity entity, Runnable runnable, long delay);

    Task entityRunLaterAsync(@NotNull Entity entity, Runnable runnable, long delay);

    Task entityRunTimer(@NotNull Entity entity, SchedulerRunnable runnable, long delay, long period);

    Task entityRunTimerAsync(@NotNull Entity entity, SchedulerRunnable runnable, long delay, long period);

    void partRun(@NotNull ActivePart part, Runnable runnable);

    void partRunAsync(@NotNull ActivePart part, Runnable runnable);

    Task partRunLater(@NotNull ActivePart part, Runnable runnable, long delay);

    Task partRunLaterAsync(@NotNull ActivePart part, Runnable runnable, long delay);

    Task partRunTimer(@NotNull ActivePart part, SchedulerRunnable runnable, long delay, long period);

    Task partRunTimerAsync(@NotNull ActivePart part, SchedulerRunnable runnable, long delay, long period);


    abstract class SchedulerRunnable implements Runnable{
        Task task;

        public void cancel(){
            task.cancel();
        }

    }

    class Task{
        private Object foliaTask;
        private BukkitTask bukkitTask;

        Task(Object foliaTask){
            this.foliaTask = foliaTask;
        }

        Task(BukkitTask bukkitTask){
            this.bukkitTask = bukkitTask;
        }

        public void cancel(){
            if (foliaTask != null){
                ((ScheduledTask) foliaTask).cancel();
            }
            else{
                bukkitTask.cancel();
            }
        }
    }
}
