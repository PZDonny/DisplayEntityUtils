package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityPart;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/*@Name("Translate Spawned Group")
@Description("Translate a spawned group")
@Examples({"deu translate {_spawnedgroup} 3 blocks left", "deu translate {_spawnedgroup} 5 blocks up over 20 ticks"})
@Since("2.6.2")*/
public class EffSpawnedGroupTranslate extends Effect {
    static {
        //Skript.registerEffect(EffSpawnedGroupTranslate.class,"[deu ]translate %spawnedgroup% [in direction] %direction% [:over %-timespan%]");
    }

    Expression<SpawnedDisplayEntityGroup> group;
    Expression<Direction> direction;
    Expression<Timespan> timespan;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        direction = (Expression<Direction>) expressions[1];
        if (parseResult.hasTag("over")){
            timespan = (Expression<Timespan>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        Direction dir = direction.getSingle(event);
        if (g == null || !g.isSpawned() || dir == null){
            return;
        }
        int duration = 0;
        if (timespan != null){
            Timespan ts = timespan.getSingle(event);
            if (ts != null){
                duration = (int) ts.getAs(Timespan.TimePeriod.TICK);
            }
        }

        Location groupLoc = g.getLocation();
        Vector displayVector = dir.getDirection(groupLoc);
        for (SpawnedDisplayEntityPart part : g.getSpawnedParts()){
            if (part.getType() == SpawnedDisplayEntityPart.PartType.INTERACTION){
                part.translate((float) displayVector.length(), duration, 0, displayVector);
            }
            else{
                Location loc = part.getEntity().getLocation();
                loc.setPitch(groupLoc.getPitch());
                loc.setYaw(groupLoc.getYaw());
                Vector iVec = dir.getDirection(loc);
                part.translate((float) displayVector.length(), duration, 0, iVec);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "translate spawned group: "+group.toString(event, debug);
    }
}
