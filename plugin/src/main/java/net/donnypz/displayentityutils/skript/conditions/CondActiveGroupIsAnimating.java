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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Is Animating?")
@Description("Check if an active group is animating")
@Examples({"if {_group} is animating:", "\tbroadcast \"It's animating, wow!\""})
@Since("2.6.2")
public class CondActiveGroupIsAnimating extends Condition {

    static {
        Skript.registerCondition(CondActiveGroupIsAnimating.class, "%activegroup% (1¦is|2¦is(n't| not)) animating");
    }

    Expression<ActiveGroup> group;

    @Override
    public boolean check(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isAnimating() == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group animating: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<ActiveGroup>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
