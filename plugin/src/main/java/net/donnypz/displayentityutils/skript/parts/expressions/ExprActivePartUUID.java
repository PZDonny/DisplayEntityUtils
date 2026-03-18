package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.UUID;

@Name("Active Part's Part UUID")
@Description("Get the part uuid of an active part, used to identify the part in its group and for animations. " +
        "\nThis is different than the uuid of the entity a part represents." +
        "\nPart UUIDs are typically used to identify which part is which internally for animations")
@Examples({"set {_uuid} to {_spawnedpart}'s part uuid",
            "",
            "#3.0.0 and later",
            "set {_uuid} to {_packetpart}'s part uuid"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartUUID extends SimplePropertyExpression<ActivePart, UUID> {


    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActivePartUUID.class, UUID.class)
                        .addPatterns(getPatterns("part uuid", "activeparts"))
                        .supplier(ExprActivePartUUID::new)
                        .build()
        );
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
