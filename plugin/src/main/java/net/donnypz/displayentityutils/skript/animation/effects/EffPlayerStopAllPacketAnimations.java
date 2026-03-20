package net.donnypz.displayentityutils.skript.animation.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Stop Animations for Player")
@Description("Stop all packet animations playing on an active group for a player")
@Examples({"stop packet animations from {_displayanimator} for {_player::*}"})
@Since("3.0.0")
public class EffPlayerStopAllPacketAnimations extends Effect {

    Expression<DisplayAnimator> animator;
    Expression<Player> players;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffPlayerStopAllPacketAnimations.class)
                        .addPattern("stop [all] packet animations from %displayanimator% for %players%")
                        .supplier(EffPlayerStopAllPacketAnimations::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        animator = (Expression<DisplayAnimator>) expressions[0];
        players = (Expression<Player>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        DisplayAnimator a = animator.getSingle(event);
        Player[] plrs = players.getAll(event);
        if (a == null || plrs == null){
            return;
        }
        for (Player p : plrs){
            a.stop(p);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "stop all packet animations for: "+players.toString(event, debug);
    }
}
