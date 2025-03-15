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
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedPartSelection;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

@Name("Translate Spawned Group/Parts/Selection")
@Description("Change the translation of a spawned group's parts, a part selection's parts, or a spawned part")
@Examples({"add {_vector} to {_spawnedgroup}'s translation", "add {_vector} to {_partselection}'s translation over 20 ticks"})
@Since("2.7.3")
public class EffSpawnedTranslate extends Effect {
    static {
        Skript.registerEffect(EffSpawnedTranslate.class,"add %vector% to %spawnedgroups/spawnedparts/partselections%['s] translation [time:(for|over|with) [duration] %-timespan%]");
    }

    Expression<Vector> vector;
    Expression<Object> spawned;
    Expression<Timespan> timespan;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        vector = (Expression<Vector>) expressions[0];
        spawned = (Expression<Object>) expressions[1];
        if (parseResult.hasTag("time")){
            timespan = (Expression<Timespan>) expressions[2];
        }

        return true;
    }

    @Override
    protected void execute(Event event) {
        Vector v = vector.getSingle(event);
        Spawned[] spawned = (Spawned[]) this.spawned.getArray(event);
        if (spawned == null || v == null) return;
        float distance = (float) v.length();
        int ticks;
        if (timespan != null){
            Timespan ts = timespan.getSingle(event);
            ticks = ts == null ? -1 : (int) ts.getAs(Timespan.TimePeriod.TICK);
        }
        else{
            ticks = -1;
        }

        for (Spawned s : spawned){
            if (s instanceof SpawnedDisplayEntityGroup g){
                g.translate(v, distance, ticks, -1);
            }
            else if (s instanceof SpawnedDisplayEntityPart p){
                p.translate(distance, ticks, -1 , v);
            }
            else if (s instanceof SpawnedPartSelection sel){
                sel.translate(distance, ticks, -1, v);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "translate spawned group/part";
    }
}
