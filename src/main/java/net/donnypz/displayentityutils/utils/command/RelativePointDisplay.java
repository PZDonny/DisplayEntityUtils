package net.donnypz.displayentityutils.utils.command;

import net.donnypz.displayentityutils.DisplayEntityPlugin;
import net.donnypz.displayentityutils.utils.DisplayEntities.RelativePoint;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
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
public abstract class RelativePointDisplay {

    private static final float scale  = 0.25f;
    private static final String pointDisplayTag = "deu_point_display";
    private static final HashMap<UUID, RelativePointDisplay> interactions = new HashMap<>();

    private UUID interaction;
    private UUID display;

    RelativePoint relativePoint;

    Location spawnLocation;

    protected boolean isValid = true;


    RelativePointDisplay(Location spawnLocation, RelativePoint relativePoint, Material itemType){
        ItemStack stack = new ItemStack(itemType);
        ItemDisplay d = spawnLocation.getWorld().spawn(spawnLocation, ItemDisplay.class, id -> {
            id.setItemStack(stack);
            id.setVisibleByDefault(false);
            id.setTransformation(new Transformation(new Vector3f(0, scale/2, 0), new AxisAngle4f(), new Vector3f(scale, scale, scale), new AxisAngle4f()));
            id.setPersistent(false);
            id.setGlowColorOverride(Color.BLACK);
            id.setBrightness(new Display.Brightness(15, 15));
            id.setGlowing(true);
        });



        Interaction i = spawnLocation.getWorld().spawn(spawnLocation, Interaction.class, e -> {
           e.setVisibleByDefault(false);
           e.setInteractionHeight(scale);
           e.setInteractionWidth(scale);
           e.setPersistent(false);
        });

        display = d.getUniqueId();
        interaction = i.getUniqueId();

        interactions.put(i.getUniqueId(), this);

        DisplayUtils.addTag(d, pointDisplayTag);
        DisplayUtils.addTag(i, pointDisplayTag);

        this.spawnLocation = spawnLocation;
        this.relativePoint = relativePoint;
    }

    void reveal(Player player){
        if (!isValid) return;
        player.showEntity(DisplayEntityPlugin.getInstance(), Bukkit.getEntity(display));
        player.showEntity(DisplayEntityPlugin.getInstance(), Bukkit.getEntity(interaction));
    }

    public void select(){
        if (!isValid) return;
        Display d = (Display) Bukkit.getEntity(display);
        d.setGlowColorOverride(Color.YELLOW);
    }

    public void deselect(){
        if (!isValid) return;
        Display d = (Display) Bukkit.getEntity(display);
        d.setGlowColorOverride(Color.BLACK);
    }


    public abstract boolean removeFromPointHolder();

    public static RelativePointDisplay get(UUID uuid){
        return interactions.get(uuid);
    }

    public static boolean isRelativePointEntity(Entity entity){
        return DisplayUtils.hasPartTag(entity, pointDisplayTag);
    }

    public RelativePoint getRelativePoint() {
        return relativePoint;
    }

    public abstract void leftClick(Player player);

    public abstract void rightClick(Player player);

    public void despawn(){
        if (!isValid){
            return;
        }
        spawnLocation = null;
        interactions.remove(interaction);
        relativePoint = null;
        despawnEntity(display);
        despawnEntity(interaction);
        display = null;
        interaction = null;
        isValid = false;
    }

    private void despawnEntity(UUID uuid){
        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) entity.remove();
    }
}
