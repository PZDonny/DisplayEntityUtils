package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.skript.doc.Name;
import net.donnypz.displayentityutils.events.GroupSpawnedEvent;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Saved Group to Spawned Group")
@Description("Spawn a saved group at a location")
@Examples({"set {_spawnedgroup} to {_savedgroup} spawned at {_location}",
"set {_spawnedgroup} to {_savedgroup} spawned at {_location} with {_groupspawnsettings}"})
@Since("2.6.2")
public class ExprSavedGroupToSpawned extends SimpleExpression<SpawnedDisplayEntityGroup> {

    static{
        Skript.registerExpression(ExprSavedGroupToSpawned.class, SpawnedDisplayEntityGroup.class, ExpressionType.COMBINED, "%savedgroup% spawned at %location% [w:with %-groupspawnsetting%]");
    }

    Expression<DisplayEntityGroup> savedGroup;
    Expression<Location> location;
    Expression<GroupSpawnSettings> groupSpawnSettings;

    @Override
    protected SpawnedDisplayEntityGroup @Nullable [] get(Event event) {
        DisplayEntityGroup saved = savedGroup.getSingle(event);
        Location loc = location.getSingle(event);
        if (saved == null || loc == null){
            return null;
        }
        if (groupSpawnSettings == null){
            return new SpawnedDisplayEntityGroup[]{saved.spawn(loc, GroupSpawnedEvent.SpawnReason.SKRIPT)};
        }
        else{
            GroupSpawnSettings settings = groupSpawnSettings.getSingle(event);
            if (settings == null){
                return null;
            }
            return new SpawnedDisplayEntityGroup[]{saved.spawn(loc, GroupSpawnedEvent.SpawnReason.SKRIPT, settings)};
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends SpawnedDisplayEntityGroup> getReturnType() {
        return SpawnedDisplayEntityGroup.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return savedGroup.toString(event,debug)+" to spawned group";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        savedGroup = (Expression<DisplayEntityGroup>) expressions[0];
        location = (Expression<Location>) expressions[1];
        if (parseResult.hasTag("w")){
            groupSpawnSettings = (Expression<GroupSpawnSettings>) expressions[2];
        }
        return true;
    }
}
