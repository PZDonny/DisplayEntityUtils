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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Show Animation Frame on Spawned Group")
@Description("Play a single animation frame on a spawned group, optionally with custom duration and delay")
@Examples({"play frame with id 5 on {_spawnedgroup} from {_spawnedanimation}"})
@Since("2.6.2")
public class EffSpawnedGroupSetFrame extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupSetFrame.class,"(play|apply|show) frame with id %number% on %spawnedgroup% (with|from) [anim[ation]] %spawnedanimation% " +
                "[d:[and] with duration %timespan% and delay %timespan%] [:async[hronously]]");

    }

    Expression<Number> frameID;
    Expression<SpawnedDisplayEntityGroup> group;
    Expression<SpawnedDisplayAnimation> animation;
    Expression<Timespan> duration;
    Expression<Timespan> delay;
    boolean async;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        frameID = (Expression<Number>) expressions[0];
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[1];
        animation = (Expression<SpawnedDisplayAnimation>) expressions[2];
        async = parseResult.hasTag("async");
        if (parseResult.hasTag("d")){
            duration = (Expression<Timespan>) expressions[3];
            delay = (Expression<Timespan>) expressions[4];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number n = frameID.getSingle(event);
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        SpawnedDisplayAnimation a = animation.getSingle(event);
        if (n == null || g == null || a == null){
            return;
        }
        try{
            SpawnedDisplayAnimationFrame frame = a.getFrames().get(n.intValue());
            if (duration == null){
                g.setToFrame(a, frame, async);
            }
            else{
                Timespan dur = duration.getSingle(event);
                Timespan del = delay.getSingle(event);
                if (dur == null || del == null){
                    g.setToFrame(a, frame, async);
                }
                else{
                    g.setToFrame(a, frame, (int) dur.getAs(Timespan.TimePeriod.TICK), (int) del.getAs(Timespan.TimePeriod.TICK), async);
                }
            }
        }
        catch(IndexOutOfBoundsException e){
            Skript.error("Failed to play animation frame! ID is less than 0 or is >= the number of frames! ("+n.intValue()+"/"+a.getFrames().size()+")");
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set to frame: "+animation.toString(event, debug);
    }
}
