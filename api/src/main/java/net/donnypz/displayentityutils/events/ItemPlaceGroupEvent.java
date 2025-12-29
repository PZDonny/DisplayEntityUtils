package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.managers.PlaceableGroupData;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player places an {@link ActiveGroup} using an item that
 * has an assigned group with {@link PlaceableGroupData}.
 */
public class ItemPlaceGroupEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    ActiveGroup<?> activeGroup;
    ItemStack itemStack;
    Player player;

    public ItemPlaceGroupEvent(@NotNull ActiveGroup<?> group, @NotNull ItemStack itemStack, @Nullable Player player){
        super(!Bukkit.isPrimaryThread());
        this.activeGroup = group;
        this.itemStack = itemStack;
        this.player = player;
    }

    /**
     * Get the {@link ActiveGroup} involved in this event.
     * @return a {@link ActiveGroup}
     */
    public @NotNull ActiveGroup<?> getGroup() {
        return activeGroup;
    }

    /**
     * Get the {@link ItemStack} invovled in this event.
     * @return an item
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
}
