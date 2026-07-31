package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AnimationPlayerProviderImpl implements AnimationPlayer.AnimationPlayerProvider {

    @Override
    public AnimationPlayer play(@NotNull DisplayAnimator animator,
                                       @NotNull SpawnedDisplayEntityGroup group,
                                       int startFrameId) {
        return new DisplayAnimationPlayer(animator, group, startFrameId);
    }

    @Override
    public AnimationPlayer playWithPackets(@NotNull DisplayAnimator animator,
                                                 @NotNull ActiveGroup<?> group,
                                                 int startFrameId) {
        return new PacketAnimationPlayer(animator, group, startFrameId);
    }

    @Override
    public ClientAnimationPlayer playForClient(@NotNull Collection<Player> players,
                                               @NotNull DisplayAnimator animator,
                                               @NotNull ActiveGroup<?> group,
                                               int startFrameId) {
        return new ClientAnimationPlayerImpl(players, animator, group, startFrameId);
    }

    @Override
    public AnimationPlayer showFrameForClient(@NotNull DisplayAnimator animator,
                                              @NotNull SpawnedDisplayAnimationFrame frame,
                                              @NotNull SpawnedDisplayEntityGroup group) {
        return new DisplayAnimationPlayer(animator, group, frame);
    }

    @Override
    public AnimationPlayer showFrameWithPackets(@NotNull DisplayAnimator animator, @NotNull SpawnedDisplayAnimationFrame frame, @NotNull ActiveGroup<?> group) {
        return new PacketAnimationPlayer(animator, group, frame);
    }

    @Override
    public AnimationPlayer showFrameForClient(@NotNull Collection<Player> players,
                                              @NotNull DisplayAnimator animator,
                                              @NotNull SpawnedDisplayAnimationFrame frame,
                                              @NotNull ActiveGroup<?> group) {
        return new ClientAnimationPlayerImpl(players, animator, group, frame);
    }
}
