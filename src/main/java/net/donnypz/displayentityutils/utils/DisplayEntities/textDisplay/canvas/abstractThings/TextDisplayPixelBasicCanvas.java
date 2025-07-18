package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels.PixelGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels.TextDisplayPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels.TextDisplayPixelBasic;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.MatrixCords;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplaySettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplayTools;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;

import java.util.List;

public abstract class TextDisplayPixelBasicCanvas extends TextDisplayPixelBasic {
    protected PixelGroup canvasGroup;
    protected Float realWidth = null ;
    protected Float realHeight = null ;
    protected MatrixCords cords;
    protected transient PacketDisplayEntityPart backFace;
    protected boolean twoFaced = false;

    public TextDisplayPixelBasicCanvas(int x, int y) {
        cords = new MatrixCords(x ,y,this);
    }
    public TextDisplayPixelBasicCanvas(int x, int y, Matrix2dContainer<? extends TextDisplayPixel> container) {
    }

    public TextDisplayPixelBasicCanvas(Color color, int x, int y, Matrix2dContainer<? extends TextDisplayPixel> container) {
        setColor(color);
    }

    public TextDisplayPixelBasicCanvas(List<Integer> color, int x, int y, Matrix2dContainer<? extends TextDisplayPixel> container) {
        this(x, y,container);
        setColor(color);
    }
    public TextDisplayPixelBasicCanvas(Color color) {
        setColor(color);
    }

    public TextDisplayPixelBasicCanvas(List<Integer> color) {
        super();
        setColor(color);
    }

    protected TextDisplayPixelBasicCanvas() {
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
        if (settings.IsVisibleDefault) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                part.showToPlayer(player, GroupSpawnedEvent.SpawnReason.CUSTOM);
            }
        }
        if (isTwoFaced()){
            spawnBackFace(location);
        }
        return true;
    }
    private void spawnBackFace(Location location){
        if (backFace!=null){
            backFace.hideFromPlayers(backFace.getViewersAsPlayers());
            backFace = null;
        }
        if (!twoFaced){
            return;
        }
        if (!location.isChunkLoaded()||settings == null){
            return;}
        if (!render){
            return;
        }

        backFace = container.createPart(SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY,location);
    }




    @Override
    public void update() {
        if (part == null || !hasUpdated) return;
        int a = alpha;
        int r = red;
        int g = green;
        int b = blue;

        if (settings.DoAdjustBrightness && a != 0) {
            r = Math.round(r * settings.AdjustBrightnessAmount);
            g = Math.round(g * settings.AdjustBrightnessAmount);
            b = Math.round(b * settings.AdjustBrightnessAmount);
        }
        part.setAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR,Color.fromARGB(a, r, g, b));
        if (twoFaced&&backFace==null){
            spawnBackFace(part.getLocation());
        }
        if (!twoFaced&&backFace!=null){
            backFace.hideFromPlayers(backFace.getViewersAsPlayers());
            backFace=null;
        }
        updateSettings(part);
        if (backFace!=null) {
            backFace.setAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR,Color.fromARGB(a, r, g, b));
            updateSettings(backFace);
        }
        if(oldMatrix4f!=settings.Matrix4f) {
            updateTransformation();
        }
        hasUpdated = false;
        subUpdate();
    }
    protected abstract void subUpdate();
    @Override
    public void despawn() {
        if (part!=null){
            part.hideFromPlayers(part.getViewersAsPlayers());
        }
        if (backFace!=null){
            backFace.hideFromPlayers(backFace.getViewersAsPlayers());
        }
        canvasGroup.remove(this);
        render = false;
        realWidth = null;
        realHeight = null;
        subRemove();
    }
    protected abstract void subRemove();


    @Override
    public void load(TextDisplaySettings settings, Matrix2dContainer container) {
        this.settings = settings;
        if (this.cords == null) {
            this.cords = new MatrixCords(getX(), getY(), container, this);
        } else {
            this.cords.migrateMatrix(container);
        }
        subLoad();
    }
    protected abstract void subLoad();
    @Override
    protected void updateTransformation() {

        if (part == null || settings == null) return;
        float offsetX = getX();
        //float offsetY = getY() * -1 + settings.Height * 2;
        float offsetY = getY();
        float RAW_MAGIC_NUMBER = 0.0625f;
        float MAGIC_NUMBER = RAW_MAGIC_NUMBER * 2;
        float magicOffsetX = RAW_MAGIC_NUMBER * 2 * settings.Size;
        float magicOffsetY = MAGIC_NUMBER * 2 * settings.Size;



        offsetX = (offsetX + (1 - RAW_MAGIC_NUMBER * RAW_MAGIC_NUMBER) - RAW_MAGIC_NUMBER * 10) + 0.0258f;
        offsetY--;




        realWidth = 0.5f * settings.Size * pixelWidth;
        realHeight = 0.25f * settings.Size * pixelHeight;
        Matrix4f matrix4f = new Matrix4f(settings.Matrix4f);
        oldMatrix4f = new Matrix4f(settings.Matrix4f);
        matrix4f.scale(realWidth,realHeight , 1);
        matrix4f.translate(magicOffsetX * offsetX, magicOffsetY * offsetY, 0);
        Transformation setTransformation = TextDisplayTools.matrixToTransformation(matrix4f);
        part.setAttribute(DisplayAttributes.Transform.SCALE,setTransformation.getScale());
        part.setAttribute(DisplayAttributes.Transform.TRANSLATION,setTransformation.getTranslation());
        part.setAttribute(DisplayAttributes.Transform.RIGHT_ROTATION,setTransformation.getRightRotation());
        part.setAttribute(DisplayAttributes.Transform.LEFT_ROTATION,setTransformation.getLeftRotation());
        for (Player player:part.getViewersAsPlayers()){
            part.resendAttributes(player);
        }
        transformation = matrix4f;;
        if (twoFaced&&backFace!=null){
            Matrix4f backMatrix4f = new Matrix4f(transformation);
            backMatrix4f.rotateY((float) Math.toRadians(180));
            Transformation backTransformation = TextDisplayTools.matrixToTransformation(backMatrix4f);

            backFace.setAttribute(DisplayAttributes.Transform.SCALE,backTransformation.getScale());
            backFace.setAttribute(DisplayAttributes.Transform.TRANSLATION,backTransformation.getTranslation());
            backFace.setAttribute(DisplayAttributes.Transform.RIGHT_ROTATION,backTransformation.getRightRotation());
            backFace.setAttribute(DisplayAttributes.Transform.LEFT_ROTATION,backTransformation.getLeftRotation());
            if ( !backFace.getViewersAsPlayers().containsAll(part.getViewersAsPlayers())){
            backFace.showToPlayers(part.getViewersAsPlayers(), GroupSpawnedEvent.SpawnReason.CUSTOM);
            }else {
                for (Player player:backFace.getViewersAsPlayers()){
                    backFace.resendAttributes(player);
                }
            }


        }
    }


    public boolean isTwoFaced() {
        return twoFaced;
    }

    public void setTwoFaced(boolean twoFaced) {
        this.twoFaced = twoFaced;
    }
    // Position getters/setters
    public int getX() { return cords.getX(); }
    public void setX(int x) { cords.setX(x); hasUpdated = true; }

    public int getY() { return cords.getY(); }
    public void setY(int y) { cords.setY(y); hasUpdated = true; }
    public MatrixCords getCords() {
        return cords;
    }
    public Float getRealWidth() {
        return realWidth;
    }

    public Float getRealHeight() {
        return realHeight;
    }

    public void setCanvasGroup(PixelGroup canvasGroup) {
        this.canvasGroup = canvasGroup;
        canvasGroup.add(this);
    }
}