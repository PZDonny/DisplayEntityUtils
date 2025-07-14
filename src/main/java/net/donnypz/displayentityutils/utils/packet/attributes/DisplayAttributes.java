package net.donnypz.displayentityutils.utils.packet.attributes;

import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import net.kyori.adventure.text.Component;

/**
 * A class containing all {@link DisplayAttribute}s that apply to both Display and Interaction entities
 */
public final class DisplayAttributes {

    public static final GlowingDisplayAttribute GLOWING = new GlowingDisplayAttribute(0);

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

}
