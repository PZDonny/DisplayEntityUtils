package net.donnypz.displayentityutils.utils;

import org.bukkit.entity.LivingEntity;

public enum FollowType {
    /**
     * Follow an entity's looking direction based on its pitch and yaw
     */
    PITCH_AND_YAW,
    /**
     * Follow an entity's looking direction based on its pitch
     */
    PITCH,
    /**
     * Follow an entity's looking direction based on its yaw
     */
    YAW,
    /**
     * Follow a {@link LivingEntity}'s body yaw
     */
    BODY,
}