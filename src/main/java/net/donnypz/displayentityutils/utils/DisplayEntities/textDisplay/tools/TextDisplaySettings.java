package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools;

import org.bukkit.entity.Display;
import org.joml.Matrix4f;

@SuppressWarnings("ClassEscapesDefinedScope")
public class TextDisplaySettings {
    public float YCenterManualOffset = 0;
    public float XCenterManualOffset = 0;
    public int Width = 16;
    public int Height = 16;
    public Float Size = 1f;
    public int TeleportDuration = 1;
    public int InterpolationDuration = 1;
    public boolean IsVisibleDefault = true;
    public Display.Billboard BillboardType = Display.Billboard.FIXED;
    public Matrix4f Matrix4f = new Matrix4f().identity();
    public boolean IsPersistent = false;
    public boolean DoAutoRespawn = false;
    public boolean DoPersistentAutoRespawn = false;
    public boolean ParseCheckOverride = false;
    public Display.Brightness Brightness = null;
    public int UpdateInterval = 10;
    // Do not use as a darken effect, use post-processing "darken" this is just for fixing the fact that text displays are brighter than the color you set them to
    public boolean DoAdjustBrightness = true;
    public float AdjustBrightnessAmount = 0.8f;
}

