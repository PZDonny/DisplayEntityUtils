package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

abstract class ClientAnimationPlayer extends AnimationPlayer{

    final Object playerLock = new Object();
    final Set<Player> players = Collections.newSetFromMap(new ConcurrentHashMap<>());

    ClientAnimationPlayer(@NotNull DisplayAnimator animator, @NotNull SpawnedDisplayAnimation animation, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimationFrame frame, int startFrameId, int delay, boolean playSingleFrame) {
        super(animator, animation, group, frame, startFrameId, delay, playSingleFrame, true);
    }

    boolean contains(Player player){
        synchronized (playerLock){
            return players.contains(player);
        }
    }
}
