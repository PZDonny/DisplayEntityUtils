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
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

@Name("Play Animation Camera")
@Description("Make players follow an animation's camera on an active group using a display animator or animation")
@Examples({"play camera on {_packetgroup} with {_animator} for {_player}",
            "play camera on {_activegroup} using {_animation} for {_player} starting at frame 3",
            "",
            "play camera on {_activegroup} using {_animation} for {_player} with a start transition of 3 ticks and end delay of 2 seconds",
            "",
            "play camera on {_activegroup} using {_animation} for {_player} starting at frame 8 with a start transition of 3 seconds and an end delay of 10 ticks"})
@Since("3.3.6")
public class EffActiveGroupPlayAnimationCamera extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupPlayAnimationCamera.class,"(start|play) [anim[ation]] camera on %activegroup% (using|with) %displayanimator/animation% [frame:[starting] (at|on) frame %-number%] for %players%" +
                " [sd:[and] [with] [a[n]] start[ing] transition of %-timespan%] [ed:[and] [with] [a[n]] end[ing] delay of %-timespan%]");
    }

    Expression<ActiveGroup> group;
    Expression<?> animationObj;
    Expression<Number> frame;
    Expression<Player> players;
    Expression<Timespan> startTransition;
    Expression<Timespan> endDelay;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        animationObj = expressions[1];
        if (parseResult.hasTag("frame")){
            frame = (Expression<Number>) expressions[2];
        }
        players = (Expression<Player>) expressions[3];
        if (parseResult.hasTag("sd")) startTransition = (Expression<Timespan>) expressions[4];
        if (parseResult.hasTag("ed")) endDelay = (Expression<Timespan>) expressions[5];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = group.getSingle(event);
        Object o = animationObj.getSingle(event);
        if (g == null || o == null){
            return;
        }
        int frameNum = frame != null ? frame.getSingle(event).intValue() : 0;
        int startTr = startTransition != null ? (int) startTransition.getSingle(event).getAs(Timespan.TimePeriod.TICK) : 0;
        int endDel = endDelay != null ? (int) endDelay.getSingle(event).getAs(Timespan.TimePeriod.TICK) : 0;
        Collection<Player> playerColl = Arrays.stream(players.getAll(event)).toList();
        if (o instanceof DisplayAnimator da){
           da.playCamera(playerColl, g, frameNum, startTr, endDel);
        }
        else if (o instanceof SpawnedDisplayAnimation sa){
            new DisplayAnimator(sa, DisplayAnimator.AnimationType.LINEAR)
                    .playCamera(playerColl, g, frameNum, startTr, endDel);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "play animation camera: "+ animationObj.toString(event, debug);
    }
}
