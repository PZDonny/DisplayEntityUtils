package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels;

import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplaySettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplayTools;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public abstract class TextDisplayPixel {
    // Color components
    protected int alpha = 255;
    protected int red = 0;
    protected int green = 0;
    protected int blue = 0;
    //

    protected final transient PacketDisplayEntityPart part;

    public  PacketDisplayEntityPart getPart() {
        return part;
    }

    protected Matrix4f transformation = new Matrix4f().identity();




    // Pixel size - width & height in pixel units WIP
    protected int pixelWidth = 1;
    protected int pixelHeight = 1;

    protected boolean render = true;

    protected boolean autoUpdateRender = true;

    protected boolean hasUpdated = true;
    
    protected transient TextDisplaySettings settings;

    protected TextDisplayPixel() {
        part = new PacketAttributeContainer().createPart(SpawnedDisplayEntityPart.PartType.TEXT_DISPLAY)
    }


    protected TextDisplayPixel(Color color) {
        this();
        setColor(color);
    }

    protected TextDisplayPixel(List<Integer> color) {
        this();
        setColor(color);
    }

    // Color setters & clamp
    public void setColor(Color color) {
        this.alpha = color.getAlpha();
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        clampColor();
    }

    public void setColor(List<Integer> color) {
        if (color.size() == 4) {
            alpha = color.get(0);
            red = color.get(1);
            green = color.get(2);
            blue = color.get(3);
        } else if (color.size() == 3) {
            alpha = 255;
            red = color.get(0);
            green = color.get(1);
            blue = color.get(2);
        }
        clampColor();
    }

    public void setColor(int alpha, int red, int green, int blue) {
        setColor(List.of(alpha, red, green, blue));
    }

    public void setColor(int red, int green, int blue) {
        setColor(List.of(red, green, blue));
    }

    private void clampColor() {
        alpha = Math.max(0, Math.min(255, alpha));
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));
        if (autoUpdateRender) {
            render = alpha != 0;
        }
        hasUpdated = true;
        container.setAttribute(DisplayAttributes.TextDisplay.BACKGROUND_COLOR,Color.fromARGB(alpha,red,green,blue));
    }
    public void ride(Entity mount) { //WIP
        if (mount==null){
            return;
        }
        //mount.addPassenger(textDisplay);
    }

    // Getters & setters for color
    public int getAlpha() { return alpha; }
    public void setAlpha(int alpha) { this.alpha = alpha;
        clampColor(); hasUpdated = true; }

    public int getRed() { return red; }
    public void setRed(int red) { this.red = red; clampColor(); hasUpdated = true; }

    public int getGreen() { return green; }
    public void setGreen(int green) { this.green = green; clampColor(); hasUpdated = true; }

    public int getBlue() { return blue; }
    public void setBlue(int blue) { this.blue = blue; clampColor(); hasUpdated = true; }


    public int getPixelWidth() { return pixelWidth; }
    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
        hasUpdated = true;
    }

    public int getPixelHeight() { return pixelHeight; }
    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight =  pixelHeight;
        hasUpdated = true;
    }

    public boolean isRender() { return render; }
    public void setRender(boolean render) { this.render = render; }

    public void setAutoUpdateRender(boolean autoUpdateRender) {
        this.autoUpdateRender = autoUpdateRender;
    }

    public TextDisplaySettings getSettings() { return settings; }
    public void setSettings(TextDisplaySettings settings) { this.settings = settings; }

    public boolean hasUpdated() { return hasUpdated; }
    public void setHasUpdated(boolean hasUpdated) { this.hasUpdated = hasUpdated; }

    public void clear() {
        setColor(0, 0, 0, 0);
        hasUpdated = true;
    }

    public abstract void addToGroup(PixelGroup group);
    public abstract boolean spawn(Location location);
    public abstract void update();
    public abstract void load(TextDisplaySettings settings, Matrix2dContainer<? extends TextDisplayPixel> container  );
    public abstract void despawn();
    public  void move(Vector vector){
        move(vector.toVector3f());
    }
    public  void move(float x, float y,float z){
        move(new Vector3f(x,y,z));
    }
    public  void move(List<Float> cords){
        if (cords.size()!=3){
            return;
        }
        move(new Vector3f(cords.getFirst(),cords.get(1),cords.getLast()));
    }
    public abstract void move(Vector3f vector);

    public Matrix4f getTextDisplayMatrix4f(){
        if (part == null||part.getDisplayTransformation()==null)
            return null;
        return TextDisplayTools.transformationToMatrix(part.getDisplayTransformation());
    }
    public Location getLocation(){
        if (part==null){
            return null;
        }
        return part.getLocation();
    }
    public void setLocation(Location location){
        if (this.part!=null&&location!=null){
            part.teleport(location);
        }
    }


}
