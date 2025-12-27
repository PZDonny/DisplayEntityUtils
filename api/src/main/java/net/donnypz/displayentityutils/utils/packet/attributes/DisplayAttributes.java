package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A class containing all {@link DisplayAttribute}s that apply to eligible part type entities
 */
public final class DisplayAttributes {

    public static final GlowingDisplayAttribute GLOWING = new GlowingDisplayAttribute(0);
    public static final OptionalComponentDisplayAttribute CUSTOM_NAME = new OptionalComponentDisplayAttribute(2);
    public static final BasicDisplayAttribute<Boolean> CUSTOM_NAME_VISIBLE = new BasicDisplayAttribute<>(3, Boolean.class, EntityDataTypes.BOOLEAN);

    private DisplayAttributes(){}

    public static final class Equipment{
        public static final EquipmentAttribute HELMET = new EquipmentAttribute(EquipmentSlot.HELMET);
        public static final EquipmentAttribute CHESTPLATE = new EquipmentAttribute(EquipmentSlot.CHEST_PLATE);
        public static final EquipmentAttribute LEGGINGS = new EquipmentAttribute(EquipmentSlot.LEGGINGS);
        public static final EquipmentAttribute BOOTS = new EquipmentAttribute(EquipmentSlot.BOOTS);
        public static final EquipmentAttribute MAIN_HAND = new EquipmentAttribute(EquipmentSlot.MAIN_HAND);
        public static final EquipmentAttribute OFF_HAND = new EquipmentAttribute(EquipmentSlot.OFF_HAND);
        public static final EquipmentAttribute BODY = new EquipmentAttribute(EquipmentSlot.BODY);

        public static EquipmentAttribute getAttribute(@NotNull org.bukkit.inventory.EquipmentSlot slot){
            switch (slot){
                case HEAD -> {
                    return HELMET;
                }
                case CHEST -> {
                    return CHESTPLATE;
                }
                case LEGS ->  {
                    return LEGGINGS;
                }
                case FEET -> {
                    return BOOTS;
                }
                case BODY -> {
                    return BODY;
                }
                case HAND -> {
                    return MAIN_HAND;
                }
                case OFF_HAND -> {
                    return OFF_HAND;
                }
                default -> {
                    throw new IllegalArgumentException("Invalid/Unexpected slot type");
                }
            }
        }
    }

    public static final class Interpolation {
        public static final BasicDisplayAttribute<Integer> DELAY = new BasicDisplayAttribute<>(8, Integer.class, EntityDataTypes.INT);
        public static final BasicDisplayAttribute<Integer> DURATION = new BasicDisplayAttribute<>(9, Integer.class, EntityDataTypes.INT);
    }

    public static final BasicDisplayAttribute<Integer> TELEPORTATION_DURATION = new BasicDisplayAttribute<>(10, Integer.class, EntityDataTypes.INT);

    public static final class Transform {
        public static final VectorDisplayAttribute TRANSLATION = new VectorDisplayAttribute(11);
        public static final VectorDisplayAttribute SCALE = new VectorDisplayAttribute(12);
        public static final QuaternionDisplayAttribute LEFT_ROTATION = new QuaternionDisplayAttribute(13);
        public static final QuaternionDisplayAttribute RIGHT_ROTATION = new QuaternionDisplayAttribute(14);
    }

    public static final BillboardDisplayAttribute BILLBOARD = new BillboardDisplayAttribute(15);

    public static final BrightnessDisplayAttribute BRIGHTNESS = new BrightnessDisplayAttribute(16);

    public static final BasicDisplayAttribute<Float> VIEW_RANGE = new BasicDisplayAttribute<>(17, Float.class, EntityDataTypes.FLOAT);

    public static final class Shadow {
        public static final BasicDisplayAttribute<Float> RADIUS = new BasicDisplayAttribute<>(18, Float.class, EntityDataTypes.FLOAT);
        public static final BasicDisplayAttribute<Float> STRENGTH = new BasicDisplayAttribute<>(19, Float.class, EntityDataTypes.FLOAT);
    }

    public static final class Culling{
        public static final BasicDisplayAttribute<Float> WIDTH = new BasicDisplayAttribute<>(20, Float.class, EntityDataTypes.FLOAT);
        public static final BasicDisplayAttribute<Float> HEIGHT = new BasicDisplayAttribute<>(21, Float.class, EntityDataTypes.FLOAT);
    }

    public static final ColorDisplayAttribute GLOW_COLOR_OVERRIDE = new ColorDisplayAttribute(22);

    public static final class BlockDisplay{
        public static final BlockStateDisplayAttribute BLOCK_STATE = new BlockStateDisplayAttribute(23);
    }

    public static final class ItemDisplay{
        public static final ItemStackDisplayAttribute ITEMSTACK = new ItemStackDisplayAttribute(23);
        public static final ItemTransformDisplayAttribute ITEM_DISPLAY_TRANSFORM = new ItemTransformDisplayAttribute(24);
    }

    public static final class TextDisplay{
        public static final BasicDisplayAttribute<Component> TEXT = new BasicDisplayAttribute<>(23, Component.class, EntityDataTypes.ADV_COMPONENT);
        public static final BasicDisplayAttribute<Integer> LINE_WIDTH = new BasicDisplayAttribute<>(24, Integer.class, EntityDataTypes.INT);
        public static final ColorDisplayAttribute BACKGROUND_COLOR = new ColorDisplayAttribute(25);
        public static final BasicDisplayAttribute<Byte> TEXT_OPACITY_PERCENTAGE = new BasicDisplayAttribute<>(26, Byte.class, EntityDataTypes.BYTE);
        public static final TextOptionsDisplayAttribute EXTRA_TEXT_OPTIONS = new TextOptionsDisplayAttribute(27);
    }

    public static final class Interaction{
        public static final BasicDisplayAttribute<Float> WIDTH = new BasicDisplayAttribute<>(8, Float.class, EntityDataTypes.FLOAT);
        public static final BasicDisplayAttribute<Float> HEIGHT = new BasicDisplayAttribute<>(9, Float.class, EntityDataTypes.FLOAT);
        public static final BasicDisplayAttribute<Boolean> RESPONSIVE = new BasicDisplayAttribute<>(10, Boolean.class, EntityDataTypes.BOOLEAN);
    }

    public static final class Mannequin{
        public static final AttributeDisplayAttribute SCALE = new AttributeDisplayAttribute(Attributes.SCALE);
        public static final BasicDisplayAttribute<Boolean> NO_GRAVITY = new BasicDisplayAttribute<>(5, Boolean.class, EntityDataTypes.BOOLEAN);
        public static final PoseDisplayAttribute POSE = new PoseDisplayAttribute(6);
        public static final MainHandDisplayAttribute MAIN_HAND = new MainHandDisplayAttribute(15);
        public static final ResolvableProfileDisplayAttribute RESOLVABLE_PROFILE = new ResolvableProfileDisplayAttribute(17);
        public static final BasicDisplayAttribute<Boolean> IMMOVABLE = new BasicDisplayAttribute<>(18, Boolean.class, EntityDataTypes.BOOLEAN);
        public static final OptionalComponentDisplayAttribute BELOW_NAME = new OptionalComponentDisplayAttribute(19);
    }
}
