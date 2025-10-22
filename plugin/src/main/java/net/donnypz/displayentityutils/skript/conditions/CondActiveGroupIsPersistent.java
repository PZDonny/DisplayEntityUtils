package net.donnypz.displayentityutils.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Is Persistent")
@Description("Check if a spawned group is persistent")
@Examples({"if {_spawnedgroup} is persistent:", "\tbroadcast \"The group wont despawn!\"",
        "",
        "if {_packetgroup} is persistent:", "\tbroadcast \"The group will persist!\""})
@Since("2.6.2, 3.3.4 (Packet-Group)")
@DocumentationId("CondSpawnedGroupIsPersistent")
public class CondActiveGroupIsPersistent extends Condition {

    static {
        Skript.registerCondition(CondActiveGroupIsPersistent.class, "%activegroup% (1¦is|2¦is(n't| not)) persistent");
    }

    Expression<ActiveGroup<?>> group;

    @Override
    public boolean check(Event event) {
        ActiveGroup<?> g = group.getSingle(event);
        if (g == null) return isNegated();
        return g.isPersistent() != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Active Group persistent: "+group.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.group = (Expression<ActiveGroup<?>>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
