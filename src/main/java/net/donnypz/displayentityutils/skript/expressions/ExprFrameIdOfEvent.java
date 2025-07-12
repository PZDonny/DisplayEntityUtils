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
import net.donnypz.displayentityutils.events.AnimationFrameEndEvent;
import net.donnypz.displayentityutils.events.AnimationFrameStartEvent;
import net.donnypz.displayentityutils.events.PacketAnimationFrameEndEvent;
import net.donnypz.displayentityutils.events.PacketAnimationFrameStartEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Frame Id of Event")
@Description("Get the frame id of a Group Animate Frame End, Group Animate Frame End, or their packet based counterparts. Returns -1 if not used for the correct event")
@Examples({"set {_frameId} to the frame id"})
@Since("3.0.0")
public class ExprFrameIdOfEvent extends SimpleExpression<Number> {

    static{
        Skript.registerExpression(ExprFrameIdOfEvent.class, Number.class, ExpressionType.SIMPLE, "[the] [deu] frame[ |-]id");
    }

    @Override
    protected Number @Nullable [] get(Event event) {
        if (event instanceof AnimationFrameEndEvent e){
            return new Number[]{e.getFrameId()};
        }
        if (event instanceof PacketAnimationFrameEndEvent e){
            return new Number[]{e.getFrameId()};
        }
        if (event instanceof AnimationFrameStartEvent e){
            return new Number[]{e.getFrameId()};
        }
        if (event instanceof PacketAnimationFrameStartEvent e){
            return new Number[]{e.getFrameId()};
        }
        Skript.error("You can get the frame id in frame start / frame end events");
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "get event's frame id";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
