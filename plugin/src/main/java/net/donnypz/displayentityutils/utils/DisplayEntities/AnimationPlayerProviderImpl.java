package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AnimationPlayerProviderImpl implements AnimationPlayer.AnimationPlayerProvider {

    @Override
    public DisplayAnimationPlayer play(@NotNull DisplayAnimator animator, @NotNull SpawnedDisplayAnimation animation, @NotNull SpawnedDisplayEntityGroup group, @NotNull SpawnedDisplayAnimationFrame frame, int startFrameId, int delay, boolean playSingleFrame) {
        return new DisplayAnimationPlayer(animator, animation, group, frame, startFrameId, delay, playSingleFrame);
    }

    @Override
    public PacketAnimationPlayer playWithPackets(@NotNull DisplayAnimator animator, @NotNull SpawnedDisplayAnimation animation, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimationFrame frame, int startFrameId, int delay, boolean playSingleFrame) {
        return new PacketAnimationPlayer(animator, animation, group, frame, startFrameId, delay, playSingleFrame);
    }

    @Override
    public ClientAnimationPlayer playForClient(@NotNull Collection<Player> players, @NotNull DisplayAnimator animator, @NotNull SpawnedDisplayAnimation animation, @NotNull ActiveGroup<?> group, @NotNull SpawnedDisplayAnimationFrame frame, int startFrameId, int delay, boolean playSingleFrame) {
        return new ClientAnimationPlayerImpl(players, animator, animation, group, frame, startFrameId, delay, playSingleFrame);
    }
}
