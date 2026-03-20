package net.donnypz.displayentityutils.skript.framepoints.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Frame Point from Animation Frame")
@Description("Get a Frame Point, of a given tag, from an Animation Frame")
@Examples({"set {_framepoint} to point with tag \"myframepoint\" from {_animationframe}"})
@Since("3.2.1")
public class ExprFramePointFromFrame extends SimpleExpression<FramePoint> {

    private Expression<SpawnedDisplayAnimationFrame> frame;
    private Expression<String> tag;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprFramePointFromFrame.class, FramePoint.class)
                        .addPattern("[frame[ |-]]point with tag %string% from %animationframe%")
                        .supplier(ExprFramePointFromFrame::new)
                        .build()
        );
    }

    @Override
    protected FramePoint[] get(Event event) {
        SpawnedDisplayAnimationFrame f = frame.getSingle(event);
        if (f == null) return new FramePoint[0];
        String t = tag.getSingle(event);
        if (t == null) return new FramePoint[0];
        return new FramePoint[]{f.getFramePoint(t)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends FramePoint> getReturnType() {
        return FramePoint.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "frame point with tag " + tag.toString(event,debug)+" from " + frame.toString(event,debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        tag = (Expression<String>) expressions[0];
        frame = (Expression<SpawnedDisplayAnimationFrame>) expressions[1];
        return true;
    }
}
