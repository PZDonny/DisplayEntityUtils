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

@Name("Unregister Spawned Group")
@Description("Unregister a spawned group from DisplayEntityUtils, making the group unusable")
@Examples({"unregister {_spawnedgroup}", "unregister {_spawnedgroup} and despawn with forced chunk loading"})
@Since("2.6.2")
public class EffSpawnedGroupUnregister extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupUnregister.class,"(unregister|delete|remove) %spawnedgroup% [d:[and ]despawn [f:[with|and] forced [chunk loading]]]");
    }

    Expression<SpawnedDisplayEntityGroup> group;
    boolean despawn;
    boolean forced;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        despawn = parseResult.hasTag("d");
        forced = parseResult.hasTag("f");
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        if (g == null){
            return;
        }
        g.unregister(despawn, forced);

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "unregister spawned group: "+group.toString(event, debug);
    }
}
