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
import net.donnypz.displayentityutils.utils.DisplayEntities.ActiveGroup;
import net.donnypz.displayentityutils.utils.DisplayEntities.DisplayEntityGroup;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Active Group to Saved")
@Description("Get a saved group from a active group")
@Examples({"set {_savedgroup} to {_spawnedgroup} as saved group",
            "set {_savedgroup} to {_packetgroup} as saved group"})
@Since("3.3.1")
public class ExprGroupAndAnimToSaved extends SimpleExpression<Object> {

    static{
        Skript.registerExpression(ExprGroupAndAnimToSaved.class, Object.class, ExpressionType.COMBINED, "%spawnedgroup/packetgroup% as saved[ |-]group");
    }

    private Expression<?> object;

    @Override
    protected Object @Nullable [] get(Event event) {
        Object o = object.getSingle(event);
        if (o instanceof ActiveGroup<?> ag){
            return new DisplayEntityGroup[]{ag.toDisplayEntityGroup()};
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
        return object.toString(event,debug)+" to saved group";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        object = expressions[0];
        return true;
    }
}
