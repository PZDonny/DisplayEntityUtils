package net.donnypz.displayentityutils.skript.group.activegroup.effects;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;


@Name("Active Group Persistence")
@Description({"Change the persistence state of a active group.",
    "Persisting a packet-based group causes it to be saved in chunk data and it cannot be teleported nor ride entities.",
    "Any changes made to a persistent packet-based group must be applied using \"\""})
@Examples({
        "deu make {_activegroup} persist",
        "deu make {_activegroup} not persist",
        "",
        "#3.4.3 and earlier",
        "set {_activegroup} to persistent"
})
@Since("2.6.3, 3.3.4 (Packet)")
public class EffActiveGroupPersistence extends Effect {

    Expression<ActiveGroup<?>> object;
    boolean persistent;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupPersistence.class)
                        .addPattern("deu (force|make) %activegroups% [to] [:not] persist[ent]")
                        .supplier(EffActiveGroupPersistence::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = (Expression<ActiveGroup<?>>) expressions[0];
        persistent = !parseResult.hasTag("not");
        return true;
    }

    @Override
    protected void execute(Event event) {
        ActiveGroup<?>[] groups = object.getArray(event);
        if (groups == null) return;
        for (ActiveGroup<?> g : groups){
            if (g != null){
                g.setPersistent(persistent);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "active group persistence: "+object.toString(event, debug);
    }
}
