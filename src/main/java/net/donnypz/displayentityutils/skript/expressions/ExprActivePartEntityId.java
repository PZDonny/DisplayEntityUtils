package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.jetbrains.annotations.Nullable;

@Name("Entity Id of Active Part")
@Description("Get the entity id of an active part.")
@Examples({"set {_id} to {_spawnedpart}'s deu entity id",
            "set {_id} to {_packetpart}'s deu entity id"})
@Since("3.2.2")
public class ExprActivePartEntityId extends SimplePropertyExpression<Object, Integer> {

    static {
        register(ExprActivePartEntityId.class, Integer.class, "[deu] entity id", "activepart");
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    @Nullable
    public Integer convert(Object o) {
        if (o instanceof ActivePart part){
            return part.getEntityId();
        }
        return null;
    }

    @Override
    protected String getPropertyName() {
        return "entity id";
    }

    @Override
    public boolean isSingle() {
        return true;
    }
}
