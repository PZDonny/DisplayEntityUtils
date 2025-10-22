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
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group/Part / Multi Part Selection Interpolation")
@Description("Set the interpolation duration/delay of an active group / spawned part / part selection")
@Examples({"deu set interpolation duration of {_spawnedpart} to 5 ticks",
        "deu set {_spawnedgroup}'s interpolation delay to 2 ticks"})
@Since("2.6.2, 3.0.0 (Packet)")
public class EffActiveInterpolation extends Effect {
    static {
        Skript.registerEffect(EffActiveInterpolation.class,"[deu ]set interpolation (:duration|delay) of %activegroups/multipartfilters/activeparts% to %timespan%",
                "[deu] set %activegroups/multipartfilters/activeparts%'s interpolation (:duration|delay) to %timespan%");
    }

    Expression<?> object;
    Expression<Timespan> timespan;
    boolean duration;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        timespan = (Expression<Timespan>) expressions[1];
        duration = parseResult.hasTag("duration");
        return true;
    }

    @Override
    protected void execute(Event event) {
        Timespan ts = timespan.getSingle(event);
        if (ts == null) {
            return;
        }
        long value = ts.getAs(Timespan.TimePeriod.TICK);
        for (Object o : object.getArray(event)) {
            if (o instanceof Active active){
                if (duration){
                    active.setInterpolationDuration((int) value);
                }
                else{
                    active.setInterpolationDelay((int) value);
                }
            }
        }
    }


    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set interpolation duration/delay: "+object.toString(event, debug);
    }
}
