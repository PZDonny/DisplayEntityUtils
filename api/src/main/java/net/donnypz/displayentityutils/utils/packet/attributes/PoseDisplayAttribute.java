package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.pose.EntityPose;
import org.bukkit.entity.Pose;

public class PoseDisplayAttribute extends DisplayAttribute<Pose, EntityPose>{
    PoseDisplayAttribute(int index) {
        super(index, Pose.class, EntityPose.class, EntityDataTypes.ENTITY_POSE);
    }

    @Override
    public EntityPose getOutputValue(Pose pose) {
        if (pose == null) return EntityPose.STANDING;
        try{
            return EntityPose.valueOf(pose.name());
        }
        catch(IllegalArgumentException e){
            return EntityPose.STANDING;
        }
    }
}
