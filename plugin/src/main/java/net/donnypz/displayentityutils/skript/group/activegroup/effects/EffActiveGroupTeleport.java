package net.donnypz.displayentityutils.skript.group.activegroup.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Teleport Active Group")
@Description("Teleport an Active Group to a location")
@Examples({"deu teleport {_activegroup} to player",
        "deu teleport {_activegroup} to {_location} and respect group direction"})
@Since("2.6.2, 3.0.0 (Packet), 3.3.5 (Plural)")
public class EffActiveGroupTeleport extends Effect {

    Expression<ActiveGroup<?>> group;
    Expression<Location> location;
    boolean respect;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EFFECT,
                SyntaxInfo.builder(EffActiveGroupTeleport.class)
                        .addPattern("deu teleport %activegroups% to %location% [r:[and] (keep|respect) group (facing|direction|orientation)]")
                        .supplier(EffActiveGroupTeleport::new)
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<ActiveGroup<?>>) expressions[0];
        location = (Expression<Location>) expressions[1];
        respect = parseResult.hasTag("r");
        return true;
    }

    @Override
    protected void execute(Event event) {

        ActiveGroup<?>[] groups = group.getArray(event);
        Location loc = location.getSingle(event);
        if (groups == null || loc == null) return;
        for (ActiveGroup<?> g : groups){
            if (g != null){
                g.teleport(loc, respect);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "teleport active group: "+group.toString(event, debug);
    }
}
