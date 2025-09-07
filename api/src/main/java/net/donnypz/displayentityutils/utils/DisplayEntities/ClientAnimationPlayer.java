package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class ClientAnimationPlayer extends AnimationPlayer{
    final Object playerLock = new Object();
    final Set<Player> players = Collections.newSetFromMap(new ConcurrentHashMap<>());

    ClientAnimationPlayer(@NotNull Collection<Player> players,
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

    boolean contains(Player player){
        synchronized (playerLock){
            return players.contains(player);
        }
    }


    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on a {@link SpawnedDisplayEntityGroup}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     */
    static void setGroupToFrame(@NotNull Player player, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        new ClientAnimationPlayer(Set.of(player), animator, animation, group, frame, -1, 0, true);
    }

    /**
     * Display the transformations of a {@link SpawnedDisplayAnimationFrame} on a {@link SpawnedDisplayEntityGroup}
     * @param group the group the transformations should be applied to
     * @param animation the animation the frame is from
     * @param frame the frame to display
     * @param duration how long the frame should play
     * @param delay how long until the frame should start playing
     */
    static void setGroupToFrame(@NotNull Player player, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayAnimationFrame frame, int duration, int delay){
        DisplayAnimator animator = new DisplayAnimator(animation, DisplayAnimator.AnimationType.LINEAR);
        SpawnedDisplayAnimationFrame clonedFrame = frame.clone();
        clonedFrame.duration = duration;
        new ClientAnimationPlayer(Set.of(player), animator, animation, group, clonedFrame, -1, delay, true);
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
