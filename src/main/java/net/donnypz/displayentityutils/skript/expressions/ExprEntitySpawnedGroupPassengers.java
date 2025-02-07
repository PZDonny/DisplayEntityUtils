package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group Passengers")
@Description("Get the spawned groups riding an entity")
@Examples({"set {_spawnedgroups::*} to spawned group passengers of {_entity}", "set {_spawnedgroups::*} to {_entity}'s spawned group passengers"})
@Since("2.6.2")
public class ExprEntitySpawnedGroupPassengers extends SimplePropertyExpression<Entity, SpawnedDisplayEntityGroup[]> {

    static {
        register(ExprEntitySpawnedGroupPassengers.class, SpawnedDisplayEntityGroup[].class, "[the] spawned[ |-]group passengers", "entity");
    }

    @Override
    public Class<? extends SpawnedDisplayEntityGroup[]> getReturnType() {
        return SpawnedDisplayEntityGroup[].class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityGroup[] convert(Entity entity) {
        if (entity == null){
            return null;
        }
        return DisplayUtils.getGroupPassengers(entity).toArray(new SpawnedDisplayEntityGroup[0]);
    }

    @Override
    protected String getPropertyName() {
        return "spawnedgroup passengers";
    }

    @Override
    public boolean isSingle() {
        return false;
    }
}
