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
import net.donnypz.displayentityutils.utils.DisplayEntities.Active;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group/Part/Selection View Range")
@Description("Change the view range of an active group, part, or part selection")
@Examples({"set {_spawnedgroup}'s view range multiplier to 5",
        "make {_spawnedpart}'s view range multiplier 0.5"})
@Since("2.6.2, 3.0.0 (Packet)")
public class EffActiveViewRange extends Effect {
    static {
        Skript.registerEffect(EffActiveViewRange.class,"(make|set) %activegroups/activeparts/multipartfilters%['s] view range multiplier [to] %number%");
    }

    Expression<?> object;
    Expression<Number> range;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        range = (Expression<Number>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object[] objects =  object.getArray(event);
        Number n = range.getSingle(event);
        if (objects == null || n == null) return;
        for (Object o : objects){
            if (o instanceof Active a){
                a.setViewRange(n.floatValue());
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "view range multiplier: "+object.toString(event, debug);
    }
}
