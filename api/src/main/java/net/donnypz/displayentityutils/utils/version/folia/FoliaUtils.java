package net.donnypz.displayentityutils.utils.version.folia;

import io.papermc.paper.entity.TeleportFlag;
import net.donnypz.displayentityutils.DisplayAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class FoliaUtils {

    private FoliaUtils(){}

    /**
     * Teleport an entity
     * @param entity the entity to teleport
     * @param location the teleport location
     */
    public static void teleport(Entity entity, Location location){
        if (isFolia()){
            entity.teleportAsync(location);
        }
        else{
            entity.teleport(location);
        }
    }

    /**
     * Teleport an entity
     * @param entity the entity to teleport
     * @param location the teleport location
     * @param flags teleport flags
     */
    public static void teleport(Entity entity, Location location, TeleportFlag... flags){
        if (isFolia()){
            entity.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN, flags);
        }
        else{
            entity.teleport(location, flags);
        }
    }

    /**
     * Teleport an entity async
     * @param entity the entity to teleport
     * @param location the teleport location
     * @return A {@link CompletableFuture} with the teleport result
     */
    public static @NotNull CompletableFuture<Boolean> teleportAsync(Entity entity, Location location){
        return entity.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Teleport an entity async
     * @param entity the entity to teleport
     * @param location the teleport location
     * @param flags teleport flags
     * @return A {@link CompletableFuture} with the teleport result
     */
    public static @NotNull CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, TeleportFlag... flags){
        return entity.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN, flags);
    }

    /**
     * Teleport an entity, automatically determining whether to do it async
     * @param entity the entity to teleport
     * @param location the teleport location
     * @return an {@link Optional} possibly containing a {@link CompletableFuture} with the teleport result.
     * Empty if the teleport was performed sync.
     */
    public static @NotNull Optional<CompletableFuture<Boolean>> teleportSafe(Entity entity, Location location){
        if (Bukkit.isPrimaryThread()){
            teleport(entity, location);
            return Optional.empty();
        }
        else{
            return Optional.of(teleportAsync(entity, location));
        }
    }

    /**
     * Teleport an entity, automatically determining whether to do it async
     * @param entity the entity to teleport
     * @param location the teleport location
     * @return an {@link Optional} possibly containing a {@link CompletableFuture} with the teleport result.
     * Empty if the teleport was performed sync.
     */
    public static @NotNull Optional<CompletableFuture<Boolean>> teleportSafe(Entity entity, Location location, TeleportFlag... flags){
        if (Bukkit.isPrimaryThread()){
            teleport(entity, location, flags);
            return Optional.empty();
        }
        else{
            return Optional.of(teleportAsync(entity, location, flags));
        }
    }

    static boolean isFolia() {
        return DisplayAPI.isFolia();
    }
}
