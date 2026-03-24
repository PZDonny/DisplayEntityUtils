package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActivePart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Part's Type")
@Description("Get the active part's type")
@Examples({"set {_type} to {_spawnedpart}'s active part type",
        "if {_type} is block_display:",
        "\tThis part represents a block display entity!"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.2 (Plural)")
public class ExprActivePartType extends SimplePropertyExpression<ActivePart, SpawnedDisplayEntityPart.PartType> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActivePartType.class, SpawnedDisplayEntityPart.PartType.class)
                        .addPatterns(getPatterns("[active] part[- | ]type", "activeparts"))
                        .supplier(ExprActivePartType::new)
                        .build()
        );
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
