package net.donnypz.displayentityutils.events;

import jdk.jfr.Experimental;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


@Experimental
abstract class NullLoaderMethodEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    private final String tag;


    public NullLoaderMethodEvent(@NotNull String tag){
        this.tag = tag;
    }


    /**
     * Get the tag involved in this event.
     * <p>Animation Tag if this is an {@link NullAnimationLoaderEvent}</p>
     * <p>Group Tag if this is an {@link NullGroupLoaderEvent}</p>
     * @return a string
     */
    public @NotNull String getTag(){
        return tag;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
