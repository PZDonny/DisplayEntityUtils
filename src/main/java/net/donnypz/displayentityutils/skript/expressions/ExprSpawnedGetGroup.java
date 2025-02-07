package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group From SpawnedPart/PartSelection")
@Description("Get the spawned group of a spawned part or part selection")
@Examples({"set {_spawnedgroup} to {_spawnedpart}'s spawned group"})
@Since("2.6.2")
public class ExprSpawnedGetGroup extends SimplePropertyExpression<Object, SpawnedDisplayEntityGroup> {

    static {
        register(ExprSpawnedGetGroup.class, SpawnedDisplayEntityGroup.class, "[the] spawned[ |-]group", "spawnedpart/partselection");
    }

    @Override
    public Class<? extends SpawnedDisplayEntityGroup> getReturnType() {
        return SpawnedDisplayEntityGroup.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityGroup convert(Object spawned) {
        if (spawned instanceof SpawnedDisplayEntityPart part){
            return part.getGroup();
        }
        else if (spawned instanceof SpawnedPartSelection sel){
            return sel.getGroup();
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
