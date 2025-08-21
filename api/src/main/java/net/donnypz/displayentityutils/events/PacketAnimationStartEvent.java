package net.donnypz.displayentityutils.events;

import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.Collection;

/**
 * Called when a {@link DisplayAnimator} starts playing a {@link SpawnedDisplayAnimation}.
 * This is called SYNC once on an animator of the type {@link DisplayAnimator.AnimationType#LOOP}.
 */
public class PacketAnimationStartEvent extends PacketAnimationEvent implements Cancellable {
    private boolean isCancelled = false;

    public PacketAnimationStartEvent(ActiveGroup group, DisplayAnimator animator, SpawnedDisplayAnimation animation, Collection<Player> players){
       super(group, animator, animation, players);
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
