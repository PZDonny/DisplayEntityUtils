package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called before the stored {@link DisplayEntityGroup} on an item can be spawned when a player places the item's block or when
 * {@link PlaceableGroupManager#spawnGroup(ItemStack, Location, Player)} or similar is called.
 */
public class PreItemPlaceGroupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    DisplayEntityGroup group;
    ItemStack itemStack;
    Player player;

    private boolean isCancelled = false;

    public PreItemPlaceGroupEvent(@Nullable DisplayEntityGroup group, @NotNull ItemStack itemStack, @Nullable Player player){
        super(!Bukkit.isPrimaryThread());
        this.group = group;
        this.itemStack = itemStack;
        this.player = player;
    }

    /**
     * Get the {@link DisplayEntityGroup} involved in this event.
     * @return a {@link DisplayEntityGroup} or null if the item's group could not be found
     */
    public @Nullable DisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Get the {@link ItemStack} involved in this event.
     * @return an {@link ItemStack}
     */
    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the player involved in this event
     * @return a player
     */
    public @Nullable Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
