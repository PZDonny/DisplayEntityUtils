package net.donnypz.displayentityutils.utils.DisplayEntities.particles;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.*;

@ApiStatus.Internal
public abstract class AnimationParticle implements Externalizable, Cloneable {

    @Serial
    private static final long serialVersionUID = 99L;

    transient Particle particle;
    String particleName;
    int count;
    double extra;

    double xOffset;
    double yOffset;
    double zOffset;
    transient Vector vectorFromOrigin;
    Vector3f vector = null;

    float groupYawAtCreation;
    float groupPitchAtCreation;

    int delayInTicks = 0;

    @ApiStatus.Internal
    public AnimationParticle(){}


    AnimationParticle(AnimationParticleBuilder builder, Particle particle){
        this.particle = particle;
        this.particleName = particle.getKey().getKey();
        this.extra = builder.extra();
        this.count = builder.count();
        this.xOffset = builder.offsetX();
        this.yOffset = builder.offsetY();
        this.zOffset = builder.offsetZ();
    }

    public void setDelayInTicks(int delayInTicks){
        this.delayInTicks = delayInTicks;
    }

    public void spawn(@NotNull Location location, @NotNull SpawnedDisplayEntityGroup group, @Nullable DisplayAnimator animator){
        if (delayInTicks == 0){
            spawn(location);
        }
        else{
            Bukkit.getScheduler().runTaskLater(DisplayEntityPlugin.getInstance(), () -> {
                if (!group.isSpawned()){
                    return;
                }
                if (animator == null){
                    spawn(location);
                }
                else if (group.isActiveAnimator(animator)){
                    spawn(location);
                }
            }, delayInTicks);
        }
    }

    /*public Location getSpawnLocation(SpawnedDisplayEntityGroup group){
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
    }*/

    public abstract void spawn(Location location);

    @ApiStatus.Internal
    public void initializeParticle(){
        //Post 2.7.0
        if (particle == null){
            particle = Registry.PARTICLE_TYPE.get(new NamespacedKey("minecraft", particleName));
        }
        //Pre 2.7.0
        else{
            particleName = particle.getKey().getKey();
        }

        //Pre 2.7.0
        if (vector != null){
            vectorFromOrigin = Vector.fromJOML(vector);
        }

        initalize();
    }

    protected abstract void initalize();

    public void sendInfo(Player player){
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Particle Info:", NamedTextColor.AQUA));
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
    }

    private void sendEditMSG(Player player, String info, AnimationParticleBuilder.Step step){
        player.sendMessage(getEditMSG(info, step));
    }

    Component getEditMSG(String info, AnimationParticleBuilder.Step step){
       return Component.text(info, NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("Click to edit this value", NamedTextColor.YELLOW, TextDecoration.ITALIC)))
                .clickEvent(ClickEvent.callback(p -> {
                    new AnimationParticleBuilder((Player) p, this, step);
                }, ClickCallback.Options.builder().uses(-1).build()));
    }

    protected abstract Component getUniqueInfo();


    boolean editParticle(AnimationParticleBuilder builder){
        AnimationParticleBuilder.Step step = builder.step;
        switch (step){
            case PARTICLE -> {
                if (this.particle.getDataType() == builder.particle().getDataType()){
                    builder.player.sendMessage(Component.text("Failed to change particle type!", NamedTextColor.RED));
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
            case OFFSETS -> {
                this.xOffset = builder.offsetX();
                this.yOffset = builder.offsetY();
                this.zOffset = builder.offsetZ();
            }
            default -> {
                return editUniqueParticle(builder, step);
            }
        }
        return true;

    }

    protected abstract boolean editUniqueParticle(AnimationParticleBuilder builder, AnimationParticleBuilder.Step step);

    @ApiStatus.Internal
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(particle.name());
        out.writeInt(count);
        out.writeDouble(extra);

        out.writeDouble(xOffset);
        out.writeDouble(yOffset);
        out.writeDouble(zOffset);

        out.writeObject(vector);

        out.writeInt(delayInTicks);

        out.writeBoolean(false); //out.writeBoolean(isStartFrame);

        out.writeFloat(groupYawAtCreation);
        out.writeFloat(groupPitchAtCreation);
    }

    @ApiStatus.Internal
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Object particleObj = in.readObject();
        if (particleObj instanceof String string) {
            try{
                this.particle = Particle.valueOf(string);
            }
            catch (IllegalArgumentException e){
                this.particle = Particle.FLAME;
                Bukkit.getLogger().warning("Failed to migrate old particle name. Defaulting to FLAME.");
            }
        }
        else if (particleObj instanceof Particle) {
            try {
                this.particle = ((Particle) particleObj); //Legacy Format
            }
            catch (Exception e) { //Fallback
                this.particle = Particle.FLAME;
                Bukkit.getLogger().warning("Failed to migrate old particle name. Defaulting to FLAME.");
            }
        }
        this.count = in.readInt();
        this.extra = in.readDouble();

        this.xOffset = in.readDouble();
        this.yOffset = in.readDouble();
        this.zOffset = in.readDouble();

        this.vector = (Vector3f) in.readObject();

        if (vector != null){
            this.vectorFromOrigin = Vector.fromJOML(vector);
        }

        this.delayInTicks = in.readInt();

        //boolean isStartFrame =
                in.readBoolean();

        this.groupYawAtCreation = in.readFloat();
        this.groupPitchAtCreation = in.readFloat();
    }

    @ApiStatus.Internal
    public float getGroupYawAtCreation() {
        return groupYawAtCreation;
    }

    @ApiStatus.Internal
    public float getGroupPitchAtCreation() {
        return groupPitchAtCreation;
    }

    @ApiStatus.Internal
    public Vector3f getVector() {
        return vector;
    }

    public Particle getParticle() {
        return particle;
    }

    public String getParticleName() {
        return particleName;
    }

    @Override
    public AnimationParticle clone() {
        try {
            return (AnimationParticle) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
