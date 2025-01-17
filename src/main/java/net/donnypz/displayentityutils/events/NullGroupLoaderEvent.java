package net.donnypz.displayentityutils.events;

import jdk.jfr.Experimental;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.controller.DisplayController;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@Experimental
public class NullGroupLoaderEvent extends NullLoaderMethodEvent {
    private static final HandlerList handlers = new HandlerList();
    private final DisplayController controller;
    private DisplayEntityGroup group;

    public NullGroupLoaderEvent(@NotNull DisplayController controller, @NotNull String tag){
        super(tag);
        this.controller = controller;
    }

    /**
     * Set the group that should be used for the {@link DisplayController} requesting it
     * @param group
     */
    public void setGroup(@NotNull DisplayEntityGroup group){
        this.group = group;
    }

    public @Nullable DisplayEntityGroup getGroup() {
        return group;
    }

    /**
     * Get the {@link DisplayController} involved in this event
     * @return a {@link DisplayController}
     */
    public @NotNull DisplayController getMobDisplayController() {
        return controller;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
