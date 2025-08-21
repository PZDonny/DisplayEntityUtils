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

@Name("Set Direction (Pitch, Yaw, Pivot)")
@Description("Set the pitch and yaw of a spawned group/part/selection. Optionally pivot interactions")
@Examples({"deu set {_spawnedgroup}'s yaw with interaction pivot to 35",
        "deu set {_spawnedpart}'s pitch to -90"})
@Since("2.6.2")
public class EffActiveDirection extends Effect {
    static {
        Skript.registerEffect(EffActiveDirection.class,"[deu ](make|set) %activegroups/activeparts/multipartselections%['s] (1¦yaw [p:with [interaction ]pivot]|2¦pitch) [to|as] %-number%");
    }

    Expression<Object> object;
    Expression<Number> angle;
    boolean yaw;
    boolean pivot;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = (Expression<Object>) expressions[0];
        angle = (Expression<Number>) expressions[1];
        yaw = parseResult.mark == 1;
        if (yaw){
            pivot = parseResult.hasTag("p");
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        Number n = angle.getSingle(event);
        if (n == null) return;

        float value = n.floatValue();
        Object[] objects = object.getArray(event);
        for (Object  o : objects){
            if (!(o instanceof Active a)) continue;
            if (yaw){
                a.setYaw(value, pivot);
            }
            else{
                a.setPitch(value);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set direction: "+object.toString(event, debug);
    }
}
