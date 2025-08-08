package net.donnypz.displayentityutils.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Frame Points from Animation Frame")
@Description("Get the Frame Points contained in an animation frame")
@Examples({"set {_framepoints::*} to {_spawnedanimationframe}'s frame points",
        "set {_framepoints::*} to {_spawnedanimationframe}'s points"})
@Since("3.2.1")
public class ExprFramePointsFromFrame extends SimpleExpression<FramePoint> {

    static {
        String property = "[the] [frame[ |-]]points";
        String fromType = "spawnedanimationframe";
        Skript.registerExpression(ExprFramePointsFromFrame.class, FramePoint.class, ExpressionType.PROPERTY, PropertyExpression.getPatterns(property, fromType));
    }

    Expression<SpawnedDisplayAnimationFrame> frame;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        frame = (Expression<SpawnedDisplayAnimationFrame>) expressions[0];
        return true;
    }

    @Override
    public Class<? extends FramePoint> getReturnType() {
        return FramePoint.class;
    }


    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    protected FramePoint @Nullable [] get(Event event) {
        SpawnedDisplayAnimationFrame f = frame.getSingle(event);
        if (f == null) return null;
        return f.getFramePoints().toArray(FramePoint[]::new);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "framepoints from frame" + frame.toString(event, debug);
    }
}
