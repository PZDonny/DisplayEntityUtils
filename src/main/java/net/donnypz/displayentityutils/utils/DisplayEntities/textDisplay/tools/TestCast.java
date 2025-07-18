package net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.tools;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.particles.SinglePixelParticle;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.TextDisplayStaticButtonCanvasElement;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.elements.TextDisplayStaticCanvasElement;
import net.donnypz.displayentityutils.utils.DisplayEntities.textDisplay.canvas.TextDisplayCanvas;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestCast {
    private final Object sync = new Object();
    private TextDisplayStaticButtonCanvasElement button;
    private Integer task;
    private Location location;
    private Vector3f forward;
    private Location beamEnd;
    private Vector beam;
    private final List<SinglePixelParticle> remove = new ArrayList<>();
    private final float speed = 0.6f;
    private final float beamSize = 1;
    private final float spawnRad = 8;
    private final float beamDist = 50;
    private final float particleCount = 400;
    private final int particlePerTick = 4;
    private final int alpha = 255;
    private final Random random = new Random();
    private final TextDisplayCanvas screen = new TextDisplayCanvas();
    private final List<SinglePixelParticle> particles = new ArrayList<>();
    public void cast(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                synchronized (sync){
                    Location loc = player.getEyeLocation();
                    forward = loc.getDirection().toVector3f();
                    loc.add(loc.getDirection().normalize().multiply(4));
                    BufferedImage castButton = TextDisplayTools.getTexture("https://i.imgur.com/xe6UsZL.png");
                    BufferedImage castIcon = TextDisplayTools.getTexture("https://i.imgur.com/Ioq9Rzu.png");
                    if (castIcon==null){
                        player.sendMessage("castIcon==null");
                        return;
                    }
                    if (castButton==null){
                        player.sendMessage("castButton==null");
                        return;
                    }
                    //castIcon = TextDisplayTools.scaleBufferedImage(castIcon,32,32);
                    //castButton = TextDisplayTools.scaleBufferedImage(castButton,16,16);
                    button = new TextDisplayStaticButtonCanvasElement(screen, -64, 64, new TextDisplayStaticButtonCanvasElement.ClickHandler<TextDisplayStaticButtonCanvasElement>() {
                        @Override
                        public void handle(TextDisplayStaticButtonCanvasElement self) {
                            start();
                        }
                    });
                    button.setImage(TextDisplayTools.bufferedImageToPixelMatrix(castButton));
                    button.setLayer(11);
                    TextDisplayStaticCanvasElement icon = new TextDisplayStaticCanvasElement();
                    icon.setTwoFaced(true);
                    button.setTwoFaced(true);
                    icon.setImage(TextDisplayTools.bufferedImageToPixelMatrix(castIcon));
                    icon.setX(-64);
                    icon.setY(64);
                    screen.getSettings().TeleportDuration=1;

                    screen.getSettings().Size=0.7f;
                    screen.addElement(icon);
                    screen.addElement(button);
                    screen.spawnFlipped(loc);
                    location = loc;
                    beamEnd = location.clone();
                    beam = player.getLocation().getDirection().normalize().multiply(beamDist);
                    beamEnd.add(beam);
                }
            }
        });

    }
    public void despawn(){
        Bukkit.getScheduler().cancelTask(task);
        Bukkit.getScheduler().scheduleSyncDelayedTask(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                screen.despawn();
                for (SinglePixelParticle particle:particles){
                    particle.remove();
                }
                particles.clear();
            }
        },1);


    }
    public void start(){
        screen.removeElement(button);
        screen.update();
        Bukkit.getScheduler().scheduleAsyncDelayedTask(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                synchronized (sync) {
                    despawn();
                }
            }
        }, 800);
        task = Bukkit.getScheduler().scheduleAsyncRepeatingTask(DisplayEntityPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 0, 1);

    }

    private void update() {
                synchronized (sync){
                int ticks = (int) (beamDist / speed);
                int alphaDecay = (alpha / ticks);
                TextDisplaySettings newSettings = screen.getSettings();
                newSettings.Matrix4f.rotateZ(0.1f);
                screen.setSettings(newSettings);
                screen.update();
                for (SinglePixelParticle particle : particles) {
                    if (particle.getPixel()==null){
                        break;
                    }
                    Location particleLoc = particle.getLocation();

                    if (particle.getTags().contains("BEAM")) {
                        Vector move = beam.clone().normalize().multiply(speed);
                        particle.setLocation(particleLoc.add(move));
                        particle.setAlpha(particle.getAlpha() - alphaDecay);

                        if (particleLoc.distanceSquared(beamEnd) <= Math.pow(speed * beamSize, 2)) {
                            remove.add(particle);
                        }
                    } else {
                        Vector move = location.clone().toVector().subtract(particleLoc.toVector()).normalize().multiply(speed);
                        particle.setLocation(particleLoc.add(move));

                        if (particleLoc.distanceSquared(location) <= Math.pow(speed * beamSize, 2)) {
                            particle.addTag("BEAM");
                        }
                    }
                }

                for (int i =0;i<particlePerTick;i++) {
                    if (particles.size() < particleCount) {
                        Location spawnLoc = location.clone();
                        Vector3f forward1 = new Vector3f(forward).normalize().mul(10);
                        spawnLoc.subtract(forward1.x, forward1.y, forward1.z);

                        spawnLoc.add(random.nextFloat(-spawnRad, spawnRad),
                                random.nextFloat(-spawnRad, spawnRad),
                                random.nextFloat(-spawnRad, spawnRad));

                        SinglePixelParticle particle = new SinglePixelParticle(List.of(alpha, 0, 0, 255), spawnLoc);
                        particles.add(particle);
                    }
                }

                for (SinglePixelParticle particle : remove) {
                    particles.remove(particle);
                    particle.despawn();
                }
                remove.clear();
            }
            }

}
