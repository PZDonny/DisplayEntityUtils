package net.donnypz.displayentityutils.utils.DisplayEntities;

import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public final class PacketLocation {

    PacketLocation(Location location){
        this.worldName = location.getWorld().getName();
        this.x = location.x();
        this.y = location.y();
        this.z = location.z();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    PacketLocation(Location location, Vector3f vector){
        this(DisplayUtils.getPivotLocation(Vector.fromJOML(vector), location, location.getYaw()));
    }

    String worldName;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;

    PacketLocation setRotation(float yaw, float pitch){
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    PacketLocation setCoordinates(Location location){
        this.x = location.x();
        this.y = location.y();
        this.z = location.z();
        return this;
    }

    Location toLocation(){
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
