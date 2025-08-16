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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Unregister Active Group")
@Description("Unregister an active group, making the group unusable. Packet based groups do not require forced chunk loading, and will always despawn.")
@Examples({"unregister {_spawnedgroup}",
        "unregister {_spawnedgroup} and despawn",
        "unregister {_spawnedgroup} and despawn with forced chunk loading",
        "",
        "#3.0.0 and later",
        "unregister {_packetgroup}"})
@Since("2.6.2")
public class EffActiveGroupUnregister extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupUnregister.class,"[deu] unregister %activegroup% [d:[and ]despawn [f:[with|and] forced [chunk loading]]]");
    }

    Expression<ActiveGroup<?>> group;
    boolean despawn;
    boolean forced;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup<?>>) expressions[0];
        despawn = parseResult.hasTag("d");
        forced = parseResult.hasTag("f");
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup<?> g = group.getSingle(event);
        if (g instanceof SpawnedDisplayEntityGroup sg){
            sg.unregister(despawn, forced);
        }
        else if (g instanceof PacketDisplayEntityGroup pg){
            pg.unregister();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "unregister active group: "+group.toString(event, debug);
    }
}
