package net.donnypz.displayentityutils.utils.DisplayEntities.concurrent;

import net.donnypz.displayentityutils.events.GroupTranslateEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Contains the completable futures of an {@link ActiveGroup} and it's non-display parts.<br>
 * Provided after using {@link ActiveGroup#teleportSafe(Location, boolean)}<br>
 * Can be used to determine teleportation completion.
 */
public class GroupTeleportCompletableFuture {
    CompletableFuture<Boolean> groupCompletableFuture;
    Collection<CompletableFuture<Boolean>> nonDisplayFutures;
    boolean eventCancelled;

    private GroupTeleportCompletableFuture(@Nullable CompletableFuture<Boolean> groupCompletableFuture,
                                           @Nullable Collection<CompletableFuture<Boolean>> nonDisplayFutures,
                                           boolean eventCancelled) {
        this.groupCompletableFuture = groupCompletableFuture;
        this.nonDisplayFutures = nonDisplayFutures;
        this.eventCancelled = eventCancelled;
    }

    @ApiStatus.Internal
    public static GroupTeleportCompletableFuture cancelled() {
        return new GroupTeleportCompletableFuture(null, null, true);
    }

    @ApiStatus.Internal
    public static GroupTeleportCompletableFuture create(@NotNull CompletableFuture<Boolean> groupCompletableFuture,
                                                        @NotNull Collection<CompletableFuture<Boolean>> nonDisplayFutures) {
        return new GroupTeleportCompletableFuture(groupCompletableFuture, nonDisplayFutures, false);
    }

    /**
     * Get the completable future representing the group's teleport completion
     * @return null if {@link #isEventCancelled()} or {@link #isSyncTeleport()} are true
     */
    public @Nullable CompletableFuture<Boolean> getGroupCompletableFuture() {
        return groupCompletableFuture;
    }

    /**
     * Get the completable future representing every non-display entity that repositioned with the group's teleport
     * @return null if {@link #isEventCancelled()} or {@link #isSyncTeleport()} are true
     */
    public @Nullable Collection<CompletableFuture<Boolean>> getNonDisplayFutures() {
        return nonDisplayFutures;
    }

    /**
     * Get whether any non-display entities were teleported
     * @return a boolean
     */
    public boolean hasNonDisplayFutures(){
        return nonDisplayFutures != null && !nonDisplayFutures.isEmpty();
    }

    /**
     * Get whether the {@link GroupTranslateEvent} was cancelled
     * @return a boolean
     */
    public boolean isEventCancelled() {
        return eventCancelled;
    }

    public boolean isSyncTeleport(){
        return !eventCancelled && groupCompletableFuture == null;
    }

    /**
     * Block the current thread until all futures are completed
     */
    public void block() {
        if (eventCancelled || isSyncTeleport()) return;
        try {
            groupCompletableFuture.get();
            for (CompletableFuture<Boolean> nonDisplayFuture : nonDisplayFutures) {
                nonDisplayFuture.get();
            }
        } catch (ExecutionException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Block the current thread until all futures are completed
     * @param timeout how long the futures should wait before timing out
     * @param timeUnit the {@link TimeUnit}
     */
    public void block(long timeout, TimeUnit timeUnit) {
        if (eventCancelled || isSyncTeleport()) return;
        try {
            groupCompletableFuture.get(timeout, timeUnit);
            for (CompletableFuture<Boolean> nonDisplayFuture : nonDisplayFutures) {
                nonDisplayFuture.get(timeout, timeUnit);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            throw new RuntimeException(ex);
        }
    }
}
