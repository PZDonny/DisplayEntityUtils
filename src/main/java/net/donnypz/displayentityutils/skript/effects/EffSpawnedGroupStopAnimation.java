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
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Stop Animation")
@Description("Stop an animation playing on a spawned group")
@Examples({"stop animation on {_spawnedgroup} from {_displayanimator}"})
@Since("2.6.2")
public class EffSpawnedGroupStopAnimation extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupStopAnimation.class,"stop animation on %spawnedgroup% from %displayanimator%");
    }

    Expression<SpawnedDisplayEntityGroup> group;
    Expression<DisplayAnimator> animator;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        animator = (Expression<DisplayAnimator>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        DisplayAnimator a = animator.getSingle(event);
        if (g == null || a == null){
            return;
        }
        g.stopAnimation(a);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "stop animator animation: "+group.toString(event, debug);
    }
}
