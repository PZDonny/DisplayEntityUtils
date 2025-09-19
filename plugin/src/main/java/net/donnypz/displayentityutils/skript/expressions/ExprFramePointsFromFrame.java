package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Name("Frame Points from Animation Frame")
@Description("Get the Frame Points contained in an animation frame")
@Examples({"set {_framepoints::*} to {_animationframe}'s frame points",
        "set {_framepoints::*} to {_animationframe}'s points"})
@Since("3.2.1")
public class ExprFramePointsFromFrame extends PropertyExpression<SpawnedDisplayAnimationFrame, FramePoint> {

    static {
        register(ExprFramePointsFromFrame.class, FramePoint.class, "animation [frame[ |-]]points", "animationframes");
    }


    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends SpawnedDisplayAnimationFrame>) expressions[0]);
        return true;
    }

    @Override
    public Class<? extends FramePoint> getReturnType() {
        return FramePoint.class;
    }

    @Override
    protected FramePoint[] get(Event event, SpawnedDisplayAnimationFrame[] source) {
        return Arrays.stream(source).flatMap(frame -> frame.getFramePoints().stream()).toArray(FramePoint[]::new);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "animation frame points" + getExpr().toString(event, debug);
    }
}
