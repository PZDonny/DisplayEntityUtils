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
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Object is Packet Based?")
@Description("Check if an active object is a Packet Group")
@Examples({"if {_group} is packet based:",
        "\tbroadcast\"This group is packet based!\"",
        "",
        "if {_selection} is not packet based:",
        "\tbroadcast\"This selection is not packet based!\""})
@Since("3.0.0")
public class CondActiveIsPacketBased extends Condition {

    static {
        Skript.registerCondition(CondActiveIsPacketBased.class, "%activegroup/activepartselection/activepart% (1¦is|2¦is(n't| not)) packet [based]");
    }

    Expression<Active> active;

    @Override
    public boolean check(Event event) {
        Active g = active.getSingle(event);
        if (g == null) return isNegated();
        return g instanceof PacketDisplayEntityGroup == isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Group is packet-based?: "+active.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.active = (Expression<Active>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
