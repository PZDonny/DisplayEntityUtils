package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Parts of Spawned Group / Part Selection")
@Description("Get the spawned parts of a spawned group or part selection")
@Examples({"set {_spawnedparts::*} to {_spawnedgroup}'s parts"})
@Since("2.6.2")
public class ExprSpawnedPartsFromSpawned extends SimplePropertyExpression<Object, SpawnedDisplayEntityPart[]> {

    static {
        register(ExprSpawnedPartsFromSpawned.class, SpawnedDisplayEntityPart[].class, "[the] [spawned][ |-]parts", "spawnedgroup/partselection");
    }

    @Override
    public Class<? extends SpawnedDisplayEntityPart[]> getReturnType() {
        return SpawnedDisplayEntityPart[].class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityPart[] convert(Object obj) {
        if (obj instanceof SpawnedDisplayEntityGroup g){
            return g.getSpawnedParts().toArray(new SpawnedDisplayEntityPart[0]);
        }
        else if (obj instanceof SpawnedPartSelection sel){
            return sel.getSelectedParts().toArray(new SpawnedDisplayEntityPart[0]);
        }

        return null;

    }

    @Override
    protected String getPropertyName() {
        return "parts";
    }

    @Override
    public boolean isSingle() {
        return false;
    }
}
