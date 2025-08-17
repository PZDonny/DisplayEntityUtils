package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Name("Entity of Spawned Part")
@Description("Get the Display or Interaction entity that a spawned part represents")
@Examples({"set {_entity} to {_spawnedpart}'s true entity"})
@Since("2.6.2")
public class ExprSpawnedPartAsEntity extends SimplePropertyExpression<SpawnedDisplayEntityPart, Entity> {

    static {
        register(ExprSpawnedPartAsEntity.class, Entity.class, "[the] [true] entity", "spawnedpart");
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    @Nullable
    public Entity convert(SpawnedDisplayEntityPart part) {
        if (part == null){
            return null;
        }
        return part.getEntity();
    }

    @Override
    protected String getPropertyName() {
        return "entity";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
