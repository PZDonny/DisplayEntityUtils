package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.jetbrains.annotations.Nullable;

@Name("Active Part's Type")
@Description("Get the active part's type")
@Examples({"set {_type} to {_spawnedpart}'s active part type", "if {_type} is block_display:", "\tThis part represents a block display entity!"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartType extends SimplePropertyExpression<ActivePart, SpawnedDisplayEntityPart.PartType> {

    static {
        register(ExprActivePartType.class, SpawnedDisplayEntityPart.PartType.class, "[active] part[- | ]type", "activeparts");
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
        return "active part type";
    }

}
