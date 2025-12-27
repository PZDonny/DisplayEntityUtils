package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.attribute.Attribute;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;

public class AttributeDisplayAttribute extends DisplayAttribute<Float, Float>{

    Attribute attribute;
    protected AttributeDisplayAttribute(Attribute attribute) {
        super(0, Float.class, EntityDataTypes.FLOAT);
        this.attribute = attribute;
        setAttributeType(AttributeType.ATTRIBUTE);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public Float getOutputValue(Float value) {
        return value;
    }
}
