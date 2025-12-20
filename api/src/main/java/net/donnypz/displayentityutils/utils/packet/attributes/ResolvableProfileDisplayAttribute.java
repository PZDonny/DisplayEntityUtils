package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;

public class ResolvableProfileDisplayAttribute extends DisplayAttribute<ResolvableProfile, ItemProfile> {
    protected ResolvableProfileDisplayAttribute(int index) {
        super(index, ResolvableProfile.class, ItemProfile.class, EntityDataTypes.RESOLVABLE_PROFILE);
    }

    @Override
    public ItemProfile getOutputValue(ResolvableProfile value) {
        return null;
    }
}
