package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;

public abstract class DisplayAttribute<T, V> {

    int index;
    Class<T> inputType;
    Class<V> outputType;
    EntityDataType<?> entityDataType;
    AttributeType type = AttributeType.METADATA;

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

    DisplayAttribute<T, V> setAttributeType(AttributeType type){
        this.type = type;
        return this;
    }

    public boolean isMetadata(){
        return type == AttributeType.METADATA;
    }

    public boolean isEquipment(){
        return type == AttributeType.EQUIPMENT;
    }

    public boolean isAttribute(){
        return type == AttributeType.ATTRIBUTE;
    }




    enum AttributeType{
        METADATA,
        EQUIPMENT,
        ATTRIBUTE
    }
}
