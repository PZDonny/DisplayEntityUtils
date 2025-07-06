package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import org.joml.Quaternionf;

public class QuaternionDisplayAttribute extends DisplayAttribute<Quaternionf, Quaternion4f>{
    QuaternionDisplayAttribute(int index) {
        super(index, Quaternionf.class, Quaternion4f.class, EntityDataTypes.QUATERNION);
    }


    @Override
    public Quaternion4f getOutputValue(Quaternionf q) {
        return new Quaternion4f(q.x, q.y, q.z, q.w);
    }
}
