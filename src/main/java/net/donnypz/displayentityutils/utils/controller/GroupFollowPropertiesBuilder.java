package net.donnypz.displayentityutils.utils.controller;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.DisplayStateMachine;
import net.donnypz.displayentityutils.utils.DisplayEntities.machine.MachineState;
import net.donnypz.displayentityutils.utils.FollowType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class GroupFollowPropertiesBuilder {
    private String id;
    private FollowType followType;
    private int unregisterDelay;
    private boolean pivotInteractions;
    private boolean pivotDisplays;
    private float yDisplayAdjustPercentage = 100;
    private float zDisplayAdjustPercentage = 100;
    private int teleportationDuration;
    private Collection<String> partTags = new HashSet<>();
    private Set<String> filteredStates = new HashSet<>();
    private boolean filterBlacklist = false;
    private boolean flip;

    GroupFollowPropertiesBuilder(){}

    GroupFollowPropertiesBuilder(@NotNull FollowType followType){
        this.followType = followType;
    }

    /**
     * Set the id of the {@link GroupFollowProperties}. This should be set if using this follow property on a {@link DisplayController}
     * @param id the id
     */
    public GroupFollowPropertiesBuilder setId(@NotNull String id) {
        this.id = id;
        return this;
    }

    /**
     * Set the {@link FollowType} of the {@link GroupFollowProperties}
     * @param followType the follow type
     */
    public GroupFollowPropertiesBuilder setFollowType(@NotNull FollowType followType) {
        this.followType = followType;
        return this;
    }

    /**
     * Set how long after the followed entity dies, that the associated group should be removed.
     * <br>
     * The group is unregistered if it is a {@link SpawnedDisplayEntityGroup}
     * <br>
     * The group is hidden from all players if it is a {@link PacketDisplayEntityGroup}
     * @param unregisterDelay
     */
    public GroupFollowPropertiesBuilder setUnregisterDelay(int unregisterDelay) {
        this.unregisterDelay = unregisterDelay;
        return this;
    }

    /**
     * Set whether interaction entities should pivot when the followed entity's yaw changes
     * @param pivotInteractions
     * @return this
     */
    public GroupFollowPropertiesBuilder setPivotInteractions(boolean pivotInteractions) {
        this.pivotInteractions = pivotInteractions;
        return this;
    }

    /**
     * Set whether display entities should be realigned by the {@code yPivotOffsetPercentage}
     * and {@code zPivotOffsetPercentage} of the {@link GroupFollowProperties} in order to keep centering.
     * @param pivotDisplays
     * @return this
     */
    public GroupFollowPropertiesBuilder setAdjustDisplays(boolean pivotDisplays) {
        this.pivotDisplays = pivotDisplays;
        return this;
    }

    /**
     * Set the vertical adjustment percentage for parts that follow an entity's pitch {@link FollowType#PITCH_AND_YAW} or {@link FollowType#PITCH}.
     * @param yDisplayAdjustPercentage the percentage
     * @return this
     */
    public GroupFollowPropertiesBuilder setYDisplayAdjustPercentage(float yDisplayAdjustPercentage) {
        this.yDisplayAdjustPercentage = yDisplayAdjustPercentage;
        return this;
    }

    /**
     * Set the horizontal adjustment percentage for parts that follow an entity's pitch {@link FollowType#PITCH_AND_YAW} or {@link FollowType#PITCH}.
     * @param zDisplayAdjustPercentage the percentage
     * @return this
     */
    public GroupFollowPropertiesBuilder setZDisplayAdjustPercentage(float zDisplayAdjustPercentage) {
        this.zDisplayAdjustPercentage = zDisplayAdjustPercentage;
        return this;
    }

    /**
     * Set the teleportation duration that affected group parts will respect when following an entity
     * @param teleportationDuration the teleport duration
     * @return this
     */
    public GroupFollowPropertiesBuilder setTeleportationDuration(int teleportationDuration) {
        this.teleportationDuration = teleportationDuration;
        return this;
    }

    /**
     * Set the part tags that should respect the {@link GroupFollowProperties}
     * @param partTags the tags
     * @return this
     */
    public GroupFollowPropertiesBuilder setPartTags(@NotNull Collection<String> partTags) {
        this.partTags = partTags;
        return this;
    }

    /**
     * Set the {@link MachineState}s that will respect the {@link GroupFollowProperties} when used in a {@link DisplayController} or {@link DisplayStateMachine}
     * @param filteredStates the filtered states by their ids
     * @return this
     */
    public GroupFollowPropertiesBuilder setFilteredStates(@NotNull Set<String> filteredStates) {
        this.filteredStates = filteredStates;
        return this;
    }

    /**
     * Set whether the filtered {@link MachineState}s should be included (whitelisted) or excluded (blacklisted)
     * @param filterBlacklist whether the states should be blacklisted
     * @return this
     */
    public GroupFollowPropertiesBuilder setFilterBlacklist(boolean filterBlacklist) {
        this.filterBlacklist = filterBlacklist;
        return this;
    }

    /**
     * Determine if the yaw and pitch of the followed entity's looking direction will be flipped.
     * The pitch will be inverted and the yaw will have 180 added to it if true.
     * This is useful if the {@link ActiveGroup} is backwards.
     * @param flip
     * @return this
     */
    public GroupFollowPropertiesBuilder setFlip(boolean flip) {
        this.flip = flip;
        return this;
    }

    public GroupFollowProperties build(){
        GroupFollowProperties properties = new GroupFollowProperties();
        properties.id = id;
        properties.followType = followType;
        properties.unregisterDelay = unregisterDelay;
        properties.pivotInteractions = pivotInteractions;
        properties.adjustDisplays = pivotDisplays;
        properties.yDisplayAdjustPercentage = yDisplayAdjustPercentage;
        properties.zDisplayAdjustPercentage = zDisplayAdjustPercentage;
        properties.teleportationDuration = teleportationDuration;
        properties.partTags = new HashSet<>(partTags);
        properties.filteredStates = new HashSet<>(filteredStates);
        properties.filterBlacklist = filterBlacklist;
        properties.flip = flip;
        return properties;
    }
}
