package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

@Name("Spawned Group Passengers")
@Description("Get the spawned groups riding an entity")
@Examples({"set {_spawnedgroups::*} to spawned group passengers of {_entity}",
        "set {_spawnedgroups::*} to {_entity}'s spawned group passengers"})
@Since("2.6.2, 3.3.2 (Plural)")
public class ExprEntitySpawnedGroupPassengers extends PropertyExpression<Entity,SpawnedDisplayEntityGroup> {

    static {
        register(ExprEntitySpawnedGroupPassengers.class, SpawnedDisplayEntityGroup.class, "spawned[ |-]group passengers", "entities");
    }

    private Expression<Entity> entity;

    @Override
    public Class<? extends SpawnedDisplayEntityGroup> getReturnType() {
        return SpawnedDisplayEntityGroup.class;
    }


    @Override
    protected SpawnedDisplayEntityGroup[] get(Event event, Entity[] objects) {
        return Arrays.stream(objects).filter(Objects::nonNull).flatMap(entity -> DisplayUtils.getGroupPassengers(entity).stream()).toArray(SpawnedDisplayEntityGroup[]::new);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawned group passengers of "+entity.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Entity>) expressions[0]);
        return true;
    }
}
