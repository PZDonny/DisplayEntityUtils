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
import net.donnypz.displayentityutils.managers.DisplayGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import net.donnypz.displayentityutils.utils.GroupResult;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Spawned Group Near Location")
@Description("Get the nearest or all nearby groups within a location")
@Examples({"set {_nearbygroups::*} to all spawned groups within 5 blocks of {_location}",
            "",
            "set {_nearest} to nearest spawned group within 2 blocks of {_location}"})
@Since("2.6.2")
public class ExprSpawnedGroupNearLocation extends SimpleExpression<SpawnedDisplayEntityGroup> {

    static{
        Skript.registerExpression(ExprSpawnedGroupNearLocation.class, SpawnedDisplayEntityGroup.class, ExpressionType.SIMPLE, "(1¦all|2¦nearest) spawned[ |-]group[s] within %number% [block[s]] of %location%");
    }

    Expression<Number> range;
    Expression<Location> location;
    boolean isAll;

    @Override
    protected SpawnedDisplayEntityGroup @Nullable [] get(Event event) {
        Number n = range.getSingle(event);
        Location loc = location.getSingle(event);
        if (n == null || loc == null){
            return null;
        }
        if (isAll){
            List<GroupResult> results = DisplayGroupManager.getSpawnedGroupsNearLocation(loc, n.doubleValue());
            SpawnedDisplayEntityGroup[] arr = new SpawnedDisplayEntityGroup[results.size()];
            for (int i = 0; i < results.size(); i++){
                arr[i] = results.get(i).group();
            }
            return arr;
        }
        else{
            GroupResult result = DisplayGroupManager.getSpawnedGroupNearLocation(loc, n.doubleValue());
            if (result == null){
                return null;
            }
            return new SpawnedDisplayEntityGroup[]{result.group()};
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
        return "group(s) near location: "+location.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        range = (Expression<Number>) expressions[0];
        location = (Expression<Location>) expressions[1];
        isAll = parseResult.mark == 1;
        return true;
    }
}
