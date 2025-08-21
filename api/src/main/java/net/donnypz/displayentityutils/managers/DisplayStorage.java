package net.donnypz.displayentityutils.managers;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface DisplayStorage {

    boolean saveDisplayEntityGroup(@NotNull DisplayEntityGroup group, @Nullable Player saver);

    void deleteDisplayEntityGroup(@NotNull String group, @Nullable Player deleter);

    @Nullable DisplayEntityGroup getDisplayEntityGroup(@NotNull String tag);

    boolean saveDisplayAnimation(@NotNull DisplayAnimation animation, @Nullable Player saver);

    void deleteDisplayAnimation(@NotNull String tag, @Nullable Player deleter);

    @Nullable DisplayAnimation getDisplayAnimation(@NotNull String tag);

    @NotNull List<String> getGroupTags();

    @NotNull List<String> getAnimationTags();


}
