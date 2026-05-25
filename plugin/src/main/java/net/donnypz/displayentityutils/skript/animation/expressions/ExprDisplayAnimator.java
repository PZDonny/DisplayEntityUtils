package net.donnypz.displayentityutils.skript.animation.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Create Display Animator")
@Description("Create a display animator to play animations on an active group")
@Examples({
        "set {_animator} to a new linear display animator using {_animation}",
        "set {_loopanimator} to a loop animator using {_animation}",
        "",
        "#3.5.2 and later",
        "#Play an animation and ONLY update transformation, not textures/text/data",
        "set {_animator} to a linear animator using {_animation} without data changes"
})
@Since("2.6.2, 3.5.2 (Data Changes)")
public class ExprDisplayAnimator extends SimpleExpression<DisplayAnimator> {

    private boolean loop;
    private boolean dataChanges;
    private Expression<SpawnedDisplayAnimation> animation;

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprDisplayAnimator.class, DisplayAnimator.class)
                        .addPatterns("[a] [new] (linear|loop:loop[ing]) [display] animator using [anim[ation]] %deuanimation% [d:without (data|texture) change[s]]")
                        .supplier(ExprDisplayAnimator::new)
                        .build()
        );
    }

    @Override
    protected DisplayAnimator[] get(Event event) {
        DisplayAnimator.AnimationType type = loop ? DisplayAnimator.AnimationType.LOOP : DisplayAnimator.AnimationType.LINEAR;
        SpawnedDisplayAnimation anim = animation.getSingle(event);
        if (anim == null) {
            return null;
        }
        return new DisplayAnimator[]{new DisplayAnimator(anim, type, dataChanges)};
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
        return (loop ? "loop" : "") + " display animator " + animation.toString(event, debug) + (!dataChanges ? "w/o data changes" : "");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        loop = parseResult.hasTag("loop");
        dataChanges = !parseResult.hasTag("d");
        animation = (Expression<SpawnedDisplayAnimation>) expressions[0];
        return true;
    }
}
