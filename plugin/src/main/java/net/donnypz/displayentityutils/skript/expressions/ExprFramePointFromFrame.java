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
import net.donnypz.displayentityutils.utils.DisplayEntities.FramePoint;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimationFrame;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Frame Point with tag from Animation Frame")
@Description("Get a Frame Point with a given tag from an Animation Frame")
@Examples({"set {_framepoint} to point with tag \"myframepoint\" from {_animationframe}"})
@Since("3.2.1")
public class ExprFramePointFromFrame extends SimpleExpression<FramePoint> {

    static{
        Skript.registerExpression(ExprFramePointFromFrame.class, FramePoint.class, ExpressionType.COMBINED, "[frame[ |-]]point with tag %string% from %animationframe%");
    }

    private Expression<SpawnedDisplayAnimationFrame> frame;
    private Expression<String> tag;

    @Override
    protected FramePoint @Nullable [] get(Event event) {
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
