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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Animate Spawned Group")
@Description("Play an animation on a spawned group with a display animator")
@Examples({"play frame with id 5 on {_spawnedgroup} from {_spawnedanimation}", "start animation on {_spawnedgroup} with {_displayanimator} starting at frame 3"})
@Since("2.6.2")
public class EffSpawnedGroupSetFrame extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupSetFrame.class,"(play|apply|show) frame with id %number% on %spawnedgroup% (with|from) [anim[ation]] %spawnedanimation% [:async[hronously]]");
    }

    Expression<Number> frameID;
    Expression<SpawnedDisplayEntityGroup> group;
    Expression<SpawnedDisplayAnimation> animator;
    boolean async;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        frameID = (Expression<Number>) expressions[0];
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[1];
        animator = (Expression<SpawnedDisplayAnimation>) expressions[2];
        async = parseResult.hasTag("async");
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number n = frameID.getSingle(event);
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        SpawnedDisplayAnimation a = animator.getSingle(event);
        if (n == null || g == null || a == null){
            return;
        }
        g.setToFrame(a, a.getFrames().get(n.intValue()), async);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set to frame: "+animator.toString(event, debug);
    }
}
