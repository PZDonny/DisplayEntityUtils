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
import net.donnypz.displayentityutils.utils.DisplayEntities.Spawned;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Set Direction (Pitch, Yaw, Pivot)")
@Description("Set the pitch and yaw of a spawned group/part/selection. Optionally pivot interactions")
@Examples({"set {_spawnedgroup}'s yaw to 35 with interaction pivot", "set {_spawnedpart}'s pitch to -90"})
@Since("2.6.2")
public class EffSpawnedDirection extends Effect {
    static {
        Skript.registerEffect(EffSpawnedDirection.class,"[deu ](make|set) %spawnedgroup/spawnedpart/partselection%['s] (1¦yaw [p:with [interaction ]pivot]|2¦pitch) [to|as] %number%");
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

        Spawned[] objects = (Spawned[]) object.getArray(event);
        if (yaw){
            for (Spawned s : objects){
                if (s == null) continue;
                s.setYaw(n.floatValue(), pivot);
            }
        }
        else{
            for (Spawned s : objects){
                if (s == null) continue;
                s.setPitch(n.floatValue());
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set direction: "+object.toString(event, debug);
    }
}
