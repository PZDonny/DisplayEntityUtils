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
            "play camera on {_activegroup} using {_animation} for {_player} starting at frame 3"})
@Since("3.3.6")
public class EffActiveGroupPlayAnimationCamera extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupPlayAnimationCamera.class,"(start|play) [anim[ation]] camera on %activegroup% (using|with) %displayanimator/animation% [frame:[starting] (at|on) frame %-number%] for %players%");
    }

    Expression<ActiveGroup> group;
    Expression<?> animator;
    Expression<Number> frame;
    Expression<Player> players;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        animator = expressions[1];
        if (parseResult.hasTag("frame")){
            frame = (Expression<Number>) expressions[2];
        }
        players = (Expression<Player>) expressions[3];
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = group.getSingle(event);
        Object o = animator.getSingle(event);
        if (g == null || o == null){
            return;
        }
        int frameNum = frame != null ? frame.getSingle(event).intValue() : 0;
        Collection<Player> playerColl = Arrays.stream(players.getAll(event)).toList();
        if (o instanceof DisplayAnimator da){
           da.playCamera(playerColl, g, frameNum);
        }
        else if (o instanceof SpawnedDisplayAnimation sa){
            new DisplayAnimator(sa, DisplayAnimator.AnimationType.LINEAR)
                    .playCamera(playerColl, g, frameNum);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "play animation camera: "+animator.toString(event, debug);
    }
}
