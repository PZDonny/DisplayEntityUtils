package net.donnypz.displayentityutils.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group Persistence")
@Description("Change the persistence state of a spawned group")
@Examples({"set {_spawnedgroup} to not persistent",
            "make {_packetgroup} to persistent"})
@Since("2.6.3, 3.3.4 (Packet)")
@DocumentationId("EffSpawnedGroupPersistence")
public class EffActiveGroupPersistence extends Effect {
    static {
        Skript.registerEffect(EffActiveGroupPersistence.class,"(make|set) %activegroups% [to] [:not] persistent");
    }

    Expression<ActiveGroup<?>> object;
    boolean persistent;

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
