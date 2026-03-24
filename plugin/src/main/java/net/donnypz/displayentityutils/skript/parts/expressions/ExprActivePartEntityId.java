package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.skript.parts.effects.EffActivePartTag;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Entity Id of Active Part")
@Description("Get the entity id of an active part. This can be used in packet operations.")
@Examples({"set {_id} to {_spawnedpart}'s deu entity id",
            "set {_id} to {_packetpart}'s deu entity id"})
@Since("3.2.2")
public class ExprActivePartEntityId extends SimplePropertyExpression<ActivePart, Integer> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActivePartEntityId.class, Integer.class)
                        .addPatterns(getPatterns("[deu] entity id", "activeparts"))
                        .supplier(ExprActivePartEntityId::new)
                        .build()
        );
    }

    @Override
    public Class<? extends Integer> getReturnType() {
        return Integer.class;
    }

    @Override
    @Nullable
    public Integer convert(ActivePart part) {
        return part.getEntityId();
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
