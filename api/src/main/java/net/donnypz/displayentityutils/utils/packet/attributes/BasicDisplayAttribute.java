package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;

public class BasicDisplayAttribute<T> extends DisplayAttribute<T, T>{

    BasicDisplayAttribute(int index, Class<T> type, EntityDataType<?> entityDataType) {
        super(index, type, entityDataType);
    }

    @Override
    public T getOutputValue(T value){
        return value;
    }
}
