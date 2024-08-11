package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

abstract class DisplayEntitySpecifics implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;

    private final ArrayList<String> partTags;
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
        this.partTags = DisplayUtils.getPartTags(displayEntity);
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
            this.brightnessBlockLight = displayEntity.getBrightness().getBlockLight();
            this.brightnessSkyLight = displayEntity.getBrightness().getSkyLight();
        }
    }

    public ArrayList<String> getPartTags() {
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

    void updateDisplay(DisplayEntity displayEntity, Display display){
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
        for (String partTag : partTags){
            display.addScoreboardTag(partTag);
        }
        if (partUUID != null){
            display.getPersistentDataContainer().set(DisplayEntityPlugin.partUUIDKey, PersistentDataType.STRING, partUUID.toString());
        }
        if (displayEntity.persistentDataContainer != null){
            try{
                display.getPersistentDataContainer().readFromBytes(displayEntity.persistentDataContainer);
            }
            catch(IOException ignore){}
        }

    }
}
