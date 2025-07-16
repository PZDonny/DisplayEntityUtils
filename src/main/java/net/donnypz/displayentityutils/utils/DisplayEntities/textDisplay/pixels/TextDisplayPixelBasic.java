package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplaySettings;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class TextDisplayPixelBasic extends TextDisplayPixel {

    protected Matrix4f oldMatrix4f;

    public TextDisplayPixelBasic(){

    }

    @Override
    public void addToGroup(PixelGroup group) {
        group.add(part);
    }

    @Override
    public boolean spawn(Location location) {

        if (!location.isChunkLoaded()||settings == null){

            return false;
        }
        //despawn();
        if (!render){

            return false;
        }
        int a = alpha;
        int r = red;
        int g = green;
        int b = blue;

        if (settings.DoAdjustBrightness && a != 0) {
            r = Math.round(r * settings.AdjustBrightnessAmount);
            g = Math.round(g * settings.AdjustBrightnessAmount);
            b = Math.round(b * settings.AdjustBrightnessAmount);
        }


        container.setAttribute(DisplayAttributes.TextDisplay.TEXT,Component.text(" "));
        container.setAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR,Color.fromARGB(a, r, g, b));
        part = container.createPart(SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY,location);
        updateSettings(part);
        updateTransformation();
        if (settings.IsVisibleDefault){
            for (Player player:Bukkit.getOnlinePlayers()) {
                part.showToPlayer(player, GroupSpawnedEvent.SpawnReason.CUSTOM);
            }
        }


        render = true;
        hasUpdated = false;
        return true;
    }
    protected void updateSettings(PacketDisplayEntityPart part){
        if (part==null){
            return;
        }
        if (settings==null){
            settings = new TextDisplaySettings();
        }
        part.setTeleportDuration(settings.TeleportDuration);
        if (settings.BillboardType!=null) {
            part.setBillboard(settings.BillboardType);
        }
        if (settings.Brightness!=null) {
            part.setBrightness(settings.Brightness);
        }
    }
    @Override
    public void despawn() {
        if (part!=null){
            part.hideFromPlayers(part.getViewersAsPlayers());
        }

    }

    @Override
    public void update() {

    }



    @Override
    public void load(TextDisplaySettings settings, Matrix2dContainer<? extends TextDisplayPixel> container) {

    }
    protected void updateTransformation() {
        if (part == null || settings == null) return;
        part.setAttribute(DisplayAttributes.Transform.SCALE,new Vector3f(0.5f*pixelWidth,0.25f*pixelHeight,0));
    }

}
