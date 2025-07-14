package net.donnypz.displayentityutils.utils.command;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.RelativePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

@ApiStatus.Internal
public abstract class RelativePointDisplay {

    private static final float scale  = 0.25f;
    private static final String pointDisplayTag = "deu_point_display_internal";
    private static final HashMap<PacketDisplayEntityPart, RelativePointDisplay> interactionParts = new HashMap<>();

    private UUID playerUUID;
    private PacketDisplayEntityPart interactionPart;
    private PacketDisplayEntityPart displayPart;

    RelativePoint relativePoint;
    Location spawnLocation;

    protected boolean isValid = true;


    RelativePointDisplay(Player player, Location spawnLocation, RelativePoint relativePoint, Material itemType){
        this.playerUUID = player.getUniqueId();
        ItemStack stack = new ItemStack(itemType);
        displayPart = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.Transform.TRANSLATION, new Vector3f(0, scale/2, 0))
                .setAttribute(DisplayAttributes.Transform.SCALE, new Vector3f(scale, scale, scale))
                .setAttribute(DisplayAttributes.GLOW_COLOR_OVERRIDE, Color.BLACK)
                .setAttribute(DisplayAttributes.GLOWING, true)
                .setAttribute(DisplayAttributes.BRIGHTNESS, new Display.Brightness(15,15))
                .setAttribute(DisplayAttributes.ItemDisplay.ITEMSTACK, stack)
                .createPart(SpawnedDisplayEntityPart.PartType.ITEM_DISPLAY, spawnLocation, pointDisplayTag);
        displayPart.showToPlayer(player, GroupSpawnedEvent.SpawnReason.INTERNAL);

        interactionPart = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.Interaction.WIDTH, scale)
                .setAttribute(DisplayAttributes.Interaction.HEIGHT, scale)
                .createPart(SpawnedDisplayEntityPart.PartType.INTERACTION, spawnLocation, pointDisplayTag);
        interactionPart.showToPlayer(player, GroupSpawnedEvent.SpawnReason.INTERNAL);

        interactionParts.put(interactionPart, this);

        this.spawnLocation = spawnLocation;
        this.relativePoint = relativePoint;
    }


    public void select(){
        if (!isValid) return;
        displayPart.setGlowColor(Color.YELLOW);
    }

    public void deselect(){
        if (!isValid) return;
        displayPart.setGlowColor(Color.BLACK);
    }

    public void setLocation(@NotNull SpawnedDisplayEntityGroup group, @NotNull Location location){
        if (!isValid){
            return;
        }
        displayPart.teleport(location);
        interactionPart.teleport(location);
        spawnLocation = location;
        relativePoint.setLocation(group, location);
    }

    public abstract boolean removeFromPointHolder();

    public static @Nullable RelativePointDisplay get(@NotNull PacketDisplayEntityPart part){
        return interactionParts.get(part);
    }

    public static boolean isRelativePointPart(@NotNull PacketDisplayEntityPart part){
        return part.getTags().contains(pointDisplayTag);
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
        interactionParts.remove(interactionPart);
        relativePoint = null;
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null){
            displayPart.hideFromPlayer(player);
            interactionPart.hideFromPlayer(player);
        }
        isValid = false;
    }
}
