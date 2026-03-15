package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Name("Spawned Group Near Location")
@Description("Get, and optionally register, the nearest or all nearby groups within a location")
@Examples({"set {_nearbygroups::*} to all spawned groups within 5 blocks of {_location}",
            "set {_nearbygroups::*} to all registered spawned groups within 8 blocks of {_location}",
            "",
            "set {_nearest} to nearest spawned group within 2 blocks of {_location}",
            "set {_nearest} to nearest registered spawned group within 6 blocks of {_location}"})
@Since("2.6.2, 3.4.3 (Registered)")
public class ExprSpawnedGroupNearLocation extends SimpleExpression<SpawnedDisplayEntityGroup> {

    static{
        Skript.registerExpression(ExprSpawnedGroupNearLocation.class, SpawnedDisplayEntityGroup.class, ExpressionType.SIMPLE, "(1¦all|2¦nearest) [r:registered] spawned[ |-]group[s] within %number% [block[s]] of %location%");
    }

    private Expression<Number> range;
    private Expression<Location> location;
    private boolean isAll;
    private boolean registered;

    @Override
    protected SpawnedDisplayEntityGroup[] get(Event event) {
        Number n = range.getSingle(event);
        if (n == null) return new SpawnedDisplayEntityGroup[0];
        Location loc = location.getSingle(event);
        if (loc == null) return new SpawnedDisplayEntityGroup[0];
        double range = n.doubleValue();
        if (isAll) {
            SpawnedDisplayEntityGroup[] arr;
            if (registered){
                Set<SpawnedDisplayEntityGroup> results = DisplayGroupManager.getNearbySpawnedGroups(loc, range);
                arr = new SpawnedDisplayEntityGroup[results.size()];
                int i = 0;
                for (SpawnedDisplayEntityGroup group : results){
                    arr[i] = group;
                    i++;
                }
            }
            else{
                Set<GroupResult> results = DisplayGroupManager.getOrCreateNearbySpawnedGroups(loc, range);
                arr = new SpawnedDisplayEntityGroup[results.size()];
                int i = 0;
                for (GroupResult r : results){
                    arr[i] = r.group();
                    i++;
                }
            }
            return arr;
        }
        else { //nearest
            if (registered){
                SpawnedDisplayEntityGroup group = DisplayGroupManager.getNearestSpawnedGroup(loc, range);
                return group == null ? null : new SpawnedDisplayEntityGroup[]{group};
            }
            else{
                GroupResult result = DisplayGroupManager.getOrCreateNearestSpawnedGroup(loc, range);
                if (result == null){
                    return null;
                }
                return new SpawnedDisplayEntityGroup[]{result.group()};
            }
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
        return (isAll ? "all" : "nearest") + (registered ? " existing" : "")+ " spawned group with " + range.toString(event, debug) + " of " + location.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        range = (Expression<Number>) expressions[0];
        location = (Expression<Location>) expressions[1];
        isAll = parseResult.mark == 1;
        registered = parseResult.hasTag("e");
        return true;
    }
}
