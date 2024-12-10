package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.Objects;


@ApiStatus.Internal
public class DisplayTransformation extends Transformation{
    private SpawnedDisplayEntityPart.PartType type;
    private Object data;


    private DisplayTransformation(Transformation transformation){
        super(transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation());
    }

    public DisplayTransformation(@NotNull Vector3f translation, @NotNull Quaternionf leftRotation, @NotNull Vector3f scale, @NotNull Quaternionf rightRotation){
        super(translation, leftRotation, scale, rightRotation);
    }

    static DisplayTransformation get(Display display){
        DisplayTransformation dTransform = new DisplayTransformation(display.getTransformation());
        if (display instanceof BlockDisplay blockDisplay){
            dTransform.type = SpawnedDisplayEntityPart.PartType.BLOCK_DISPLAY;
            dTransform.data = blockDisplay.getBlock();
        }
        else if (display instanceof ItemDisplay itemDisplay){
            dTransform.type = SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY;
            dTransform.data = itemDisplay.getItemStack();
        }
        else{
            dTransform.type = SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY;
            dTransform.data = ((TextDisplay) display).text();
        }
        return dTransform;
    }

    public static DisplayTransformation get(@NotNull Vector3f translation, @NotNull Quaternionf leftRotation, @NotNull Vector3f scale, @NotNull Quaternionf rightRotation, Serializable data, SpawnedDisplayEntityPart.PartType type){
        DisplayTransformation displayTransformation = new DisplayTransformation(translation, leftRotation, scale, rightRotation);
        displayTransformation.setData(type, data);
        return displayTransformation;
    }

    void applyData(Display display){
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
            this.data = Bukkit.createBlockData((String) data);
        }
        else{
            this.data = ItemStack.deserializeBytes((byte[]) data);
        }
    }

    public @Nullable Object getData(){
        return data;
    }

    public @Nullable Serializable getSerializableData(){
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

    public SpawnedDisplayEntityPart.PartType getType(){
        return type;
    }

    public boolean isSimilar(Transformation transformation){
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

    public Transformation toTransformation(){
        return new Transformation(getTranslation(), getLeftRotation(), getScale(), getRightRotation());
    }
}
