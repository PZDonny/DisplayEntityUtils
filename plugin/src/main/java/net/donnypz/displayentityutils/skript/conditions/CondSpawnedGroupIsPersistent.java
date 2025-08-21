package net.donnypz.displayentityutils.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group Is Persistent")
@Description("Check if a spawned group is persist")
@Examples({"if {_group} is persistent:", "\tbroadcast \"The group wont despawn!\""})
@Since("2.6.2")
public class CondSpawnedGroupIsPersistent extends Condition {

    static {
        Skript.registerCondition(CondSpawnedGroupIsPersistent.class, "%spawnedgroup% (1¦is|2¦is(n't| not)) persistent");
    }

    Expression<SpawnedDisplayEntityGroup> group;

    @Override
    public boolean check(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isPersistent() == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group persistent: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
