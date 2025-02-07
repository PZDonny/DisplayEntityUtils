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
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Animate Spawned Group")
@Description("Play an animation on a spawned group with a display animator")
@Examples({"play animation on {_spawnedgroup} using {_displayanimator}", "start animation on {_spawnedgroup} with {_displayanimator} starting at frame 3"})
@Since("2.6.2")
public class EffSpawnedGroupPlayAnimation extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupPlayAnimation.class,"(start|play) anim[ation] on %spawnedgroup% (using|with) %displayanimator% [frame:[starting ](at|on) frame %-number%]");
    }

    Expression<SpawnedDisplayEntityGroup> group;
    Expression<DisplayAnimator> animator;
    Expression<Number> frame;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        animator = (Expression<DisplayAnimator>) expressions[1];
        if (parseResult.hasTag("frame")){
            frame = (Expression<Number>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        DisplayAnimator a = animator.getSingle(event);
        if (g == null || a == null){
            return;
        }
        if (frame != null){
            Number n = frame.getSingle(event);
            if (n == null){
                return;
            }
            a.play(g, n.intValue());
        }
        else{
            a.play(g);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "play animation: "+animator.toString(event, debug);
    }
}
