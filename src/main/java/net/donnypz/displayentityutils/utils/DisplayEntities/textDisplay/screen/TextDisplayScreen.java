package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplaySettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.Elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayScreenClickableElement;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.abstractThings.TextDisplayScreenElement;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.screen.enums.TextDisplayScreenRenderTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class TextDisplayScreen {
    private final Object sync = new Object();
    private TextDisplayScreenRenderTypes renderType = TextDisplayScreenRenderTypes.DYNAMIC;
    private Matrix2dContainer<TextDisplayScreenPixel> pixels = new Matrix2dContainer<>(TextDisplayScreenPixel.class);
    private boolean isStrict = false;
    private int width;
    private int height;
    private boolean forceUpdates = false;
    private boolean wipe = true;
    private List<TextDisplayScreenElement> elements = new ArrayList<>();
    private Location location = null;
    private TextDisplaySettings settings = new TextDisplaySettings();
    private boolean doUpdates = false;
    private Integer updateTask;
    private int updateInterval = 1;
    private Entity mount;
    private List<Player> shownPlayers = new ArrayList<>();

    public void setWipe(boolean wipe) {
        this.wipe = wipe;
    }

    public boolean isWipe() {
        return wipe;
    }
    public void update(){
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                synchronized (sync) {
                    checkIfDoUpdates();
                    if (!doUpdates && !forceUpdates) {
                        stopUpdateTask();
                    }
                    if (wipe) {
                        pixels.forEach(new Consumer<TextDisplayScreenPixel>() {
                            @Override
                            public void accept(TextDisplayScreenPixel pixel) {
                                pixel.setRender(false);
                            }
                        });
                        pixels.clean();
                    }
                    if (location != null) {
                        for (TextDisplayScreenElement element : elements) {
                            element.update();
                        }
                    }
                    mergeElements();
                    purge();
                    if ((doUpdates || forceUpdates) && updateTask == null) {
                        startUpdateTask();
                    }
                    updatePixels();
                }
            }
            });
        }

    public void spawn(Location location){
        Vector dir = location.getDirection();

        location.setDirection(new Vector(0,0,1));
        if (!location.isChunkLoaded()){
            return;
        }
        Vector3f position = new Vector3f(0, 0, 0);
        Vector3f target = dir.toVector3f();
        Vector3f up = new Vector3f(0, 1, 0);
        settings.Matrix4f.lookAt(position, target, up).invert();
        Vector3f localUp = dir.toVector3f();
        Vector3f right = new Vector3f();
        localUp.cross(up, right).normalize();
        Quaternionf pitchUp = new Quaternionf().fromAxisAngleRad(right, (float) Math.toRadians(90));
        Vector3f pitchedDirection = new Vector3f();
        pitchUp.transform(localUp, pitchedDirection);
        settings.Matrix4f.rotateY(180);
        this.location = location;
        spawn();
    }
    public void spawnFlipped(Location location){

        Vector dir = location.getDirection();
        location.setDirection(new Vector(0,0,1));
        if (!location.isChunkLoaded()){

            return;
        }
        Vector3f position = new Vector3f(0, 0, 0);         // Your object's position
        Vector3f target = dir.toVector3f();          // Where it should face
        Vector3f up = new Vector3f(0, 1, 0);
        settings.Matrix4f.lookAt(position, target, up).invert();
        this.location = location;
        spawn();
    }
    private void spawn(){

        checkIfDoUpdates();
        update();
        switch (renderType){
            case DYNAMIC -> {
                pixels.forEach(new Consumer<TextDisplayScreenPixel>() {
                    @Override
                    public void accept(TextDisplayScreenPixel pixel){
                        if (pixel.isUseSuperSettings()){
                        pixel.setSettings(settings);
                        }
                        pixel.spawn(location);


                    }
                });
            }
            case OPTIMIZED -> {
            }
        }




        if (doUpdates){
            startUpdateTask();
        }
    }
    private void checkIfDoUpdates(){
        doUpdates = false;
        for (TextDisplayScreenElement element : elements){
            if (element.isNeedsUpdates()) {
                doUpdates = true;
                break;
            }
        }
    }
    private void mergeElements(){
        List<TextDisplayScreenElement> remove = new ArrayList<>();
        elements.sort(Comparator.comparingInt(TextDisplayScreenElement::getLayer));
        elements.forEach(new Consumer<TextDisplayScreenElement>() {
            @Override
            public void accept(TextDisplayScreenElement element) {
                if (element.isDead()){
                    remove.add(element);
                    return;
                }
                int startX = element.getX();
                int startY = element.getY();
                boolean useThisSettings = element.getSettings() == null;
                boolean clickable = element instanceof TextDisplayScreenClickableElement;
                element.getPixels().forEach(new Consumer<TextDisplayElementPixel>() {
                    @Override
                    public void accept(TextDisplayElementPixel pixel) {
                        if (pixel!=null&&pixel.isRender()) {





                            int x = pixel.getX();
                            int y = pixel.getY();
                            int trueX = x + startX;
                            int trueY = y + startY;

                            boolean needsSpawning = false;
                            TextDisplayScreenPixel screenPixel = pixels.get(trueX, trueY);
                            if (screenPixel == null) {
                                screenPixel = new TextDisplayScreenPixel(trueX, trueY, pixels);
                                needsSpawning = true;
                            }

                            if (!useThisSettings) {
                                //needs per pixel settings
                            } else if(screenPixel.getSettings()==null){
                                screenPixel.setSettings(settings);
                            }
                            screenPixel.setTwoFaced(element.isTwoFaced());
                            screenPixel.setColor(pixel.getAlpha(), pixel.getRed(), pixel.getGreen(), pixel.getBlue());
                            screenPixel.setPixelHeight(pixel.getPixelHeight());
                            screenPixel.setPixelWidth(pixel.getPixelWidth());
                            screenPixel.setRender(pixel.isRender());
                            if (needsSpawning&&location!= null){
                                screenPixel.spawn(location);
                            }
                            if (clickable){
                                ((TextDisplayScreenClickableElement) element).addClickSpacePixel(screenPixel);
                            }
                        }
                    }
                });

            }
        });
        for (TextDisplayScreenElement element:remove){
            elements.remove(element);
        }

    }
    public void purge(){
        pixels.forEach(new Consumer<TextDisplayScreenPixel>() {
            @Override
            public void accept(TextDisplayScreenPixel pixel) {
                if (!pixel.isRender()){
                    pixels.set(pixel.getX(), pixel.getY(), null );
                    pixel.despawn();
                }
            }
        });
        pixels.clean();
    }
    public void despawn(){
        stopUpdateTask();
        pixels.forEach(new Consumer<TextDisplayScreenPixel>() {
            @Override
            public void accept(TextDisplayScreenPixel pixel) {
                pixel.despawn();
            }
        });
    }
    public void remove(){
        despawn();
        for (TextDisplayScreenElement element : elements){
            element.remove();
        }
    }



    public void translate(float x, float y, float z) {
        settings.Matrix4f.translate(x,y,z);
        updatePixels();

    }

    public void translate(Vector3f offset) {
        settings.Matrix4f.translate(offset);
        updatePixels();

    }

    public void setMatrix4f(Matrix4f matrix4f) {
        settings.Matrix4f = matrix4f;
        updatePixels();

    }

    public void setBillboard(Display.Billboard billboard) {
        settings.BillboardType = billboard;
        updatePixels();
    }

    public void ride(Entity entity) {
        mount = entity;
        updatePixels();
    }

    public void setIsVisibleDefault(Boolean isVisibleDefault) {
        settings.IsVisibleDefault = isVisibleDefault;
        updatePixels();
    }

    public void showPlayer(Player player) {
        shownPlayers.add(player);
        updatePixels();
    }

    public void setLocation(Location location) {
        Location newLocation = location.clone();
        newLocation.setDirection(new Vector(0,0,1));
        this.location=newLocation;
        updatePixels();
    }
    public Location getLocation(){
        return location;
    }
    public void setTpSpeed(int speed) {
        settings.TeleportDuration = speed;
        updatePixels();
    }

    private void updatePixels(){
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                synchronized (sync) {
                    pixels.forEach(new Consumer<TextDisplayScreenPixel>() {
                        @Override
                        public void accept(TextDisplayScreenPixel pixel) {

                            pixel.update();
                            pixel.setLocation(location);
                            pixel.ride(mount);

                        }
                    });
                }
            }
        });
    }
    public TextDisplaySettings getSettings() {
        return settings;
    }

    public List<TextDisplayScreenElement> getElements() {
        return elements;
    }

    public void setElements(List<TextDisplayScreenElement> elements) {
        this.elements = elements;
    }
    public void addElement(TextDisplayScreenElement element){
        elements.add(element);
    }
    public void removeElement(TextDisplayScreenElement element){
        elements.remove(element);
        element.remove();
    }
    private void startUpdateTask(){
        updateTask = Bukkit.getScheduler().scheduleAsyncRepeatingTask(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                update();
            }
        },0,updateInterval);
    }
    private void stopUpdateTask(){
        if (updateTask==null){
            return;
        }
        Bukkit.getScheduler().cancelTask(updateTask);
        updateTask = null;
    }

    public List<Player> getShownPlayers() {
        return shownPlayers;
    }

    public void addShownPlayer(Player player) {
        this.shownPlayers.add(player);
    }

    public boolean isDoUpdates() {
        return doUpdates;
    }

    public void setSettings(TextDisplaySettings settings) {
        this.settings = settings;
        updatePixels();
    }

    public boolean isForceUpdates() {
        return forceUpdates;
    }

    public void setForceUpdates(boolean forceUpdates) {
        this.forceUpdates = forceUpdates;
    }
}
