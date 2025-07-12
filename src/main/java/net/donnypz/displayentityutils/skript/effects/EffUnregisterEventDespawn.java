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
import net.donnypz.displayentityutils.events.GroupUnregisteredEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Override Unregister Event Despawn")
@Description("Override the possible despawning of spawned parts when a spawned group is unregistered")
@Examples({"on display group unregistered:", "\tdisallow group despawn"})
@Since("2.6.2")
public class EffUnregisterEventDespawn extends Effect {
    static {
        Skript.registerEffect(EffUnregisterEventDespawn.class,"[:dis]allow [group] despawn");
    }

    boolean despawn;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        despawn = !parseResult.hasTag("dis");
        return true;
    }

    @Override
    protected void execute(Event event) {
        if (event instanceof GroupUnregisteredEvent e){
            e.setDespawn(despawn);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "group unregister event despawn: "+despawn;
    }
}
