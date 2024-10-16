package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;

import java.io.*;

@ApiStatus.Internal
public abstract class AnimationParticle implements Externalizable {

    @Serial
    private static final long serialVersionUID = 99L;

    Particle particle;
    int count;
    double extra;

    double xOffset;
    double yOffset;
    double zOffset;
    transient Vector vectorFromOrigin;
    Vector3f vector;

    float groupYawAtCreation;
    float groupPitchAtCreation;

    int delayInTicks = 0;

    boolean isStartFrame;

    @ApiStatus.Internal
    public AnimationParticle(){}


    AnimationParticle(AnimationParticleBuilder builder, Particle particle){
        this.particle = particle;
        this.extra = builder.extra();
        this.count = builder.count();
        this.xOffset = builder.offsetX();
        this.yOffset = builder.offsetY();
        this.zOffset = builder.offsetZ();
        this.isStartFrame = builder.isStartAdd;
    }

    void setVectorFromOrigin(Vector vector, float initialYaw, float initialPitch){
        this.vectorFromOrigin = vector;
        this.groupYawAtCreation = initialYaw;
        this.groupPitchAtCreation = initialPitch;
        this.vector = vector.toVector3f();
    }

    void setDelayInTicks(int delayInTicks){
        this.delayInTicks = delayInTicks;
    }

    public void spawn(SpawnedDisplayEntityGroup group){
        Location spawnLoc = getSpawnLocation(group);
        if (delayInTicks == 0){
            spawn(spawnLoc);
        }
        else{
            long timeStamp = group.getLastAnimationTimeStamp();
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                if (group.isSpawned() && group.getLastAnimationTimeStamp() == timeStamp){
                    spawn(getSpawnLocation(group));
                }
            }, delayInTicks);
        }
    }

    public Location getSpawnLocation(SpawnedDisplayEntityGroup group){
        Vector v = vectorFromOrigin.clone();
        Location groupLoc = group.getLocation();

        double pitchDiff = groupLoc.getPitch() - groupPitchAtCreation;
        double pitchAsRad = Math.toRadians(pitchDiff);
        double sin = Math.sin(pitchAsRad);
        double cos = Math.cos(pitchAsRad);

        v.setY(-1*(v.length() * sin - v.getY() * cos)); //Adjust for pitch
        v.rotateAroundY(Math.toRadians(groupYawAtCreation - groupLoc.getYaw())); //Pivot

        groupLoc.add(v);
        return groupLoc;
    }

    public abstract void spawn(Location location);

    public void repair(){
        vectorFromOrigin = Vector.fromJOML(vector);
        initalize();
    }

    protected abstract void initalize();

    public void sendInfo(Player player){
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Particle General Info:", NamedTextColor.AQUA));
        player.sendMessage(Component.text("Click a value to edit it", NamedTextColor.YELLOW));
        sendEditMSG(player, "| Particle: "+particle.name(), AnimationParticleBuilder.Step.PARTICLE);
        sendEditMSG(player, "| Count: "+count,  AnimationParticleBuilder.Step.COUNT);
        sendEditMSG(player, "| Extra: "+extra,  AnimationParticleBuilder.Step.EXTRA);
        sendEditMSG(player, "| Delay: "+delayInTicks, AnimationParticleBuilder.Step.DELAY);

        String xyzOffset = xOffset+", "+yOffset+", "+zOffset;
        sendEditMSG(player, "| Offsets: "+xyzOffset, AnimationParticleBuilder.Step.OFFSETS);

        Component unique = getUniqueInfo();
        if (unique != null){
            player.sendMessage(unique);
        }

        player.sendMessage(Component.text("Sneak+Right Click to DELETE this particle", NamedTextColor.RED, TextDecoration.BOLD));
        player.sendMessage(Component.text("Use \"/mdis anim cancelparticles\" to hide revealed particles", NamedTextColor.GRAY, TextDecoration.ITALIC));
    }

    private void sendEditMSG(Player player, String info, AnimationParticleBuilder.Step step){
        player.sendMessage(getEditMSG(info, step));
    }

    Component getEditMSG(String info, AnimationParticleBuilder.Step step){
       return Component.text(info, NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("Click to edit this value", NamedTextColor.YELLOW, TextDecoration.ITALIC)))
                .clickEvent(ClickEvent.callback(p -> {
                    new AnimationParticleBuilder((Player) p, isStartFrame, this, step);
                }, ClickCallback.Options.builder().uses(-1).build()));
    }

    protected abstract Component getUniqueInfo();

    boolean editParticle(AnimationParticleBuilder builder){
        AnimationParticleBuilder.Step step = builder.step;
        switch (step){
            case PARTICLE -> {
                if (this.particle.getDataType() == builder.particle().getDataType()){
                    builder.player.sendMessage(Component.text("Particle Type could not be changed! Previous particle has unique data."));
                    builder.player.sendMessage(Component.text("Particles that have particle specific data, such as block, item, or color data, cannot be replaced with another particle.", NamedTextColor.GRAY));
                    return false;
                }
                else{
                    this.particle = builder.particle();
                }
            }
            case COUNT -> {
                this.count = builder.count();
            }
            case EXTRA -> {
                this.extra = builder.extra();
            }
            case DELAY -> {
                this.delayInTicks = builder.delayInTicks;
            }
            default -> {
                return editUniqueParticle(builder, step);
            }
        }
        return true;

    }

    protected abstract boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step);

    @ApiStatus.Internal
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(particle);
        out.writeInt(count);
        out.writeDouble(extra);

        out.writeDouble(xOffset);
        out.writeDouble(yOffset);
        out.writeDouble(zOffset);

        out.writeObject(vector);

        out.writeInt(delayInTicks);

        out.writeBoolean(isStartFrame);

        out.writeFloat(groupYawAtCreation);
        out.writeFloat(groupPitchAtCreation);
    }

    @ApiStatus.Internal
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.particle = (Particle) in.readObject();
        this.count = in.readInt();
        this.extra = in.readDouble();

        this.xOffset = in.readDouble();
        this.yOffset = in.readDouble();
        this.zOffset = in.readDouble();

        this.vector = (Vector3f) in.readObject();
        this.vectorFromOrigin = Vector.fromJOML(vector);

        this.delayInTicks = in.readInt();

        this.isStartFrame = in.readBoolean();

        this.groupYawAtCreation = in.readFloat();
        this.groupPitchAtCreation = in.readFloat();




    }
}
