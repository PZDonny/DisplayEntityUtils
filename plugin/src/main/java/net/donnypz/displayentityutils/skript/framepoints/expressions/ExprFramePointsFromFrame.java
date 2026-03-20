package net.donnypz.displayentityutils.skript.framepoints.expressions;

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
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;

@Name("Frame Points from Animation Frame")
@Description("Get the Frame Points of an animation frame")
@Examples({"set {_framepoints::*} to {_animationframe}'s animation frame points"})
@Since("3.2.1, 3.3.2 (Plural)")
public class ExprFramePointsFromFrame extends PropertyExpression<SpawnedDisplayAnimationFrame, FramePoint> {

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprFramePointsFromFrame.class, FramePoint.class)
                        .addPatterns(getPatterns("anim[ation] [frame[ |-]]points", "animationframes"))
                        .supplier(ExprFramePointsFromFrame::new)
                        .build()
        );
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
