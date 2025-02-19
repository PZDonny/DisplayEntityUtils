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
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group/Part/Selection View Range")
@Description("Change the view range of a DisplayEntityUtils spawned object")
@Examples({"set {_spawnedgroup}'s view range multiplier to 5", "make {_spawnedpart}'s view range multiplier 0.5"})
@Since("2.6.2")
public class EffSpawnedViewRange extends Effect {
    static {
        Skript.registerEffect(EffSpawnedViewRange.class,"(make|set) %spawnedgroups/spawnedparts/partselections%['s] view range multiplier [to] %number%");
    }

    Expression<?> object;
    Expression<Number> range;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        range = (Expression<Number>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Spawned[] spawned = (Spawned[]) object.getArray(event);
        Number n = range.getSingle(event);
        if (spawned == null || n == null) return;
        for (Spawned s : spawned){
            s.setViewRange(n.floatValue());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "view range multiplier: "+object.toString(event, debug);
    }
}
