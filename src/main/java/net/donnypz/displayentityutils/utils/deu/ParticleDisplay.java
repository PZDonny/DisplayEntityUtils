package net.donnypz.displayentityutils.utils.deu;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.particles.AnimationParticle;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.ApiStatus;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.UUID;

@ApiStatus.Internal
public class ParticleDisplay {

    private Interaction interaction;
    private ItemDisplay display;
    private static final ItemStack stack = new ItemStack(Material.PURPLE_CONCRETE);
    private static final float scale  = 0.25f;
    AnimationParticle particle;
    SpawnedDisplayAnimationFrame frame;
    static final HashMap<UUID, ParticleDisplay> interactions = new HashMap<>();
    boolean isStartParticle;
    private static String particleDisplayTag = "deu_particle_display";

    private boolean isValid = true;

    ParticleDisplay(Location spawnLocation, AnimationParticle particle, SpawnedDisplayAnimationFrame frame, boolean isStartParticle){
        display = spawnLocation.getWorld().spawn(spawnLocation, ItemDisplay.class, id -> {
            id.setItemStack(stack);
            id.setVisibleByDefault(false);
            id.setTransformation(new Transformation(new Vector3f(0, scale/2, 0), new AxisAngle4f(), new Vector3f(scale, scale, scale), new AxisAngle4f()));
            id.setPersistent(false);
            id.setGlowColorOverride(Color.YELLOW);
            id.setGlowing(true);
        });
        DisplayUtils.addTag(display, particleDisplayTag);

        interaction = spawnLocation.getWorld().spawn(spawnLocation, Interaction.class, i -> {
           i.setVisibleByDefault(false);
           i.setInteractionHeight(scale);
           i.setInteractionWidth(scale);
           i.setPersistent(false);
        });
        interactions.put(interaction.getUniqueId(), this);
        DisplayUtils.addTag(interaction, particleDisplayTag);
        this.particle = particle;
        this.frame = frame;
        this.isStartParticle = isStartParticle;
    }

    void reveal(Player player){
        player.showEntity(DisplayEntityPlugin.getInstance(), display);
        player.showEntity(DisplayEntityPlugin.getInstance(), interaction);
    }

    public static void sendInfo(UUID uuid, Player player){
        ParticleDisplay display = interactions.get(uuid);
        if (display == null){
            return;
        }
        display.particle.sendInfo(player);
    }

    public static boolean delete(UUID uuid){
        ParticleDisplay display = interactions.get(uuid);
        if (display == null){
            return false;
        }

        if (display.isStartParticle){
            display.frame.removeFrameStartParticle(display.particle);
        }
        else{
            display.frame.removeFrameEndParticle(display.particle);
        }
        display.remove();
        return true;
    }

    public static ParticleDisplay get(UUID uuid){
        return interactions.get(uuid);
    }

    public static boolean isParticleDisplay(Entity entity){
        return DisplayUtils.hasTag(entity, particleDisplayTag);
    }


    void remove(){
        if (!isValid){
            return;
        }
        display.remove();
        interaction.remove();
        interactions.remove(interaction.getUniqueId());
        frame = null;
        particle = null;
        display = null;
        interaction = null;
        isValid = false;
    }
}
