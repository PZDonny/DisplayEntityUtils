package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class OptionalComponentDisplayAttribute extends DisplayAttribute<Component, Optional<Component>> {
    protected OptionalComponentDisplayAttribute(int index) {
        super(index, Component.class, EntityDataTypes.OPTIONAL_ADV_COMPONENT);
    }

    @Override
    public Optional<Component> getOutputValue(Component value) {
        return Optional.of(value);
    }
}