package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

abstract class DisplayEntitySpecifics implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;

    private ArrayList<String> partTags = new ArrayList<>(); //Legacy Part Tags (Using Scoreboard Only)
    private final UUID partUUID;
    SerialTransformation serialTransformation;
    private final Display.Billboard billboard;
    private final float viewRange;
    private final float shadowRadius;
    private final float shadowStrength;
    private final float displayWidth;
    private final float displayHeight;
    private int brightnessBlockLight = -1;
    private int brightnessSkyLight = -1;

    private int glowColorOverride = Color.WHITE.asRGB();

    DisplayEntitySpecifics(Display displayEntity){
        //this.partTags = DisplayUtils.getPartTags(displayEntity);
        this.partUUID = DisplayUtils.getPartUUID(displayEntity);
        this.serialTransformation = new SerialTransformation(displayEntity.getTransformation());
        this.billboard = displayEntity.getBillboard();
        this.viewRange = displayEntity.getViewRange();
        this.shadowRadius = displayEntity.getShadowRadius();
        this.shadowStrength = displayEntity.getShadowStrength();
        this.displayWidth = displayEntity.getDisplayWidth();
        this.displayHeight = displayEntity.getDisplayHeight();
        if (displayEntity.getGlowColorOverride() != null && displayEntity.getGlowColorOverride().asRGB() != Color.WHITE.asRGB()){
            this.glowColorOverride = displayEntity.getGlowColorOverride().asRGB();
        }
        Display.Brightness brightness = displayEntity.getBrightness();
        if (brightness != null) {
            this.brightnessBlockLight = brightness.getBlockLight();
            this.brightnessSkyLight = brightness.getSkyLight();
        }
    }

    DisplayEntitySpecifics(PacketDisplayEntityPart part){
        //this.partTags = DisplayUtils.getPartTags(displayEntity);
        this.partUUID = part.partUUID;
        this.serialTransformation = new SerialTransformation(part.getTransformation());
        PacketAttributeContainer c = part.attributeContainer;
        this.billboard = c.getAttributeOrDefault(DisplayAttributes.BILLBOARD, Display.Billboard.FIXED);
        this.viewRange = c.getAttributeOrDefault(DisplayAttributes.VIEW_RANGE, 1f);
        this.shadowRadius = c.getAttributeOrDefault(DisplayAttributes.Shadow.RADIUS, 0f);
        this.shadowStrength = c.getAttributeOrDefault(DisplayAttributes.Shadow.STRENGTH, 0f);
        this.displayWidth = c.getAttributeOrDefault(DisplayAttributes.Culling.WIDTH, 0f);
        this.displayHeight = c.getAttributeOrDefault(DisplayAttributes.Culling.HEIGHT, 0f);
        this.glowColorOverride = c.getAttributeOrDefault(DisplayAttributes.GLOW_COLOR_OVERRIDE, Color.WHITE).asRGB();
        Display.Brightness brightness = c.getAttribute(DisplayAttributes.BRIGHTNESS);
        if (brightness != null) {
            this.brightnessBlockLight = brightness.getBlockLight();
            this.brightnessSkyLight = brightness.getSkyLight();
        }
    }

    List<String> getLegacyPartTags() {
        return partTags;
    }

    SerialTransformation getSerialTransformation() {
        return serialTransformation;
    }

    Display.Billboard getBillboard() {
        return billboard;
    }

    float getViewRange() {
        return viewRange;
    }

    float getShadowRadius() {
        return shadowRadius;
    }

    float getShadowStrength() {
        return shadowStrength;
    }

    float getDisplayWidth() {
        return displayWidth;
    }

    float getDisplayHeight() {
        return displayHeight;
    }

    int getBrightnessBlockLight() {
        return brightnessBlockLight;
    }

    int getBrightnessSkyLight() {
        return brightnessSkyLight;
    }

    int getGlowColorOverride(){
        return glowColorOverride;
    }

    PacketAttributeContainer getAttributeContainer(){
        Transformation transformation = serialTransformation.toTransformation();

        PacketAttributeContainer attributeContainer = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.BILLBOARD, billboard)
                .setAttribute(DisplayAttributes.VIEW_RANGE, viewRange)
                .setAttribute(DisplayAttributes.Shadow.RADIUS, shadowRadius)
                .setAttribute(DisplayAttributes.Shadow.STRENGTH, shadowStrength)
                .setAttribute(DisplayAttributes.Culling.WIDTH, displayWidth)
                .setAttribute(DisplayAttributes.Culling.HEIGHT, displayHeight)
                .setAttribute(DisplayAttributes.Transform.LEFT_ROTATION, transformation.getLeftRotation())
                .setAttribute(DisplayAttributes.Transform.RIGHT_ROTATION, transformation.getRightRotation())
                .setAttribute(DisplayAttributes.Transform.SCALE, transformation.getScale())
                .setAttribute(DisplayAttributes.Transform.TRANSLATION, transformation.getTranslation());

        if (brightnessBlockLight != -1 && brightnessSkyLight != -1)
            attributeContainer.setAttribute(DisplayAttributes.BRIGHTNESS, new Display.Brightness(brightnessBlockLight, brightnessSkyLight));
        applyToAttributeContainer(attributeContainer);
        return attributeContainer;
    }

    protected abstract void applyToAttributeContainer(PacketAttributeContainer attributeContainer);

    void apply(DisplayEntity displayEntity, Display display){
        display.setTransformation(serialTransformation.toTransformation());
        display.setBillboard(billboard);
        display.setViewRange(viewRange);
        display.setShadowRadius(shadowRadius);
        display.setShadowStrength(shadowStrength);
        display.setDisplayWidth(displayWidth);
        display.setDisplayHeight(displayHeight);
        if (glowColorOverride != Color.WHITE.asRGB()){
            display.setGlowColorOverride(Color.fromRGB(glowColorOverride));
        }
        if (brightnessBlockLight != -1 && brightnessSkyLight != -1){
            display.setBrightness(new Display.Brightness(brightnessBlockLight, brightnessSkyLight));
        }

        if (partTags != null){
            for (String partTag : partTags){
                display.addScoreboardTag(partTag);
            }
        }


        if (displayEntity.persistentDataContainer != null){
            try{
                display.getPersistentDataContainer().readFromBytes(displayEntity.persistentDataContainer);
            }
            catch(IOException ignore){}
        }

        if (partUUID != null){
            display.getPersistentDataContainer().set(DisplayAPI.getPartUUIDKey(), PersistentDataType.STRING, partUUID.toString());
        }

    }
}
