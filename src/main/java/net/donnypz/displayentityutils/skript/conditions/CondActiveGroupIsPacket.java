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
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group is Packet Based?")
@Description("Check if an active group is a Packet Group")
@Examples({"if {_group} is packet based:", "\tbroadcast\"This group is packet based!\""})
@Since("2.8.0")
public class CondActiveGroupIsPacket extends Condition {

    static {
        Skript.registerCondition(CondActiveGroupIsPacket.class, "%activegroup% (1¦is|2¦is(n't| not)) packet( |-)?based");
    }

    Expression<ActiveGroup> group;

    @Override
    public boolean check(Event event) {
        ActiveGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g instanceof PacketDisplayEntityGroup == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group is packet-based?: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<ActiveGroup>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
