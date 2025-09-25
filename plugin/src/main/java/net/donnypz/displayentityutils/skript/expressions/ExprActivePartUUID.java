package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Name("Active Part's Part UUID")
@Description("Get the part uuid of an active part, used to identify the part in its group and for animations. This is different than the uuid of the entity a part represents.")
@Examples({"set {_uuid} to {_spawnedpart}'s part uuid",
            "",
            "#3.0.0 and later",
            "set {_uuid} to {_packetpart}'s part uuid"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartUUID extends SimplePropertyExpression<ActivePart, UUID> {

    static {
        register(ExprActivePartUUID.class, UUID.class, "part uuid", "activeparts");
    }

    @Override
    public Class<? extends UUID> getReturnType() {
        return UUID.class;
    }

    @Override
    @Nullable
    public UUID convert(ActivePart part) {
        if (part == null){
            return null;
        }
        return part.getPartUUID();
    }

    @Override
    protected String getPropertyName() {
        return "part uuid";
    }

}
