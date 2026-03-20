package net.donnypz.displayentityutils.skript.animation.conditions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("DisplayAnimator is animating ActiveGroup?")
@Description("Check if a displayanimator is animating an active group")
@Examples({"if {_animator} is animating {_group}:", "\tbroadcast \"The animator is animating the group!\""})
@Since("2.6.2")
public class CondActiveGroupAnimatorIsActive extends Condition {

    Expression<ActiveGroup<?>> group;
    Expression<DisplayAnimator> animator;

    public static void register(SyntaxRegistry registry){
        registry.register(SyntaxRegistry.CONDITION,
                SyntaxInfo.builder(CondActiveGroupAnimatorIsActive.class)
                        .addPattern("%displayanimator% (1¦is|2¦is(n't| not)) (animating|[a[n]] active animation) [on|of] %activegroup%")
                        .supplier(CondActiveGroupAnimatorIsActive::new)
                        .build()
        );
    }

    @Override
    public boolean check(Event event) {
        ActiveGroup<?> g = group.getSingle(event);
        DisplayAnimator a = animator.getSingle(event);
        if (g == null || a == null) return isNegated();
        return g.isActiveAnimator(a) != isNegated();
    }



    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "AnimatorIsActive: "+animator.toString(event, debug)+" | Group:"+group.toString(event,debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.animator = (Expression<DisplayAnimator>) expressions[0];
        this.group = (Expression<ActiveGroup<?>>) expressions[1];
        setNegated(parseResult.mark == 2);
        return true;
    }
}
