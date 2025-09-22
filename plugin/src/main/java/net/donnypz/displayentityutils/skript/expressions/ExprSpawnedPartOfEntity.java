package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Part of Entity")
@Description("Get the spawned part representing a Display or Interaction entity")
@Examples({"set {_spawnedpart} to {_entity}'s spawned part"})
@Since("2.6.2")
public class ExprSpawnedPartOfEntity extends SimplePropertyExpression<Entity, SpawnedDisplayEntityPart> {

    static {
        register(ExprSpawnedPartOfEntity.class, SpawnedDisplayEntityPart.class, "spawned[ |-]part", "entities");
    }

    @Override
    public Class<? extends SpawnedDisplayEntityPart> getReturnType() {
        return SpawnedDisplayEntityPart.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityPart convert(Entity entity) {
        return SpawnedDisplayEntityPart.getPart(entity);
    }

    @Override
    protected String getPropertyName() {
        return "spawnedpart";
    }

}
