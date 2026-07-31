package net.donnypz.displayentityutils.utils.version.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public interface Scheduler {

    void run(@NotNull Runnable runnable);

    void runAsync(@NotNull Runnable runnable);

    Task runLater(@NotNull Runnable runnable, long delay);

    Task runLaterAsync(@NotNull Runnable runnable, long delay);

    Task runTimer(@NotNull SchedulerRunnable runnable, long delay, long period);

    Task runTimerAsync(@NotNull SchedulerRunnable runnable, long delay, long period);

    void entityRun(@NotNull Entity entity,@NotNull Runnable runnable);

    void entityRunAsync(@NotNull Entity entity, @NotNull Runnable runnable);

    Task entityRunLater(@NotNull Entity entity, @NotNull Runnable runnable, long delay);

    Task entityRunLaterAsync(@NotNull Entity entity, @NotNull Runnable runnable, long delay);

    Task entityRunTimer(@NotNull Entity entity, @NotNull SchedulerRunnable runnable, long delay, long period);

    Task entityRunTimerAsync(@NotNull Entity entity, @NotNull SchedulerRunnable runnable, long delay, long period);

    void partRun(ActivePart part, @NotNull Runnable runnable);

    void partRunAsync(ActivePart part, @NotNull Runnable runnable);

    Task partRunLater(ActivePart part, @NotNull Runnable runnable, long delay);

    Task partRunLaterAsync(ActivePart part, @NotNull Runnable runnable, long delay);

    Task partRunTimer(ActivePart part, @NotNull SchedulerRunnable runnable, long delay, long period);

    Task partRunTimerAsync(ActivePart part, @NotNull SchedulerRunnable runnable, long delay, long period);


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

    class EmptyTask extends Task{
        EmptyTask(){
            super((Object) null);
        }

        @Override
        public void cancel(){}

    }
}
