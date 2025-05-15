package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Spawned Group Scale Multiplier")
@Description("Change the scale multiplier of a spawned group")
@Examples({"set {_spawnedgroup}'s scale multiplier to 7 over 0 ticks", "make {_spawnedgroup}'s scale multiplier 0.5 over 10 ticks"})
@Since("2.6.3")
public class EffSpawnedGroupScaleMultiplier extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupScaleMultiplier.class,"(make|set) %spawnedgroups%['s] scale [multiplier] [to] %number% (for|over) %timespan% [i:and [scale] interactions]");
    }

    Expression<SpawnedDisplayEntityGroup> group;
    Expression<Number> multiplier;
    Expression<Timespan> timespan;
    boolean scaleInteractions;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        multiplier = (Expression<Number>) expressions[1];
        timespan = (Expression<Timespan>) expressions[2];
        scaleInteractions = parseResult.hasTag("i");
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup[] spawned = group.getArray(event);
        Number n = multiplier.getSingle(event);
        Timespan ts = timespan.getSingle(event);
        if (spawned == null || n == null || ts == null) return;
        for (SpawnedDisplayEntityGroup s : spawned){
            s.scale(n.floatValue(), (int) ts.getAs(Timespan.TimePeriod.TICK), scaleInteractions);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "scale multiplier: "+group.toString(event, debug);
    }
}
