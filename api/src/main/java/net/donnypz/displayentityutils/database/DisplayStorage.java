package net.donnypz.displayentityutils.database;

import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class DisplayStorage {

    protected final ComponentLogger logger;

    public DisplayStorage(ComponentLogger logger) {
        this.logger = logger;
    }

    public abstract boolean isEnabled();

    public abstract boolean saveDisplayEntityGroup(@NotNull DisplayEntityGroup group, @Nullable Player saver);

    public abstract void deleteDisplayEntityGroup(@NotNull String group, @Nullable Player deleter);

    public @Nullable abstract DisplayEntityGroup getDisplayEntityGroup(@NotNull String tag);

    public abstract boolean saveDisplayAnimation(@NotNull DisplayAnimation animation, @Nullable Player saver);

    public abstract void deleteDisplayAnimation(@NotNull String tag, @Nullable Player deleter);

    public @Nullable abstract DisplayAnimation getDisplayAnimation(@NotNull String tag);

    public @NotNull abstract List<String> getGroupTags();

    public @NotNull abstract List<String> getGroupTags(int page, int size);

    public @NotNull abstract List<String> getAnimationTags();

    public @NotNull abstract List<String> getAnimationTags(int page, int size);
}
