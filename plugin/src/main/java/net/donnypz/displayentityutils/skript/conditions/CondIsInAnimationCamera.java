package net.donnypz.displayentityutils.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.managers.DEUUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Is Player In Animation Camera?")
@Description("Check if a player is in an animation camera")
@Examples({"if {_player} is in an animation camera:", "\tbroadcast \"The player is viewing an animation from its camera!\""})
@Since("3.3.6")
public class CondIsInAnimationCamera extends Condition {

    static {
        Skript.registerCondition(CondIsInAnimationCamera.class, "%player% (1¦is|2¦is(n't| not)) in [a[n]] animation camera");
    }

    Expression<Player> player;

    @Override
    public boolean check(Event event) {
        Player p = player.getSingle(event);
        if (p == null) return isNegated();
        DEUUser user = DEUUser.getUser(p);
        if (user == null) return isNegated();
        return user.isInAnimationCamera() != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Is in animation camera: "+ player.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.player = (Expression<Player>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}