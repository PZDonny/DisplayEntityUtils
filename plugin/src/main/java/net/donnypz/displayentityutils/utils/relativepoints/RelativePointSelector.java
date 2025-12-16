package net.donnypz.displayentityutils.utils.relativepoints;

import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import net.donnypz.displayentityutils.utils.packet.PacketAttributeContainer;
import net.donnypz.displayentityutils.utils.packet.attributes.DisplayAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.UUID;

@ApiStatus.Internal
public abstract class RelativePointSelector<T extends RelativePoint> {

    private static final float scale  = 0.25f;
    private static final String pointDisplayTag = "deu_point_display_internal";
    private static final HashMap<PacketDisplayEntityPart, RelativePointSelector> interactionParts = new HashMap<>();

    private UUID playerUUID;
    protected PacketDisplayEntityPart selectPart;
    protected PacketDisplayEntityPart displayPart;

    T relativePoint;
    Location spawnLocation;

    protected boolean isValid = true;


    RelativePointSelector(Player player, Location spawnLocation, T relativePoint, Material itemType){
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

        selectPart = new PacketAttributeContainer()
                .setAttribute(DisplayAttributes.Interaction.WIDTH, scale)
                .setAttribute(DisplayAttributes.Interaction.HEIGHT, scale)
                .createPart(SpawnedDisplayEntityPart.PartType.INTERACTION, spawnLocation, pointDisplayTag);
        selectPart.showToPlayer(player, GroupSpawnedEvent.SpawnReason.INTERNAL);

        interactionParts.put(selectPart, this);

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

    public void setLocation(@NotNull ActiveGroup<?> group, @NotNull Location location){
        if (!isValid){
            return;
        }
        displayPart.teleport(location);
        selectPart.teleport(location);
        spawnLocation = location;
        relativePoint.setLocation(group, location);
    }

    public abstract boolean removeFromPointHolder();

    public static @Nullable RelativePointSelector get(@NotNull PacketDisplayEntityPart part){
        return interactionParts.get(part);
    }

    public static boolean isRelativePointPart(@NotNull PacketDisplayEntityPart part){
        return part.getTags().contains(pointDisplayTag);
    }

    public T getRelativePoint(){
        return relativePoint;
    }

    public abstract void sendInfo(Player player);

    public abstract void rightClick(Player player);

    public void despawn(){
        if (!isValid){
            return;
        }
        spawnLocation = null;
        interactionParts.remove(selectPart);
        relativePoint = null;
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null){
            displayPart.remove();
            selectPart.remove();
        }
        isValid = false;
    }
}
