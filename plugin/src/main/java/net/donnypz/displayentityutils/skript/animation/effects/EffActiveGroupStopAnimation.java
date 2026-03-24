package net.donnypz.displayentityutils.skript.animation.effects;

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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;
import java.util.Collection;

@Name("Stop Animation")
@Description("Stop an animation playing on an active group")
@Examples({"stop animation on {_spawnedgroup} from {_displayanimator}",
            "",
            "#3.0.0 and later",
            "stop packet animation on {_packetgroup} from {_displayanimator} for {_players::*}",
            "",
            "#3.3.6 and later",
            "stop animation on {_activegroup} from {_displayanimator} for {_players::*} and stop camera"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.6 (Animation Camera)")
public class EffActiveGroupStopAnimation extends Effect {

    Expression<ActiveGroup> group;
    Expression<DisplayAnimator> animator;
    Expression<Player> players;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupStopAnimation.class)
                        .addPattern("stop [packet] animation on %activegroup% from %displayanimator% [f:for %-players%] [cam:and (stop|end) cam[era]]")
                        .supplier(EffActiveGroupStopAnimation::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        animator = (Expression<DisplayAnimator>) expressions[1];
        if (parseResult.hasTag("f")){
            players = (Expression<Player>) expressions[2];
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = group.getSingle(event);
        DisplayAnimator a = animator.getSingle(event);
        if (g == null || a == null){
            return;
        }
        if (players == null){
            g.stopAnimation(a);
        }
        else{
            Collection<Player> plrs = Arrays.stream(players.getAll(event)).toList();
            a.stop(plrs, g);
        }

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "stop animator animation: "+group.toString(event, debug);
    }
}
