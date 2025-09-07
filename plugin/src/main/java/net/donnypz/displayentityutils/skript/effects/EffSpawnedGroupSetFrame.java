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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Show Animation Frame on Active Group")
@Description("Play a single animation frame on an active group, optionally with custom duration and delay.")
@Examples({"play frame with id 5 on {_spawnedgroup} from {_spawnedanimation}",
        "",
        "#3.0.0 and later",
        "show frame with id 2 on {_packetgroup} from {_spawnedanimation}",
        "show frame with id 12 on {_spawnedgroup} with {_spawnedanimation} for {_player}"})
@Since("2.6.2")
public class EffSpawnedGroupSetFrame extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupSetFrame.class,"(play|apply|show) frame with id %number% on %activegroup% (with|from) [anim[ation]] %spawnedanimation% " +
                "[d:[and] with duration %-timespan% and delay %-timespan%] [f:for %-players%]");

    }

    Expression<Number> frameID;
    Expression<ActiveGroup<?>> group;
    Expression<SpawnedDisplayAnimation> animation;
    Expression<Timespan> duration;
    Expression<Timespan> delay;
    Expression<Player> players;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        frameID = (Expression<Number>) expressions[0];
        group = (Expression<ActiveGroup<?>>) expressions[1];
        animation = (Expression<SpawnedDisplayAnimation>) expressions[2];
        if (parseResult.hasTag("d")){
            duration = (Expression<Timespan>) expressions[3];
            delay = (Expression<Timespan>) expressions[4];
        }
        if (parseResult.hasTag("f")){
            players = (Expression<Player>) expressions[5];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number n = frameID.getSingle(event);
        ActiveGroup<?> g = group.getSingle(event);
        SpawnedDisplayAnimation a = animation.getSingle(event);
        Player[] plrs = players == null ? null : players.getAll(event);
        if (n == null || g == null || a == null){
            return;
        }
        int frameId = n.intValue();
        try{
            if (duration == null){
                if (plrs != null){
                    for (Player p : plrs){
                        g.setToFrame(p, a, frameId);
                    }
                }
                else{
                    if (g instanceof SpawnedDisplayEntityGroup sg){
                        sg.setToFrame(a, frameId);
                    }
                    else if (g instanceof PacketDisplayEntityGroup pg) {
                        pg.setToFrame(a, frameId);
                    }
                }

            }
            else{
                Timespan dur = duration.getSingle(event);
                Timespan del = delay.getSingle(event);
                if (dur == null || del == null){
                    if (plrs != null){
                        for (Player p : plrs){
                            g.setToFrame(p, a, frameId);
                        }
                    }
                    else{
                        if (g instanceof SpawnedDisplayEntityGroup sg){
                            sg.setToFrame(a, frameId);
                        }
                        else if (g instanceof PacketDisplayEntityGroup pg) {
                            pg.setToFrame(a, frameId);
                        }
                    }

                }
                else{
                    if (plrs != null){
                        for (Player p : plrs){
                            g.setToFrame(p, a, frameId, (int) dur.getAs(Timespan.TimePeriod.TICK), (int) del.getAs(Timespan.TimePeriod.TICK));
                        }
                    }
                    else{
                        if (g instanceof SpawnedDisplayEntityGroup sg){
                            sg.setToFrame(a, frameId, (int) dur.getAs(Timespan.TimePeriod.TICK), (int) del.getAs(Timespan.TimePeriod.TICK));
                        }
                        else if (g instanceof PacketDisplayEntityGroup pg) {
                            pg.setToFrame(a, frameId, (int) dur.getAs(Timespan.TimePeriod.TICK), (int) del.getAs(Timespan.TimePeriod.TICK));
                        }
                    }
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
