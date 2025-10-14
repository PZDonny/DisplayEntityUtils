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
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Packet Group Is Persistent")
@Description("Check if a packet group is persistent.")
@Examples({"if {_packetgroup} is persistent:", "\tbroadcast \"The group will exist after server restarts!\""})
@Since("3.3.4")
public class CondPacketGroupIsPersistent extends Condition {

    static {
        Skript.registerCondition(CondPacketGroupIsPersistent.class, "%packetgroup% (1¦is|2¦is(n't| not)) persistent");
    }

    Expression<PacketDisplayEntityGroup> group;

    @Override
    public boolean check(Event event) {
        PacketDisplayEntityGroup g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isPersistent() == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Packet Group persistent: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<PacketDisplayEntityGroup>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
