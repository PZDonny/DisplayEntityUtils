package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.particles;


import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;

import java.util.List;

public class SinglePixelParticle extends TextDisplayParticle {
    private TextDisplayParticlePixel pixel;
    private Location location;
    private final Matrix2dContainer<TextDisplayParticlePixel> dummy = new Matrix2dContainer<>(TextDisplayParticlePixel.class);
    public SinglePixelParticle(){
        setup();
    }
    public SinglePixelParticle(Color color){
        setup();
        setColor(color);
    }public SinglePixelParticle(List<Integer> color){
        setup();
        setColor(color);
    }
    public SinglePixelParticle(int alpha, int red, int green, int blue){
        setup();
        setColor(List.of(alpha, red, green, blue));
    }
    public SinglePixelParticle(int red, int green, int blue){
        setup();
        setColor(List.of(red, green, blue));
    }
    public SinglePixelParticle(List<Integer> color,Location location){
        setup();
        setColor(color);
        spawn(location);
    }
    @Override
    protected void setup(){
        settings.BillboardType = Display.Billboard.CENTER;
        pixel = new TextDisplayParticlePixel();
    }
    public SinglePixelParticle(int alpha, int red, int green, int blue,Location location){
        setColor(List.of(alpha, red, green, blue));
        spawn(location);
    }
    public SinglePixelParticle(int red, int green, int blue,Location location){
        setColor(List.of(red, green, blue));
        spawn(location);
    }

    public SinglePixelParticle(Color color, Location location){
        setColor(color);
        spawn(location);
    }

    public void setColor(Color color) {
        if (pixel==null){
            return;
        }
        pixel.setColor(color);
        update();
    }

    @Override
    public void spawn(Location location) {
        this.location = location;
        if (pixel==null){
            return;
        }
        pixel.setSettings(settings);
        pixel.spawn(location);
    }

    @Override
    protected void update() {
        pixel.update();
    }

    @Override
    public Location getLocation(){
        return location.clone();
    }
    @Override
    public void setLocation(Location location){
        this.location = location;
        pixel.setLocation(location);
    }
    @Override
    public void despawn(){
        if (pixel!=null) {
            pixel.despawn();
        }
    }
    @Override
    public void remove(){
        despawn();
        pixel = null;
    }


    public TextDisplayParticlePixel getPixel() {
        return pixel;
    }
    public void setAlpha(int alpha){
        if (pixel==null){
            return;
        }
        pixel.setAlpha(alpha);
        update();
    }
    public int getAlpha(){
        if (pixel==null){
            return 0;
        }
       return pixel.getAlpha();
    }

    @Override
    public void setColor(List<Integer> color) {
        if (pixel==null){
            return;
        }
        pixel.setColor(color);
        update();
    }

    @Override
    public List<Integer> getColor() {
        if (pixel==null){
            return null;
        }
        return List.of(pixel.getAlpha(),pixel.getRed(),pixel.getGreen(),pixel.getBlue());
    }
}
