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
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Create Display Animator")
@Description("Create a display animator to play animations on an active group")
@Examples({"set {_animator} to a new linear display animator using {_animation}",
        "set {_loopanimator} to a loop animator using {_animation}"})
@Since("2.6.2")
public class ExprDisplayAnimator extends SimpleExpression<DisplayAnimator> {

    static{
        Skript.registerExpression(ExprDisplayAnimator.class, DisplayAnimator.class, ExpressionType.SIMPLE, "[a] [new] (linear|loop:loop[ing]) [display] animator using [anim[ation]] %animation%");
    }

    private boolean loop;
    private Expression<SpawnedDisplayAnimation> animation;

    @Override
    protected DisplayAnimator[] get(Event event) {
        DisplayAnimator.AnimationType type = loop ? DisplayAnimator.AnimationType.LOOP : DisplayAnimator.AnimationType.LINEAR;
        SpawnedDisplayAnimation anim = animation.getSingle(event);
        if (anim == null){
            return null;
        }
        return new DisplayAnimator[]{new DisplayAnimator(anim, type)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends DisplayAnimator> getReturnType() {
        return DisplayAnimator.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (loop ? "loop" : "") + " display animator "+animation.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        loop = parseResult.hasTag("loop");
        animation = (Expression<SpawnedDisplayAnimation>) expressions[0];
        return true;
    }
}
