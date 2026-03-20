package net.donnypz.displayentityutils.skript.active.effects;

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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Set Direction (Pitch, Yaw, Pivot)")
@Description("Set the pitch and yaw of a spawned group/part/selection. Optionally pivot interactions")
@Examples({"deu set {_activegroup}'s yaw with interaction pivot to 35",
        "deu set {_activepart}'s pitch to -90"})
@Since("2.6.2, 3.0.0 (Packet)")
public class EffActiveDirection extends Effect {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveDirection.class)
                        .addPattern("deu set %activegroups/activeparts/multipartfilters%['s] (1¦yaw [p:with [interaction ]pivot]|2¦pitch) to %number%")
                        .supplier(EffActiveDirection::new)
                        .build()
        );
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
