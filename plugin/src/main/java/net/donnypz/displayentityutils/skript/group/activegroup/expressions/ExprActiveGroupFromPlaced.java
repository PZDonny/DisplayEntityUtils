package net.donnypz.displayentityutils.skript.group.activegroup.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.managers.PlaceableGroupManager;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Active Group From Placed Block")
@Description("Get the active group manually placed down at a block by a player. " +
        "A player must have been holding a placeable group block item for the group " +
        "at the specified block location to return the appropriate group.")
@Examples({"set {_activegroup} to player placed group at {_location}"})
@Since("3.5.0")
public class ExprActiveGroupFromPlaced extends SimpleExpression<ActiveGroup> {

    Expression<Location> locExpr;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprActiveGroupFromPlaced.class, ActiveGroup.class)
                        .addPatterns("[player] placed [active] group at %location%")
                        .supplier(ExprActiveGroupFromPlaced::new)
                        .build()
        );
    }

    @Override
    public Class<? extends ActiveGroup> getReturnType() {
        return ActiveGroup.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    protected ActiveGroup[] get(Event event) {
        Location loc = locExpr == null ? null : locExpr.getSingle(event);
        if (loc == null) return null;
        ActiveGroup g = PlaceableGroupManager.getPlacedGroup(loc.getBlock());
        return g == null ? null : new ActiveGroup[]{g};
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "active group from player placed block";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        locExpr = (Expression<Location>) expressions[0];
        return true;
    }
}
