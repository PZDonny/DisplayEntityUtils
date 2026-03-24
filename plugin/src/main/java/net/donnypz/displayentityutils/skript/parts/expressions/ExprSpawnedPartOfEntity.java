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

@Name("Spawned Part of Entity")
@Description("Get the spawned part that represents a part-eligible entity")
@Examples({"set {_foundpart} to {_entity}'s deu part",
        "",
        "#3.4.3 or earlier",
        "set {_foundpart} to {_entity}'s spawned part"})
@Since("2.6.2, 3.3.2 (Plural)")
public class ExprSpawnedPartOfEntity extends SimplePropertyExpression<Entity, SpawnedDisplayEntityPart> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprSpawnedPartOfEntity.class, SpawnedDisplayEntityPart.class)
                        .addPatterns(getPatterns("deu part", "entities"))
                        .supplier(ExprSpawnedPartOfEntity::new)
                        .build()
        );
    }

    @Override
    public Class<? extends SpawnedDisplayEntityPart> getReturnType() {
        return SpawnedDisplayEntityPart.class;
    }

    @Override
    @Nullable
    public SpawnedDisplayEntityPart convert(Entity entity) {
        return SpawnedDisplayEntityPart.getPart(entity);
    }

    @Override
    protected String getPropertyName() {
        return "spawnedpart";
    }

}
