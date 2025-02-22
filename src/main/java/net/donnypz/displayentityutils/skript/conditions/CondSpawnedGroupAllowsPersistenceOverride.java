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

@Name("Spawned Group Persistence Override")
@Description("Check if a spawned group allows chunk loading to override its persistence")
@Examples({"if {_group} allows persistence override:", "\tbroadcast\"The group's persistence will be overriden by config settings'!\""})
@Since("2.6.3")
public class CondSpawnedGroupAllowsPersistenceOverride extends Condition {

    static {
        Skript.registerCondition(CondSpawnedGroupAllowsPersistenceOverride.class, "%spawnedgroup% (1¦(is|allows)|2¦(is(n't| not) [allowing])) [chunk] persistence override");
    }

    Expression<SpawnedDisplayEntityGroup> group;

    @Override
    public boolean check(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.allowsPersistenceOverriding() == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group persistence override: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
