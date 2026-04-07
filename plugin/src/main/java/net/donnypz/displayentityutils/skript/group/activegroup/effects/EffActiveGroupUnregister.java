package net.donnypz.displayentityutils.skript.group.activegroup.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.PacketDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Unregister Active Group")
@Description("Unregister an active group, making the group unusable. Packet based groups do not require forced chunk loading, and will always despawn.")
@Examples({
        "deu unregister {_activegroup}",
        "deu unregister {_activegroup} and despawn",
        "deu unregister {_activegroup} and despawn with forced chunk loading",
        "",
})
@Since("2.6.2, 3.0.0 (Packet)")
public class EffActiveGroupUnregister extends Effect {

    Expression<ActiveGroup<?>> group;
    boolean despawn;
    boolean forced;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupUnregister.class)
                        .addPattern("[deu] unregister %activegroup% [d:[and ]despawn [f:[with|and] forced [chunk loading]]]")
                        .supplier(EffActiveGroupUnregister::new)
                        .build()
        );
    }


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
            if (pg.isPersistent()){
                DisplayGroupManager.removePersistentPacketGroup(pg, true);
            }
            else{
                pg.unregister();
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "unregister active group: "+group.toString(event, debug);
    }
}
