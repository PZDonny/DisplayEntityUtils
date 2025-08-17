package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;

public abstract class DisplayAttribute<T, V> {

    int index;
    Class<T> inputType;
    Class<V> outputType;
    EntityDataType<?> entityDataType;

    protected DisplayAttribute(int index, Class<T> type, EntityDataType<?> entityDataType){
        this.index = index;
        this.inputType = type;
        this.entityDataType = entityDataType;
    }

    protected DisplayAttribute(int index, Class<T> inputType, Class<V> outputType, EntityDataType<?> entityDataType){
        this.index = index;
        this.inputType = inputType;
        this.outputType = outputType;
        this.entityDataType = entityDataType;
    }

    public EntityDataType<?> getEntityDataType(){
        return entityDataType;
    }

    public int getIndex(){
        return index;
    }

    public Class<T> getInputType(){
        return inputType;
    }

    public Class<V> getOutputType(){
        return outputType;
    }

    public abstract V getOutputValue(T value);
}
