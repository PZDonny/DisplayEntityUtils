package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group Location")
@Description("Get the location of a spawned group")
@Examples({"set {_location} to {_spawnedgroup}'s true location"})
@Since("2.6.2")
public class ExprSpawnedGroupLocation extends SimplePropertyExpression<SpawnedDisplayEntityGroup, Location> {

    static {
        register(ExprSpawnedGroupLocation.class, Location.class, "[the] true location", "spawnedgroup");
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    @Nullable
    public Location convert(SpawnedDisplayEntityGroup group) {
        if (group == null){
            return null;
        }
        return group.getLocation();
    }

    @Override
    protected String getPropertyName() {
        return "true location";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
