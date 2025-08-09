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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Frame Point Relative Location")
@Description("Get the location of a Frame Point relative to a location or Active Group. " +
        "The provided location is more accurate when used on an Active Group since group scale is taken into account.")
@Examples({"set {_loc} to {_framepoint}'s location relative to {_activegroup}",
            "",
            "set {_loc} to {_framepoint}'s location relative to {_location}"})
@Since("3.2.1")
public class ExprFramePointRelativeLocation extends SimpleExpression<Location> {

    static{
        Skript.registerExpression(ExprFramePointRelativeLocation.class, Location.class, ExpressionType.SIMPLE, "%framepoint% location relative [to|of] %activegroup/location%");
    }

    Expression<FramePoint> fp;
    Expression<Object> obj;

    @Override
    protected Location @Nullable [] get(Event event) {
        FramePoint framePoint = fp.getSingle(event);
        Object o = obj.getSingle(event);
        if (framePoint == null || o == null){
            return null;
        }
        Location fromLoc;
        if (o instanceof ActiveGroup<?> g){
            fromLoc = g.getLocation();
        }
        else if (o instanceof Location l){
            fromLoc = l;
        }
        else{
            return null;
        }
        return new Location[]{framePoint.getLocation(fromLoc)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "location relative to: "+fp.toString(event, debug)+" | "+obj.toString();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        fp = (Expression<FramePoint>) expressions[0];
        obj = (Expression<Object>) expressions[1];
        return true;
    }
}
