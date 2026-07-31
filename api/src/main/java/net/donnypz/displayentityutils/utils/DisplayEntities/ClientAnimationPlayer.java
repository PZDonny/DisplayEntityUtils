package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ClientAnimationPlayer extends AnimationPlayer{

    final Object playerLock = new Object();
    final Set<Player> players = Collections.newSetFromMap(new ConcurrentHashMap<>());

    ClientAnimationPlayer(@NotNull Collection<Player> players,
                          @NotNull DisplayAnimator animator,
                          @NotNull ActiveGroup<?> group,
                          int startFrameId)
    {
        super(animator, group, false, true);
        this.players.addAll(players);
        animator.addPlayers(players, this);
        startAnimation(group, startFrameId);
    }

    public ClientAnimationPlayer(@NotNull Collection<Player> players,
                                 @NotNull DisplayAnimator animator,
                                 @NotNull ActiveGroup<?> group,
                                 @NotNull SpawnedDisplayAnimationFrame frame) {
        super(animator, group,true, true);
        this.players.addAll(players);
        animator.addPlayers(players, this);
        startAnimation(group, frame, -1);
    }

    boolean contains(Player player){
        synchronized (playerLock){
            return players.contains(player);
        }
    }
}
