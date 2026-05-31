package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

class BDEConvertEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String conversionId;
    private final DisplayEntityGroup savedGroup;
    private final SpawnedDisplayEntityGroup spawnedGroup;
    private final List<SpawnedDisplayAnimation> animations;
    private final boolean isSavedGroup;
    private final boolean isSavedAnimations;

    BDEConvertEvent(Player player,
                           String conversionId,
                           DisplayEntityGroup savedGroup,
                           SpawnedDisplayEntityGroup spawnedGroup,
                           List<SpawnedDisplayAnimation> animations,
                           boolean isSavedGroup,
                           boolean isSavedAnimations) {
        this.player = player;
        this.conversionId = conversionId;
        this.savedGroup = savedGroup;
        this.spawnedGroup = spawnedGroup;
        this.animations = Collections.unmodifiableList(animations);
        this.isSavedGroup = isSavedGroup;
        this.isSavedAnimations = isSavedAnimations;
    }

    /**
     * Get the player who initiated the conversion, if done through commands
     * @return a {@link Player} or null
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * Get the id used to identify a conversion process
     * @return a string or null
     */
    public @Nullable String getConversionId() {
        return conversionId;
    }

    /**
     * Get the {@link DisplayEntityGroup} representing the group created from the conversion
     * return a {@link DisplayEntityGroup}
     */
    public @NotNull DisplayEntityGroup getGroup(){
        return savedGroup;
    }

    /**
     * Get the group that created as a result of the conversion. Null if the group was despawned after conversion
     * @return a {@link SpawnedDisplayEntityGroup} or null
     */
    public @Nullable SpawnedDisplayEntityGroup getSpawnedGroup() {
        return spawnedGroup;
    }

    /**
     * Get the animations that were created as a result of the conversion
     * @return an unmodifiable list of {@link SpawnedDisplayAnimation}s
     */
    public @NotNull List<SpawnedDisplayAnimation> getAnimations() {
        return animations;
    }

    /**
     * Get whether the created group were saved after conversion
     * @return a boolean
     */
    public boolean isSavingGroups(){
        return isSavedGroup;
    }

    /**
     * Get whether the created animations were saved after conversion
     * @return a boolean
     */
    public boolean isSavingAnimations(){
        return isSavedAnimations;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
