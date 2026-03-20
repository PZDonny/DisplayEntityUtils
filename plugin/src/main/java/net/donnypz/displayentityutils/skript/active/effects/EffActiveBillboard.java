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
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Set Billboard")
@Description("Set the billboard of an active group/part/filter")
@Examples({
        "deu set {_activepart}'s billboard to vertical",
        "deu set {_activegroup}'s billboard to center",
        "",
        "#3.4.3 and earlier",
        "set {_activepart}'s billboard to vertical",
        "set {_activegroup}'s billboard to center",
        })
@Since("2.6.2, 3.0.0 (Packet Types), 3.5.0 (Plural)")
public class EffActiveBillboard extends Effect {

    Expression<?> object;
    Expression<Display.Billboard> billboard;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveBillboard.class)
                        .addPattern("deu set %activegroups/activeparts/multipartfilters%['s] billboard to %billboard%")
                        .supplier(EffActiveBillboard::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        billboard = (Expression<Display.Billboard>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Object[] objects = object.getArray(event);

        Display.Billboard b = billboard.getSingle(event);
        if (objects != null && b != null) {
            for (Object o : objects){
                if (!(o instanceof Active active)) continue;
                active.setBillboard(b);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "set billboard: "+object.toString(event, debug);
    }
}
