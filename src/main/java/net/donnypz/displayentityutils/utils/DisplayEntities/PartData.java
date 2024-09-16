package net.donnypz.displayentityutils.utils.DisplayEntities;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PartData {

    private final UUID uuid;
    private String worldName;

    PartData(@NotNull Entity entity){
        this(entity.getUniqueId(), entity.getWorld().getName());
    }

    PartData(@NotNull UUID uuid, @NotNull String worldName){
        this.uuid = uuid;
        this.worldName = worldName;
    }

    void setWorldName(String worldName){
        this.worldName = worldName;
    }

    /**
     * Get the UUID of the entity this PartData represents
     * @return a UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Get the world name of the entity this PartData represents
     * @return a string
     */
    public String getWorldName() {
        return worldName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()){
            return false;
        }

        PartData data = (PartData) obj;
        return uuid.equals(data.uuid) && worldName.equals(data.worldName);
    }

    @Override
    public int hashCode(){
        return Objects.hash(uuid, worldName);
    }
}
