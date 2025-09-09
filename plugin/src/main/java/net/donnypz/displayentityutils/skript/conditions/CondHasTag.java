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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayAnimation;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.SpawnedDisplayAnimation;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Group/Animation Has Tag?")
@Description("Check if a saved or spawned group/animation has a tag")
@Examples({"if {_savedgroup} has tag:", "\tbroadcast \"This group has a tag!\"",
        "",
        "if {_animation} doesn't have a tag:", "\tbroadcast \"This animation is tagless!\""})
@Since({"2.6.2"})
public class CondHasTag extends Condition {

    static {
        Skript.registerCondition(CondHasTag.class, "%activegroup/savedgroup/animation% (1¦has|2¦(has no|does(n't| not) have)) [a] tag");
    }

    Expression<?> object;

    @Override
    public boolean check(Event event) {
        Object obj = object.getSingle(event);
        if (obj instanceof ActiveGroup<?> g){
            return (g.getTag() != null) == isNegated();
        }
        else if (obj instanceof DisplayEntityGroup g){
            return (g.getTag() != null) == isNegated();
        }
        else if (obj instanceof SpawnedDisplayAnimation a){
            return (a.getAnimationTag() != null) == isNegated();
        }
        return isNegated();

    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "Has Tag: "+object.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
