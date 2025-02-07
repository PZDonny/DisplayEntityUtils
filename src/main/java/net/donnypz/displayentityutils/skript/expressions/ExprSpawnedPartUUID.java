package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Name("Spawned Part's Part UUID")
@Description("Get the part uuid of a spawned part. This is different than the uuid of the entity a part represents")
@Examples({"set {_uuid} to {_spawnedpart}'s part uuid"})
@Since("2.6.2")
public class ExprSpawnedPartUUID extends SimplePropertyExpression<SpawnedDisplayEntityPart, UUID> {

    static {
        register(ExprSpawnedPartUUID.class, UUID.class, "[the] part uuid", "spawnedpart");
    }

    @Override
    public Class<? extends UUID> getReturnType() {
        return UUID.class;
    }

    @Override
    @Nullable
    public UUID convert(SpawnedDisplayEntityPart part) {
        if (part == null){
            return null;
        }
        return part.getPartUUID();
    }

    @Override
    protected String getPropertyName() {
        return "part uuid";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
