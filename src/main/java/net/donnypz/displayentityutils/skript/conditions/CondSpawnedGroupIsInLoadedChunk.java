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

@Name("Spawned Group Is In Loaded Chunk?")
@Description("Check if a spawned group is in a loaded chunk")
@Examples({"if {_group} is in a loaded chunk:", "\tbroadcast \"The group is in a loaded chunk!\""})
@Since("2.6.2")
public class CondSpawnedGroupIsInLoadedChunk extends Condition {

    static {
        Skript.registerCondition(CondSpawnedGroupIsInLoadedChunk.class, "%spawnedgroup% (1¦is|2¦is(n't| not)) in [a] loaded chunk");
    }

    Expression<SpawnedDisplayEntityGroup> group;

    @Override
    public boolean check(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isInLoadedChunk() == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group in loaded chunk: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
