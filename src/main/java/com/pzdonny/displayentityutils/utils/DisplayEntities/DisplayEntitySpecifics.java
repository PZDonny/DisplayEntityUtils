package com.pzdonny.displayentityutils.utils.DisplayEntities;

import com.pzdonny.displayentityutils.DisplayEntityPlugin;
import com.pzdonny.displayentityutils.managers.DisplayGroupManager;
import org.bukkit.entity.Display;

import java.io.Serial;
import java.io.Serializable;

public abstract class DisplayEntitySpecifics implements Serializable {

    @Serial
    private static final long serialVersionUID = 99L;

    private final String partTag;
    SerialTransformation serialTransformation;
    private final Display.Billboard billboard;
    private final float viewRange;
    private final float shadowRadius;
    private final float shadowStrength;
    private final float displayWidth;
    private final float displayHeight;
    private int brightnessBlockLight;
    private int brightnessSkyLight;

    DisplayEntitySpecifics(Display displayEntity){
        this.partTag = DisplayGroupManager.getPartTag(displayEntity);
        this.serialTransformation = new SerialTransformation(displayEntity.getTransformation());
        this.billboard = displayEntity.getBillboard();
        this.viewRange = displayEntity.getViewRange();
        this.shadowRadius = displayEntity.getShadowRadius();
        this.shadowStrength = displayEntity.getShadowStrength();
        this.displayWidth = displayEntity.getDisplayWidth();
        this.displayHeight = displayEntity.getDisplayHeight();
        Display.Brightness brightness = displayEntity.getBrightness();
        if (brightness != null) {
            this.brightnessBlockLight = displayEntity.getBrightness().getBlockLight();
            this.brightnessSkyLight = displayEntity.getBrightness().getSkyLight();
        }
    }

    String getPartTag() {
        return partTag;
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

    void updateDisplay(Display display){
        display.setTransformation(serialTransformation.toTransformation());
        display.setBillboard(billboard);
        display.setViewRange(viewRange);
        display.setShadowRadius(shadowRadius);
        display.setShadowStrength(shadowStrength);
        display.setDisplayWidth(displayWidth);
        display.setDisplayHeight(displayHeight);
        if (brightnessBlockLight != 0 && brightnessSkyLight != 0){
            display.setBrightness(new Display.Brightness(brightnessBlockLight, brightnessSkyLight));
        }
        if (partTag != null){
            display.addScoreboardTag(DisplayEntityPlugin.partTagPrefix+partTag);
        }

    }
}
