package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import org.joml.Vector3f;

public class VectorDisplayAttribute extends DisplayAttribute<Vector3f, com.github.retrooper.packetevents.util.Vector3f>{

    VectorDisplayAttribute(int index) {
        super(index, Vector3f.class, com.github.retrooper.packetevents.util.Vector3f.class, EntityDataTypes.VECTOR3F);
    }

    @Override
    public com.github.retrooper.packetevents.util.Vector3f getOutputValue(Vector3f v) {
        return new com.github.retrooper.packetevents.util.Vector3f(v.x, v.y, v.z);
    }
}
