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
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Stop All Animations")
@Description("Stop all animations playing on an active group")
@Examples({"stop all animations on {_spawnedgroup}",
        "stop animations on {_spawnedgroup} and remove from state machine",
        "",
        "#3.0.0 and later",
        "stop all animations on {_packetgroup}"})
@Since("2.6.2, 3.0.0 (Packet)")
public class EffActiveGroupStopAllAnimations extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupStopAllAnimations.class,"stop [all] animations on %activegroup% [r:[and ]remove from [state] machine]");
    }

    Expression<ActiveGroup> group;
    boolean remove;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup>) expressions[0];
        remove = parseResult.hasTag("r");
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null){
            return;
        }
        g.stopAnimations(remove);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "stop all animations: "+group.toString(event, debug);
    }
}
