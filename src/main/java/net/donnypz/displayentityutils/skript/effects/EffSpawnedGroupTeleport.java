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
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Teleport Spawned Group")
@Description("Teleport a spawned group to a location")
@Examples({"deu teleport {_spawnedgroup} to player", "deu move {_spawnedgroup} to {_location} and respect group facing"})
@Since("2.6.2")
public class EffSpawnedGroupTeleport extends Effect {
    static {
        Skript.registerEffect(EffSpawnedGroupTeleport.class,"[deu ](move|teleport) %spawnedgroup% to %location% [r:[and] (keep|respect) group (facing|direction|orientation)]");
    }

    Expression<SpawnedDisplayEntityGroup> group;
    Expression<Location> location;
    boolean respect;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        group = (Expression<SpawnedDisplayEntityGroup>) expressions[0];
        location = (Expression<Location>) expressions[1];
        respect = parseResult.hasTag("r");
        return true;
    }

    @Override
    protected void execute(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        Location loc = location.getSingle(event);
        if (g == null || loc == null){
            return;
        }
        g.teleport(loc, respect);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "teleport spawned group: "+group.toString(event, debug);
    }
}
