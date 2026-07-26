package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

final class ClientAnimationPlayerImpl extends ClientAnimationPlayer{

    ClientAnimationPlayerImpl(@NotNull Collection<Player> players,
                              @NotNull DisplayAnimator animator,
                              @NotNull ActiveGroup<?> group,
                              int startFrameId)
    {
        super(players, animator, group, startFrameId);
    }

    public ClientAnimationPlayerImpl(@NotNull Collection<Player> players,
                                     @NotNull DisplayAnimator animator,
                                     @NotNull ActiveGroup<?> group,
                                     @NotNull SpawnedDisplayAnimationFrame frame) {
        super(players, animator, group, frame);
    }

    @Override
    protected void onAnimationStart(MultiPartSelection<?> selection) {
        selection.addPlayerAnimationPlayer(this);
    }

    @Override
    protected boolean canFrameStart(ActiveGroup<?> group) {
        return group.isRegistered();
    }

    @Override
    protected void handleAnimationInterrupted(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        animator.stop(players, this);
        removeSelection(selection);
    }

    @Override
    protected void handleAnimationComplete(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        animator.stop(players, this);
        removeSelection(selection);
    }

    @Override
    protected boolean onStartNewFrame(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        synchronized (playerLock){
            Iterator<Player> iter = players.iterator();
            while(iter.hasNext()){
                Player p = iter.next();
                if (!p.isConnected()){
                    animator.stop(p);
                    iter.remove();
                }
                else if (!animator.isAnimating(p, this)){
                    iter.remove();
                }
            }
            if (players.isEmpty()){
                removeSelection(selection);
                return false;
            }
        }
        return true;
    }

    private void removeSelection(MultiPartSelection<?> selection){
        selection.removePlayerAnimationPlayer(this);
        selection.remove();
    }
}
