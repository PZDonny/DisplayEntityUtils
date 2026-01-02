package net.donnypz.displayentityutils.utils.packet.attributes;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.PlayerModelType;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import org.bukkit.profile.PlayerTextures;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResolvableProfileDisplayAttribute extends DisplayAttribute<ResolvableProfile, ItemProfile> {
    protected ResolvableProfileDisplayAttribute(int index) {
        super(index, ResolvableProfile.class, ItemProfile.class, EntityDataTypes.RESOLVABLE_PROFILE);
    }

    @Override
    public ItemProfile getOutputValue(ResolvableProfile value) {
        String name = value.name();
        UUID uuid = value.uuid();

        List<ItemProfile.Property> properties = new ArrayList<>();
        for (ProfileProperty prop : value.properties()){
            properties.add(new ItemProfile.Property(prop.getName(), prop.getValue(), prop.getSignature()));
        }
        ItemProfile.SkinPatch skinPatch;
        ResolvableProfile.SkinPatch bukkitSkinPatch = value.skinPatch();
        if (bukkitSkinPatch.isEmpty()){
            skinPatch = ItemProfile.SkinPatch.EMPTY;
        }
        else{
            PlayerModelType modelType;
            if (bukkitSkinPatch.model() == null){
                modelType = null;
            }
            else{
                modelType = (bukkitSkinPatch.model() == PlayerTextures.SkinModel.CLASSIC) ? PlayerModelType.WIDE : PlayerModelType.SLIM;
            }
            Key bodyKey = bukkitSkinPatch.body();
            Key capeKey = bukkitSkinPatch.cape();
            Key elytraKey = bukkitSkinPatch.elytra();

            skinPatch = new ItemProfile.SkinPatch(
                    bodyKey == null ? null : new ResourceLocation(bodyKey),
                    capeKey == null ? null : new ResourceLocation(capeKey),
                    elytraKey == null ? null : new ResourceLocation(elytraKey),
                    modelType);
        }

        return new ItemProfile(name, uuid, properties, skinPatch);
    }
}
