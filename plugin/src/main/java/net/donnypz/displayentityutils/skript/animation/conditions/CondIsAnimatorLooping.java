package net.donnypz.displayentityutils.skript.animation.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Is Display Animator Looping")
@Description("Check if an animator is looping")
@Examples({"if {_displayanimator}'s animation type is looping:",
        "\tbroadcast \"The animator's type is looping!\""})
@Since("3.5.0")
public class CondIsAnimatorLooping extends Condition {


    Expression<DisplayAnimator> animator;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondIsAnimatorLooping.class)
                        .addPattern("%displayanimator%['s] anim[ation] type (1¦is|2¦is(n't| not)) looping")
                        .supplier(CondIsAnimatorLooping::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        DisplayAnimator a = animator.getSingle(event);
        if (a == null) return isNegated();
        return a.getAnimationType() == DisplayAnimator.AnimationType.LOOP != isNegated();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Is display animator looping: "+ animator.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.animator = (Expression<DisplayAnimator>) expressions[0];
        setNegated(parseResult.mark == 2);
        return true;
    }
}