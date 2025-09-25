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
import org.bukkit.entity.Display;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Set Billboard")
@Description("Set the billboard of an active group/part/selection")
@Examples({"set {_spawnedpart}'s billboard to vertical",
        "set {_spawnedgroup}'s billboard to center",
        "",
        "#3.0.0 and later",
        "set {_packetgroup}'s billboard to horizontal"
        })
@Since("2.6.2, 3.0.0 (Packet Types)")
public class EffActiveBillboard extends Effect {
    static {
        Skript.registerEffect(EffActiveBillboard.class,"[deu] (set|change) %activegroup/activepart/multipartselection%['s] [display] billboard to %billboard%");
    }

    Expression<?> object;
    Expression<Display.Billboard> billboard;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        billboard = (Expression<Display.Billboard>) expressions[1];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Active active = (Active) object.getSingle(event);
        Display.Billboard b = billboard.getSingle(event);
        if (active != null && b != null) {
            active.setBillboard(b);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "billboard: "+object.toString(event, debug);
    }
}
