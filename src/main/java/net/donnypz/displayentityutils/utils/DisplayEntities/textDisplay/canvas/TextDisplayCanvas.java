package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.pixels.PixelGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.Matrix.Matrix2dContainer;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools.TextDisplaySettings;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.helper.TextDisplayElementPixel;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasClickableElement;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.abstractThings.TextDisplayCanvasElement;
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
import java.util.UUID;
import java.util.function.Consumer;

public class TextDisplayCanvas {
    private   PixelGroup canvasGroup = new PixelGroup();
    private final Object sync = new Object();
    private TextDisplayCanvasRenderTypes renderType = TextDisplayCanvasRenderTypes.DYNAMIC;
    private Matrix2dContainer<TextDisplayCanvasPixel> pixels = new Matrix2dContainer<>(TextDisplayCanvasPixel.class);
    private int stackSize = 500;
    private boolean forceUpdates = false;
    private boolean wipe = true;
    private List<TextDisplayCanvasElement> elements = new ArrayList<>();
    private Location location = null;
    private TextDisplaySettings settings = new TextDisplaySettings();
    private boolean doUpdates = false;
    private Integer updateTask;
    private int updateInterval = 1;
    private Entity mount;

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
                        pixels.forEach(new Consumer<TextDisplayCanvasPixel>() {
                            @Override
                            public void accept(TextDisplayCanvasPixel pixel) {
                                pixel.setRender(false);
                            }
                        });
                        pixels.clean();
                    }
                    if (location != null) {
                        for (TextDisplayCanvasElement element : elements) {
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
                pixels.forEach(new Consumer<TextDisplayCanvasPixel>() {
                    @Override
                    public void accept(TextDisplayCanvasPixel pixel){
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
        for (TextDisplayCanvasElement element : elements){
            if (element.isNeedsUpdates()) {
                doUpdates = true;
                break;
            }
        }
    }
    private void mergeElements(){
        List<TextDisplayCanvasElement> remove = new ArrayList<>();
        elements.sort(Comparator.comparingInt(TextDisplayCanvasElement::getLayer));
        elements.forEach(new Consumer<TextDisplayCanvasElement>() {
            @Override
            public void accept(TextDisplayCanvasElement element) {
                if (element.isDead()){
                    remove.add(element);
                    return;
                }
                int startX = element.getX();
                int startY = element.getY();
                boolean useThisSettings = element.getSettings() == null;
                boolean clickable = element instanceof TextDisplayCanvasClickableElement;
                element.getPixels().forEach(new Consumer<TextDisplayElementPixel>() {
                    @Override
                    public void accept(TextDisplayElementPixel pixel) {
                        if (pixel!=null&&pixel.isRender()) {





                            int x = pixel.getX();
                            int y = pixel.getY();
                            int trueX = x + startX;
                            int trueY = y + startY;

                            boolean needsSpawning = false;
                            TextDisplayCanvasPixel canvasPixel = pixels.get(trueX, trueY);
                            if (canvasPixel == null) {
                                canvasPixel = new TextDisplayCanvasPixel(trueX, trueY, pixels);
                                needsSpawning = true;
                            }
                             if (!canvasPixel.getClickableElements().isEmpty()){
                                 for (TextDisplayCanvasClickableElement clickableElement: canvasPixel.getClickableElements()){
                                    if (!clickableElement.isBleed()){
                                        canvasPixel.getClickableElements().remove(clickableElement);
                                        clickableElement.getClickSpace().remove(canvasPixel);
                                    }
                                 }
                             }
                            if (!useThisSettings) {
                                //needs per pixel settings
                            } else if(canvasPixel.getSettings()==null){
                                canvasPixel.setSettings(settings);
                            }
                            canvasPixel.setTwoFaced(element.isTwoFaced());
                            canvasPixel.setColor(pixel.getAlpha(), pixel.getRed(), pixel.getGreen(), pixel.getBlue());
                            canvasPixel.setPixelHeight(pixel.getPixelHeight());
                            canvasPixel.setPixelWidth(pixel.getPixelWidth());
                            canvasPixel.setRender(pixel.isRender());
                            if (needsSpawning&&location!= null){
                                canvasPixel.spawn(location);
                            }
                            if (clickable){
                                ((TextDisplayCanvasClickableElement) element).addClickSpacePixel(canvasPixel);
                                canvasPixel.getClickableElements().add(((TextDisplayCanvasClickableElement) element));
                            }
                        }
                    }
                });

            }
        });
        for (TextDisplayCanvasElement element:remove){
            elements.remove(element);
        }

    }
    public void purge(){
        pixels.forEach(new Consumer<TextDisplayCanvasPixel>() {
            @Override
            public void accept(TextDisplayCanvasPixel pixel) {
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
        pixels.forEach(new Consumer<TextDisplayCanvasPixel>() {
            @Override
            public void accept(TextDisplayCanvasPixel pixel) {
                pixel.despawn();
            }
        });
    }
    public void remove(){
        despawn();
        for (TextDisplayCanvasElement element : elements){
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

    public void addViewer(Player player) {
        canvasGroup.addViewer(player);
        updatePixels();
    }

    public void setLocation(Location location) {
        Location newLocation = location.clone();

        newLocation.setDirection(new Vector(0,0,1));
        canvasGroup.teleport(newLocation);
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
                    pixels.forEach(new Consumer<TextDisplayCanvasPixel>() {
                        @Override
                        public void accept(TextDisplayCanvasPixel pixel) {
                            pixel.setCanvasGroup(canvasGroup);
                            pixel.update();
                            pixel.ride(mount);

                        }
                    });
                }
            }
        });
    }
    public void stack(){
        
    }
    public TextDisplaySettings getSettings() {
        return settings;
    }

    public List<TextDisplayCanvasElement> getElements() {
        return elements;
    }

    public void setElements(List<TextDisplayCanvasElement> elements) {
        this.elements = elements;
    }
    public void addElement(TextDisplayCanvasElement element){
        elements.add(element);
    }
    public void removeElement(TextDisplayCanvasElement element){
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

    public List<Player> geViewerPlayers() {
        return canvasGroup.getViewerPlayers();
    }

    public void addViewer(UUID uuid){
        canvasGroup.addViewer(uuid);
        updatePixels();
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

    public int getStackSize() {
        return stackSize;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    public PixelGroup getCanvasGroup(){
        return canvasGroup;
    }
}
