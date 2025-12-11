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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Stop Animation Camera")
@Description("Stop players from viewing an animation camera")
@Examples({"stop animation camera for {_player}",
            "",
            "stop animation camera for {_players::*}"})
@Since("3.3.6")
public class EffActiveGroupStopAnimationCamera extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupStopAnimationCamera.class,"stop [animation] camera for %players%");
    }

    Expression<Player> players;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        players = (Expression<Player>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (Player p : players.getAll(event)){
            DisplayAnimator.stopCameraView(p);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "stop animation camera: "+players.toString(event, debug);
    }
}
