package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group Passengers")
@Description("Get the spawned groups riding an entity")
@Examples({"set {_spawnedgroups::*} to spawned group passengers of {_entity}", "set {_spawnedgroups::*} to {_entity}'s spawned group passengers"})
@Since("2.6.2")
public class ExprEntitySpawnedGroupPassengers extends SimpleExpression<SpawnedDisplayEntityGroup> {

    static {
        String property = "[the] spawned[ |-]group passengers";
        String fromType = "entity";
        Skript.registerExpression(ExprEntitySpawnedGroupPassengers.class, SpawnedDisplayEntityGroup.class, ExpressionType.PROPERTY, PropertyExpression.getPatterns(property, fromType));
    }

    Expression<Entity> entity;

    @Override
    public Class<? extends SpawnedDisplayEntityGroup> getReturnType() {
        return SpawnedDisplayEntityGroup.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    protected SpawnedDisplayEntityGroup @Nullable [] get(Event event) {
        Entity e = entity.getSingle(event);
        if (e == null){
            return null;
        }
        return DisplayUtils.getGroupPassengers(e).toArray(new SpawnedDisplayEntityGroup[0]);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawnedgroup passengers of "+entity.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        entity = (Expression<Entity>) expressions[0];
        return true;
    }
}
