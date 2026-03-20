package net.donnypz.displayentityutils.skript.parts.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.UUID;

@Name("Entity of Spawned Part")
@Description("Get the Display or Interaction entity that a part represents. " +
        "\nThis should only be done on non-packet based parts")
@Examples({"set {_entity} to {_spawnedpart}'s true entity",
            "",
            "#3.3.2 and later",
            "set {_entity} to {_spawnedpart}'s spawned part entity"})
@Since("2.6.2, 3.3.2 (Plural)")
public class ExprSpawnedPartAsEntity extends SimplePropertyExpression<SpawnedDisplayEntityPart, Entity> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprSpawnedPartAsEntity.class, Entity.class)
                        .addPatterns(getPatterns("[true] [spawned] part entity", "spawnedparts"))
                        .supplier(ExprSpawnedPartAsEntity::new)
                        .build()
        );
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    @Nullable
    public Entity convert(SpawnedDisplayEntityPart part) {
        return part != null ? part.getEntity() : null;
    }

    @Override
    protected String getPropertyName() {
        return "spawned part entity";
    }

}
