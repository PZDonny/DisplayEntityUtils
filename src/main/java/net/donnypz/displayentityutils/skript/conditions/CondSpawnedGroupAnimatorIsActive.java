package net.donnypz.displayentityutils.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimator;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("DisplayAnimator is animating SpawnedGroup?")
@Description("Check if a displayanimator is animating a spawnedgroup")
@Examples({"if {_animator} is {_group} is animating {_group}:", "\tbroadcast\"The animator is animating the group!\""})
@Since("2.6.2")
public class CondSpawnedGroupAnimatorIsActive extends Condition {

    static {
        Skript.registerCondition(CondSpawnedGroupAnimatorIsActive.class, "%displayanimator% (1¦is|2¦is(n't| not)) (animating|[a[n]] active animation) [on|of] %spawnedgroup%");
    }

    Expression<SpawnedDisplayEntityGroup> group;
    Expression<DisplayAnimator> animator;

    @Override
    public boolean check(Event event) {
        SpawnedDisplayEntityGroup g = group.getSingle(event);
        DisplayAnimator a = animator.getSingle(event);
        if (g == null || a == null) return isNegated();
        return g.isActiveAnimator(a) == isNegated();
    }



    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "AnimatorIsActive: "+animator.toString(event, debug)+" | Group:"+group.toString(event,debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.animator = (Expression<DisplayAnimator>) expressions[0];
        this.group = (Expression<SpawnedDisplayEntityGroup>) expressions[1];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
