package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

final class ClientAnimationPlayerImpl extends ClientAnimationPlayer{

    ClientAnimationPlayerImpl(@NotNull Collection<Player> players,
                              @NotNull DisplayAnimator animator,
                              @NotNull SpawnedDisplayAnimation animation,
                              @NotNull ActiveGroup<?> group,
                              @NotNull SpawnedDisplayAnimationFrame frame,
                              int startFrameId,
                              int delay,
                              boolean playSingleFrame)
    {
        super(animator, animation, group, frame, startFrameId, delay, playSingleFrame, true);
        this.players.addAll(players);
    }

    @Override
    public void prepareAnimation(SpawnedDisplayAnimation animation, ActiveGroup<?> group, SpawnedDisplayAnimationFrame frame, int frameId, int delay){
        group.addActiveAnimator(animator);
        MultiPartSelection<?> selection = animation.hasFilter() ? group.createPartSelection(animation.filter) : group.createPartSelection();
        selection.addPlayerAnimationPlayer(this);
        Bukkit
                .getScheduler()
                .runTaskLaterAsynchronously(DisplayAPI.getPlugin(),
                        () -> executeAnimation(players, animation, group, selection, frame, frameId, playSingleFrame),
                        Math.max(delay, 0));
    }

    @Override
    protected boolean canContinueAnimation(ActiveGroup<?> group) {
        return group.masterPart != null && group instanceof SpawnedDisplayEntityGroup g && g.isRegistered();
    }

    @Override
    protected void handleAnimationInterrupted(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        animator.stop(players, group);
        if (group instanceof SpawnedDisplayEntityGroup g){
            if (!g.isRegistered()){
                removeSelection(selection);
            }
        }
    }

    @Override
    protected void handleAnimationComplete(ActiveGroup<?> group, MultiPartSelection<?> selection) {
        animator.stop(players, group);
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
                else if (!animator.isAnimating(p, group)){
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
