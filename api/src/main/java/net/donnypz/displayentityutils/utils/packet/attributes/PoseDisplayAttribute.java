package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.bukkit.entity.Pose;

public class PoseDisplayAttribute extends DisplayAttribute<Pose, Integer>{
    PoseDisplayAttribute(int index) {
        super(index, Pose.class, Integer.class, EntityDataTypes.INT);
    }

    @Override
    public Integer getOutputValue(Pose pose) {
        return pose == null ? Pose.STANDING.ordinal() : pose.ordinal();
    }
}
