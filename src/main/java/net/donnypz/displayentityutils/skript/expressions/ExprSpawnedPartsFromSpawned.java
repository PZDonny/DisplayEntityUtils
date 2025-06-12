package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.SequencedCollection;

@Name("Spawned Parts of Spawned Group / Part Selection")
@Description("Get the spawned parts of a spawned group or part selection")
@Examples({"set {_spawnedparts::*} to {_spawnedgroup}'s parts"})
@Since("2.6.2")
public class ExprSpawnedPartsFromSpawned extends SimpleExpression<SpawnedDisplayEntityPart> {

    static {
        String property = "[the] [spawned][ |-]parts";
        String fromType = "spawnedgroup/partselection";
        Skript.registerExpression(ExprSpawnedPartsFromSpawned.class, SpawnedDisplayEntityPart.class, ExpressionType.PROPERTY, PropertyExpression.getPatterns(property, fromType));
    }

    Expression<Object> spawned;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        spawned = (Expression<Object>) expressions[0];
        return true;
    }

    @Override
    public Class<? extends SpawnedDisplayEntityPart> getReturnType() {
        return SpawnedDisplayEntityPart.class;
    }


    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    protected SpawnedDisplayEntityPart @Nullable [] get(Event event) {
        Object obj = spawned.getSingle(event);
        if (obj instanceof SpawnedDisplayEntityGroup g){
            return g.getSpawnedParts().toArray(new SpawnedDisplayEntityPart[0]);
        }
        else if (obj instanceof SpawnedPartSelection sel){
            return sel.getSelectedParts().toArray(new SpawnedDisplayEntityPart[0]);
        }

        return null;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "parts from" + spawned.toString(event, debug);
    }
}
