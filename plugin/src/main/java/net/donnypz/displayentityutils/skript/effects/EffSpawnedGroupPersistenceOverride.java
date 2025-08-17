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

@Name("Spawned Group Persistence Override")
@Description("Change whether a spawned group allows chunk loading to override its persistence")
@Examples({"let {_spawnedgroup} allow persistence override"})
@Since("2.6.3")
public class EffSpawnedGroupPersistenceOverride extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupPersistenceOverride.class,"(make|set|let) %spawnedgroups% [to] [:not] [allow[ing]] [chunk] persistence override");
    }

    Expression<SpawnedDisplayEntityGroup> object;
    boolean override;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        override = !parseResult.hasTag("not");
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup[] groups = object.getArray(event);
        if (groups == null) return;
        for (SpawnedDisplayEntityGroup g : groups){
            if (g != null){
                g.setPersistenceOverride(override);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "spawned group persistence override: "+object.toString(event, debug);
    }
}
