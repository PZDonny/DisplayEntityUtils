package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group of Entity")
@Description("Get the spawned group of a Display or Interaction entity")
@Examples({"set {_spawnedgroup} to {_interaction}'s spawned group"})
@Since("2.6.2")
public class ExprSpawnedGroupOfEntity extends SimplePropertyExpression<Entity, SpawnedDisplayEntityGroup> {

    static {
        register(ExprSpawnedGroupOfEntity.class, SpawnedDisplayEntityGroup.class, "[the] spawned[ |-]group", "entity");
    }

    @Override
    public Class<? extends SpawnedDisplayEntityGroup> getReturnType() {
        return SpawnedDisplayEntityGroup.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityGroup convert(Entity entity) {
        SpawnedDisplayEntityPart part = null;
        if (entity instanceof Display display){
            part = SpawnedDisplayEntityPart.getPart(display);
        }
        else if (entity instanceof Interaction interaction){
            part = SpawnedDisplayEntityPart.getPart(interaction);
        }
        if (part != null){
            return part.getGroup();
        }

        return null;
    }

    @Override
    protected String getPropertyName() {
        return "spawnedgroup";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
