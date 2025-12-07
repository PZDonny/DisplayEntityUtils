package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.donnypz.displayentityutils.utils.version.VersionUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;


class DisplayTransformation extends Transformation{
    private SpawnedDisplayEntityPart.PartType type;
    private Object data;


    private DisplayTransformation(Transformation transformation){
        super(transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
    }

    DisplayTransformation(@NotNull Vector3f translation, @NotNull Quaternionf leftRotation, @NotNull Vector3f scale, @NotNull Quaternionf rightRotation){
        super(translation, leftRotation, scale, rightRotation);
    }

    static DisplayTransformation get(ActivePart part){
        DisplayTransformation dTransform = new DisplayTransformation(part.getTransformation());
        if (part.type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            dTransform.type = SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY;
            dTransform.data = part.getBlockDisplayBlock();
        }
        else if (part.type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
            dTransform.type = SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY;
            dTransform.data = part.getItemDisplayItem();
        }
        else{
            dTransform.type = SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY;
            dTransform.data = part.getTextDisplayText();
        }
        return dTransform;
    }

    static DisplayTransformation get(@NotNull Vector3f translation,
                                     @NotNull Quaternionf leftRotation,
                                     @NotNull Vector3f scale,
                                     @NotNull Quaternionf rightRotation,
                                     Serializable data,
                                     SpawnedDisplayEntityPart.PartType type){
        DisplayTransformation displayTransformation = new DisplayTransformation(translation, leftRotation, scale, rightRotation);
        displayTransformation.setData(type, data);
        return displayTransformation;
    }

    void applyData(Display display){
        if (!DisplayUtils.isInLoadedChunk(display)){
            return;
        }
        if (type == null || data == null){
            return;
        }
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            ((TextDisplay) display).text((Component) data);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            ((BlockDisplay) display).setBlock((BlockData) data);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
            ((ItemDisplay) display).setItemStack((ItemStack) data);
        }
    }

    void applyData(ActivePart part){
        if (type == null || data == null){
            return;
        }
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            part.setTextDisplayText((Component) data);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            part.setBlockDisplayBlock((BlockData) data);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
            part.setItemDisplayItem((ItemStack) data);
        }
    }

    void applyData(ActivePart part, Collection<Player> players){
        if (type == null || data == null){
            return;
        }
        PacketAttributeContainer container = new PacketAttributeContainer();
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            container.setAttribute(DisplayAttributes.TextDisplay.TEXT, (Component) data);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            container.setAttribute(DisplayAttributes.BlockDisplay.BLOCK_STATE, (BlockData) data);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY){
            container.setAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK, (ItemStack) data);
        }

        container.sendAttributesUsingPlayers(players, part.getEntityId());
    }

    private void setData(SpawnedDisplayEntityPart.PartType type, Serializable data){
        this.type = type;
        if (data == null){
            return;
        }
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            String serialized = (String) data;
            this.data = MiniMessage.miniMessage().deserialize(serialized);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            this.data = VersionUtils.createBlockData((String) data);
        }
        else{
            this.data = ItemStack.deserializeBytes((byte[]) data);
        }
    }

    @Nullable Object getData(){
        return data;
    }

    @Nullable Serializable getSerializableData(){
        if (type == null || data == null){
            return null;
        }
        if (type == SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY){
            return MiniMessage.miniMessage().serialize((Component) data);
        }
        else if (type == SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY){
            return ((BlockData) data).getAsString();
        }
        else{
            return ((ItemStack) data).serializeAsBytes();
        }
    }

    SpawnedDisplayEntityPart.PartType getType(){
        return type;
    }

    boolean isSimilar(Transformation transformation){
        if (transformation == this){
            return true;
        }
        if (transformation == null){
            return false;
        }
        if (!Objects.equals(this.getTranslation(), transformation.getTranslation())) {
            return false;
        }
        if (!Objects.equals(this.getLeftRotation(), transformation.getLeftRotation())) {
            return false;
        }
        if (!Objects.equals(this.getScale(), transformation.getScale())) {
            return false;
        }
        return Objects.equals(this.getRightRotation(), transformation.getRightRotation());
    }

    boolean isSimilar(ActivePart part){
        if (part instanceof PacketDisplayEntityPart p){
            return isSimilar(p.getTransformation());
        }
        return isSimilar(((Display) ((SpawnedDisplayEntityPart) part).getEntity()).getTransformation());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final DisplayTransformation other = (DisplayTransformation) obj;

        if (this.type != other.type){
            return false;
        }

        if (!toTransformation().equals(other.toTransformation())){
            return false;
        }

        return Objects.equals(data, other.data);
    }

    Transformation toTransformation(){
        return new Transformation(getTranslation(), getLeftRotation(), getScale(), getRightRotation());
    }
}
