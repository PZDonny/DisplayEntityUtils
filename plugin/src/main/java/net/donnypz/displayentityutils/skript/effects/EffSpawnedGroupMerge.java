package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Merge Spawned Groups")
@Description("Merge two spawned groups as a single group")
@Examples({"#{_spawnedgroupB}'s parts will be merged into {_spawnedgroupA}. {_spawnedgroupB} will become unusable afterwards",
        "merge {_spawnedgroupA} with {_spawnedgroupB}"})
@Since("3.3.3")
public class EffSpawnedGroupMerge extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupMerge.class,"(merge|combine) %spawnedgroup% with %spawnedgroup%");
    }

    private Expression<SpawnedDisplayEntityGroup> groupA;
    private Expression<SpawnedDisplayEntityGroup> groupB;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        groupA = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        groupB = (Expression<SpawnedDisplayEntityGroup>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g1 = groupA.getSingle(event);
        if (g1 == null) return;
        SpawnedDisplayEntityGroup g2 = groupB.getSingle(event);
        if (g2 == null) return;
        g1.merge(g2);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "merge "+groupA.toString(event, debug) +" with "+groupB.toString(event, debug);
    }
}
