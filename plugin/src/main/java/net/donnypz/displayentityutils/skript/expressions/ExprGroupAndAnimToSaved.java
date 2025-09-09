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
import net.donnypz.displayentityutils.utils.DisplayEntities.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group or Animation to Saved")
@Description("Get a saved group/animation from a active group or animation")
@Examples({"set {_savedgroup} to {_spawnedgroup} as saved group",
        "set {_savedanim} to {_spawnedanim} as saved animation",
        "#3.3.1",
        "set {_savedgroup} to {_packetgroup} as saved group"})
@Since("2.6.2")
public class ExprGroupAndAnimToSaved extends SimpleExpression<Object> {

    static{
        Skript.registerExpression(ExprGroupAndAnimToSaved.class, Object.class, ExpressionType.SIMPLE, "%spawnedgroup/packetgroup/spawnedanimation% as saved[ |-](group|anim[ation])");
    }

    Expression<?> object;

    @Override
    protected Object @Nullable [] get(Event event) {
        Object o = object.getSingle(event);
        if (o instanceof ActiveGroup<?> ag){
            return new DisplayEntityGroup[]{ag.toDisplayEntityGroup()};
        }
        if (o instanceof SpawnedDisplayAnimation anim){
            DisplayAnimation a = anim.toDisplayAnimation();
            return new DisplayAnimation[]{a};
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return object.toString(event,debug)+" to saved type";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        return true;
    }
}
