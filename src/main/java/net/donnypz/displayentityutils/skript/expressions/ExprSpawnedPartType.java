package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Part's Type")
@Description("Get the active part's type")
@Examples({"set {_type} to {_spawnedpart}'s part type", "if {_type} is parttype_interaction:", "\tThis part represents an interaction entity!"})
@Since("2.6.2")
public class ExprSpawnedPartType extends SimplePropertyExpression<ActivePart, SpawnedDisplayEntityPart.PartType> {

    static {
        register(ExprSpawnedPartType.class, SpawnedDisplayEntityPart.PartType.class, "[the] [part( |-)?]type", "activepart");
    }

    @Override
    public Class<? extends SpawnedDisplayEntityPart.PartType> getReturnType() {
        return SpawnedDisplayEntityPart.PartType.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityPart.PartType convert(ActivePart part) {
        if (part == null){
            return null;
        }
        return part.getType();
    }

    @Override
    protected String getPropertyName() {
        return "type";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
